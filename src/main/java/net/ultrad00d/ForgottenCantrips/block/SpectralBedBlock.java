package net.ultrad00d.ForgottenCantrips.block;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.mojang.datafixers.util.Either;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;

public class SpectralBedBlock extends BedBlock {
    public SpectralBedBlock() {
        super(DyeColor.CYAN, BlockBehaviour.Properties.of().mapColor((p_284863_) -> {
            return p_284863_.getValue(BedBlock.PART) == BedPart.FOOT ? MapColor.COLOR_PURPLE : MapColor.COLOR_CYAN;
        }).sound(SoundType.AMETHYST).strength(0.2F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SpectralBedBlockEntity(pos, state); }
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (state.getValue(PART) != BedPart.HEAD) {
                pos = pos.relative(state.getValue(FACING));
                state = level.getBlockState(pos);
                if (!state.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }

            if (!canSetSpawn(level)) {
                level.removeBlock(pos, false);
                BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
                if (level.getBlockState(blockpos).is(this)) {
                    level.removeBlock(blockpos, false);
                }

                Vec3 vec3 = pos.getCenter();
                level.explode(null, level.damageSources().badRespawnPointExplosion(vec3), null, vec3, 5.0F, true, Level.ExplosionInteraction.BLOCK);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(OCCUPIED)) {
                if (!this.kickVillagerOutOfBed(level, pos)) {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            } else {
                if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

                sleepNoSpawnpoint(serverPlayer, pos).ifLeft((result) -> {
                    if (result.getMessage() != null) {
                        player.displayClientMessage(result.getMessage(), true);
                    }
                });

                return InteractionResult.SUCCESS;
            }
        }
    }

    public static Either<Player.BedSleepingProblem, Unit> sleepNoSpawnpoint(ServerPlayer player, @NotNull BlockPos at) {
        Player.BedSleepingProblem ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, Optional.of(at));
        if (ret != null) return Either.left(ret);

        Direction direction = player.level().getBlockState(at).getValue(HorizontalDirectionalBlock.FACING);
        if (!player.isSleeping() && player.isAlive()) {
            if (!player.level().dimensionType().natural()) {
                return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
            } else if (!bedInRange(player, at, direction)) {
                return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
            } else if (bedBlocked(player, at, direction)) {
                return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
            } else {
                if (!net.minecraftforge.event.ForgeEventFactory.fireSleepingTimeCheck(player, Optional.of(at))) {
                    return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
                } else {
                    if (!player.isCreative()) {
                        double d0 = 8.0D;
                        double d1 = 5.0D;
                        Vec3 vec3 = Vec3.atBottomCenterOf(at);
                        List<Monster> list = player.level().getEntitiesOfClass(Monster.class, new AABB(vec3.x() - d0, vec3.y() - d1, vec3.z() - d0, vec3.x() + d0, vec3.y() + d1, vec3.z() + d0), (p_9062_) -> p_9062_.isPreventingPlayerRest(player));
                        if (!list.isEmpty()) {
                            return Either.left(Player.BedSleepingProblem.NOT_SAFE);
                        }
                    }

                    Either<Player.BedSleepingProblem, Unit> either = startSleepInBed(player, at).ifRight((p_9029_) -> {
                        player.awardStat(Stats.SLEEP_IN_BED);
                        CriteriaTriggers.SLEPT_IN_BED.trigger(player);
                    });
                    if (!player.serverLevel().canSleepThroughNights()) {
                        player.displayClientMessage(Component.translatable("sleep.not_possible"), true);
                    }

                    ((ServerLevel)player.level()).updateSleepingPlayerList();
                    return either;
                }
            }
        } else {
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
        }
    }

    private static boolean bedInRange(ServerPlayer playerEntity, BlockPos blockPos, Direction direction) {
        if (direction == null) {
            return false;
        } else {
            return isReachableBedBlock(playerEntity, blockPos) || isReachableBedBlock(playerEntity, blockPos.relative(direction.getOpposite()));
        }
    }

    public static Either<Player.BedSleepingProblem, Unit> startSleepInBed(Player player, BlockPos pBedPos) {
        player.startSleeping(pBedPos);
        return Either.right(Unit.INSTANCE);
    }

    private static boolean bedBlocked(ServerPlayer playerEntity, BlockPos blockPos, Direction direction) {
        BlockPos blockpos = blockPos.above();
        return isAbnormalCube(playerEntity.level(), blockpos) || isAbnormalCube(playerEntity.level(), blockpos.relative(direction.getOpposite()));
    }

    private static boolean isAbnormalCube(Level world, BlockPos pos) {
        return world.getBlockState(pos).isSuffocating(world, pos);
    }

    private static boolean isReachableBedBlock(ServerPlayer playerEntity, BlockPos blockPos) {
        Vec3 vector3d = Vec3.atBottomCenterOf(blockPos);
        return Math.abs(playerEntity.getX() - vector3d.x()) <= (double)3.0F && Math.abs(playerEntity.getY() - vector3d.y()) <= (double)2.0F && Math.abs(playerEntity.getZ() - vector3d.z()) <= (double)3.0F;
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos blockPos) {
        List<Villager> list = level.getEntitiesOfClass(Villager.class, new AABB(blockPos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.get(0).stopSleeping();
            return true;
        }
    }
}
