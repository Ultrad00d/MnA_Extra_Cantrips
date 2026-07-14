package net.ultrad00d.ForgottenCantrips.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class BubbleUpEffect extends MobEffect {
    private static final Map<UUID, Float> BUBBLE_SPEED = new HashMap<>();
    private static final Map<UUID, Boolean> WAS_IN_WATER = new HashMap<>();

    public BubbleUpEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x66CCFF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return;

        Level level = player.level();
        UUID id = player.getUUID();
        if (!player.isInWater()) {
            if (WAS_IN_WATER.getOrDefault(id, false)) {
                Vec3 m = player.getDeltaMovement();
                player.setDeltaMovement(m.x, 0.8F, m.z);
            }
            if (!level.isClientSide()) {
                player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            }
            BUBBLE_SPEED.remove(id);
            WAS_IN_WATER.remove(id);
            return;
        }

        float speed = BUBBLE_SPEED.getOrDefault(id, 0.01F) + 0.01F;
        BUBBLE_SPEED.put(id, speed);

        Vec3 m = player.getDeltaMovement();
        player.setDeltaMovement(m.x, speed, m.z);

        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            double x = player.getX();
            double y = player.getY() + 0.5;
            double z = player.getZ();
            serverLevel.sendParticles(ParticleTypes.BUBBLE, x, y, z, 6, 0.4, 0.8, 0.4, 0.02);
            serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x, y - 0.3, z, 3, 0.3, 0.6, 0.3, 0.05);

            if (player.tickCount % 5 == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, player.getSoundSource(), 0.6F, 1.0F + 0.2F * level.random.nextFloat());
            }
        }

        WAS_IN_WATER.put(id, true);
    }
}