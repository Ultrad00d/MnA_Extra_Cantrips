package net.ultrad00d.ForgottenCantrips.registry;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.tools.RLoc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;
import static net.minecraft.resources.ResourceLocation.of;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.ultrad00d.ForgottenCantrips.blockentity.SpectralBedBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;

public class CantripRegistry {
    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                CantripRegistry::lightning,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                CantripRegistry::placeBed,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                CantripRegistry::summonBoat,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Donkey Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_donkey"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_donkey.png"),
                1,
                CantripRegistry::summonDonkey,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
    }

    public static void lightning(Player player, ICantrip cantrip, InteractionHand hand) {
        double range;
        try {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } catch (Throwable var14) {
            range = 4.5F;
        }
        Vec3 target = player.pick(range, 0.0F, true).getLocation();
        Level level = player.level();
        if (!level.canSeeSky(new BlockPos((int) target.x, (int) target.y, (int) target.z))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.lightning.obstructed"));
            return;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(target);
                lightning.setCause((ServerPlayer) player);
                serverLevel.addFreshEntity(lightning);
            }
        }
    }

    public static void placeBed(Player player, ICantrip cantrip, InteractionHand hand) {

        HitResult rayHit = player.pick(player.getBlockReach(), 0.0F, false);

        if (rayHit.getType() != BlockHitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.toofar"));
            return;
        }

        long dayTime = player.level().getDayTime() % 24000L;
        boolean isDaytime = dayTime < 13000L;

        if (player.level().dimensionType().bedWorks()) {

            if (isDaytime && !(player.level().isThundering())) {
                player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtime"));
                return;
            }
        }


        BlockPos targetBlock = ((BlockHitResult) rayHit).getBlockPos();

        BlockPos footBlockPos, headBlockPos;
        // if player is looking at the top of the targeted block, simply try placing the bed on top of that block
        if (((BlockHitResult) rayHit).getDirection() == Direction.UP) {
            footBlockPos = targetBlock.above();
        }
        // if the player is looking at the bottom of the targeted block, try placing the bed below the block
        else if (((BlockHitResult) rayHit).getDirection() == Direction.DOWN) {
            footBlockPos = targetBlock.below();
        } else {
            //otherwise, player is looking at the side of the block, so try placing the bed one block in that direction
            footBlockPos = targetBlock.relative(((BlockHitResult) rayHit).getDirection(), 1);
        }

        headBlockPos = footBlockPos.relative(player.getDirection(), 1);

        if (!(player.level().getBlockState(footBlockPos.below()).isSolid())) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtarget"));
            return;
        }

        if (!((player.level().getBlockState(footBlockPos).isAir()) && (player.level().getBlockState(headBlockPos).isAir()))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.nospace"));
            return;
        }



        BedBlock b1 = (BedBlock) BlockRegistry.SPECTRAL_BED.get();
        BlockState bedState = b1
                .defaultBlockState()
                .setValue(BedBlock.FACING, player.getDirection())
                .setValue(BedBlock.PART, BedPart.HEAD);

        BlockState footState = bedState.setValue(BedBlock.PART, BedPart.FOOT);

        player.level().setBlock(headBlockPos, bedState, 3);
        player.level().setBlock(footBlockPos, footState, 3);

        BlockEntity bedBlockEntity = new SpectralBedBlockEntity(headBlockPos, bedState);
        player.level().setBlockEntity(bedBlockEntity);
    }
    public static void summonBoat(Player player, ICantrip cantrip, InteractionHand hand)
    {
        double range;
        try
        {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } 
        catch (Throwable var14)
        {
            return;
        }

        Level level = player.level();
        Vec3 target = player.pick(range, 0.0F, true).getLocation();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel)
        {
            SpectralBoat boat = EntityRegistry.SPECTRAL_BOAT.get().create(serverLevel);
            if (boat != null)
            {
                boat.moveTo(target.x, target.y, target.z, player.getYRot(), 0.0F);
                boat.setOwnerUUID(player.getUUID());
                serverLevel.addFreshEntity(boat);
            }
        }
    }

    public static void summonDonkey(Player player, ICantrip cantrip, InteractionHand hand) {
        HitResult hitResult = player.pick(player.getBlockReach(), 0.0F, false);

//        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
//            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_donkey.toofar"));
//            return;
//        }

        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 target = hitResult.getLocation();
            SpectralDonkey spectralDonkey = EntityRegistry.SPECTRAL_DONKEY.get().create(serverLevel);
            if (spectralDonkey != null) {
                DifficultyInstance difficultyInstance = serverLevel.getCurrentDifficultyAt(new BlockPos((int) target.x, (int) target.y, (int) target.z));
                spectralDonkey.finalizeSpawn(serverLevel, difficultyInstance, MobSpawnType.TRIGGERED, null, null);
                spectralDonkey.setOwnerUUID(player.getUUID());
                spectralDonkey.invulnerableTime = 60;
                spectralDonkey.setAge(0);
                spectralDonkey.tameWithName(player);
                spectralDonkey.equipSaddle(null);
                spectralDonkey.moveTo(target.x, target.y, target.z);
                ItemStack offhandStack = player.getItemInHand(InteractionHand.MAIN_HAND.equals(hand) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                if (offhandStack.is(Items.GOLDEN_CARROT)) {
                    spectralDonkey.setPersistent(true);
                    offhandStack.shrink(1);
                }
                serverLevel.addFreshEntity(spectralDonkey);
            }
        }
    }
}
