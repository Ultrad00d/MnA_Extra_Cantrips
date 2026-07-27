package net.ultrad00d.ForgottenCantrips.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.util.BlockUtil;
import org.jetbrains.annotations.NotNull;

public class BubbleUpEffect extends MobEffect {
    private static final Map<UUID, Float> MAGICAL_SPEED = new HashMap<>();
    private static final Map<UUID, Boolean> HAS_SNEAKED = new HashMap<>();

    private static final float INITIAL_SPEED = 2.0F;
    private static final float MAX_SPEED = 18.0F;
    private static final float SNEAK_DECELERATION = -4.0F;
    private static final float JUMP_BOOST = 0.8F;

    public BubbleUpEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x66CCFF);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        // Get player UUID to store it into the map
        UUID id = player.getUUID();
        boolean inWater = player.isInWater();
        boolean inBubbleColumn = BlockUtil.isInBubbleColumn(player);

        // If at any time in bubble column -> effect wears off
        if (inBubbleColumn) {
            player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            player.connection.send(new ClientboundSoundPacket(
                    Holder.direct(SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED), SoundSource.PLAYERS,
                    player.getX(), player.getY(), player.getZ(),
                    0.4F, 0.4F,
                    serverLevel.getRandom().nextLong()
            ));
            cleanupPlayer(id);
            return;
        }

        // If the player left the water
        if (!inWater) {
            Vec3 movement = player.getDeltaMovement();

            player.setDeltaMovement(movement.x, JUMP_BOOST, movement.z);
            player.connection.send(new ClientboundSetEntityMotionPacket(player));

            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();

            serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 5, 0.3, 0.3, 0.3, 0.05);

            player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            cleanupPlayer(id);
            return;
        }

        // If the container is closed and the player's speed is not increasing
        Vec3 delta = player.getDeltaMovement();
        if (delta.y <= 0.01 && MAGICAL_SPEED.containsKey(id)) {
            player.removeEffect(EffectRegistry.BUBBLE_UP.get());
            cleanupPlayer(id);
            return;
        }

        float currentSpeed = MAGICAL_SPEED.getOrDefault(id, INITIAL_SPEED);

        // If during the effect player presses down Shift, then we decrease their ascend speed by SNEAK_DECELERATION
        // Else we increase their current speed with acceleration = 3m/s
        if (player.isShiftKeyDown()) HAS_SNEAKED.put(id, true);
        boolean hasSneaked = HAS_SNEAKED.getOrDefault(id, false);
        if (hasSneaked) {
            currentSpeed = Math.max(currentSpeed + SNEAK_DECELERATION * 0.05F, 0);
            if (currentSpeed <= 0) {
                player.removeEffect(EffectRegistry.BUBBLE_UP.get());
                cleanupPlayer(id);
                return;
            }
        } else {
            currentSpeed = Math.min(currentSpeed + 0.15F, MAX_SPEED);
        }

        // save current speed to use it later
        MAGICAL_SPEED.put(id, currentSpeed);

        // Apply speed to the player and send them a packet (since this is pure serverside)
        Vec3 movement = player.getDeltaMovement();
        double verticalBoost = currentSpeed * 0.05;
        if (movement.y < 0) verticalBoost = Math.max(verticalBoost + movement.y, 0);
        player.setDeltaMovement(movement.x, verticalBoost, movement.z);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));


        double x = player.getX();
        double y = player.getY() + 0.5;
        double z = player.getZ();
        int particleCount = Math.max(2, (int) (currentSpeed / 2));
        float particleSpread = currentSpeed / 10;

        serverLevel.sendParticles(ParticleTypes.BUBBLE, x, y, z,
                particleCount, particleSpread * 0.4, particleSpread * 0.8, particleSpread * 0.4, 0.02);
        serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x, y - 0.3, z,
                particleCount / 2, particleSpread * 0.3, particleSpread * 0.6, particleSpread * 0.3, 0.05);


        if (player.tickCount % 5 == 0) {
            player.connection.send(new ClientboundSoundPacket(
                    Holder.direct(SoundEvents.BUBBLE_COLUMN_BUBBLE_POP), SoundSource.PLAYERS,
                    player.getX(), player.getY(), player.getZ(),
                    0.6F, 1.0F + 0.2F * serverLevel.random.nextFloat(),
                    serverLevel.getRandom().nextLong()
            ));
        }
    }

    private void cleanupPlayer(UUID id) {
        MAGICAL_SPEED.remove(id);
        HAS_SNEAKED.remove(id);
    }
}