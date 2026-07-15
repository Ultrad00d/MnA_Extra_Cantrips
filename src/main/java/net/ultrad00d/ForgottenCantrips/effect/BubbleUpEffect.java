package net.ultrad00d.ForgottenCantrips.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class BubbleUpEffect extends MobEffect {
    private static final Map<UUID, Float> MAGICAL_SPEED = new HashMap<>();
    private static final Map<UUID, Boolean> WAS_IN_WATER = new HashMap<>();
    private static final Map<UUID, Double> INITIAL_Y = new HashMap<>();
    private static final Map<UUID, Boolean> PRESSED_SHIFT = new HashMap<>();

    public BubbleUpEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x66CCFF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) {
            return;
        }

        Level level = player.level();
        UUID id = player.getUUID();
        boolean inWater = player.isInWater();
        boolean inBubbleColumn = isInBubbleColumn(player);

        if (inBubbleColumn && !level.isClientSide()) {
            player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            cleanupPlayer(id);
            return;
        }

        Vec3 delta = player.getDeltaMovement();
        if (delta.y <= 0.01 && inWater && MAGICAL_SPEED.containsKey(id)) {
            if (!level.isClientSide()) {
                player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            }
            cleanupPlayer(id);
            return;
        }

        if (inWater) {
            if (!MAGICAL_SPEED.containsKey(id)) {
                MAGICAL_SPEED.put(id, 2.0F);
                INITIAL_Y.put(id, player.getY());
            }

            float currentSpeed = MAGICAL_SPEED.getOrDefault(id, 2.0F);

            double waterCeilingY = findWaterCeiling(player);
            if (waterCeilingY >= 0 && player.getY() >= waterCeilingY - 0.5) {
                currentSpeed = 0;
                if (!level.isClientSide()) {
                    player.removeEffect(EffectRegistry.BUBBLE_UP.get());
                }
                cleanupPlayer(id);
                return;
            }

            if (player.isShiftKeyDown()) {
                PRESSED_SHIFT.put(id, true);
            }

            boolean hasSneaked = PRESSED_SHIFT.getOrDefault(id, false);

            if (hasSneaked) {
                currentSpeed = Math.max(currentSpeed - 2.0F, 0);
                if (currentSpeed <= 0) {
                    if (!level.isClientSide()) {
                        player.removeEffect(EffectRegistry.BUBBLE_UP.get());
                    }
                    cleanupPlayer(id);
                    return;
                }
            } else {
                currentSpeed = Math.min(currentSpeed + 0.15F, 18.0F);
            }

            MAGICAL_SPEED.put(id, currentSpeed);

            Vec3 movement = player.getDeltaMovement();

            double verticalBoost = currentSpeed * 0.05;

            if (movement.y < 0) {
                verticalBoost = Math.max(verticalBoost - Math.abs(movement.y), 0);
            }

            player.setDeltaMovement(movement.x, verticalBoost, movement.z);

            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.hurtMarked = true;

                if (level instanceof ServerLevel serverLevel) {
                    double x = player.getX();
                    double y = player.getY() + 0.5;
                    double z = player.getZ();

                    int particleCount = Math.max(2, (int) (currentSpeed / 2));
                    float particleSpread = currentSpeed / 10;

                    serverLevel.sendParticles(ParticleTypes.BUBBLE, x, y, z,
                            particleCount, particleSpread * 0.4, particleSpread * 0.8, particleSpread * 0.4, 0.02);
                    serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x, y - 0.3, z,
                            particleCount / 2, particleSpread * 0.3, particleSpread * 0.6, particleSpread * 0.3, 0.05);
                }

                if (player.tickCount % 5 == 0) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, player.getSoundSource(), 0.6F, 1.0F + 0.2F * level.random.nextFloat());
                }
            }
        }
        else {
            if (WAS_IN_WATER.getOrDefault(id, false)) {
                Vec3 movement = player.getDeltaMovement();

                double jumpBoost = 0.8F;
                player.setDeltaMovement(movement.x, jumpBoost, movement.z);

                if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.hurtMarked = true;

                    if (level instanceof ServerLevel serverLevel) {
                        double x = player.getX();
                        double y = player.getY();
                        double z = player.getZ();

                        serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 5, 0.3, 0.3, 0.3, 0.05);
                    }
                }
            }

            if (!level.isClientSide()) {
                player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            }
            cleanupPlayer(id);
            return;
        }

        WAS_IN_WATER.put(id, inWater);
    }

    private boolean isInBubbleColumn(Player player) {
        BlockPos pos = player.blockPosition();
        return player.level().getBlockState(pos).is(Blocks.BUBBLE_COLUMN) ||
                player.level().getBlockState(pos.above()).is(Blocks.BUBBLE_COLUMN);
    }

    private double findWaterCeiling(Player player) {
        BlockPos playerPos = player.blockPosition();
        Level level = player.level();
        int startY = playerPos.getY();

        for (int y = startY; y < startY + 256; y++) {
            if (y >= level.getMaxBuildHeight()) {
                return level.getMaxBuildHeight();
            }

            BlockPos checkPos = new BlockPos(playerPos.getX(), y, playerPos.getZ());

            boolean isCurrentWater = level.getBlockState(checkPos).getBlock() instanceof LiquidBlock;
            boolean isCurrentAir = level.getBlockState(checkPos).isAir();

            if (isCurrentWater) {
                continue;
            }

            if (isCurrentAir) {
                return -1;
            }

            boolean belowIsWater = level.getBlockState(checkPos.below()).getBlock() instanceof LiquidBlock;
            if (belowIsWater) {
                return checkPos.getY();
            }
        }

        return -1;
    }

    private void cleanupPlayer(UUID id) {
        MAGICAL_SPEED.remove(id);
        WAS_IN_WATER.remove(id);
        INITIAL_Y.remove(id);
        PRESSED_SHIFT.remove(id);
    }
}