package net.ultrad00d.ForgottenCantrips.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class UndyingEffect extends MobEffect {
    public UndyingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700);
    }
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(EffectRegistry.UNDYING.get());
        if (effect == null) return;

        if (entity.getHealth() - event.getAmount() > 1e-5) return;

        event.setCanceled(true);
        entity.removeEffect(EffectRegistry.UNDYING.get());
        entity.setHealth(1.0F);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 25, 2));
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F);

        if (entity instanceof ServerPlayer sp) {
            var packet = new ClientboundLevelParticlesPacket(
                ParticleTypes.TOTEM_OF_UNDYING, true,
                sp.getX(), sp.getY() + 1.0, sp.getZ(),
                0.7f, 1.2f, 0.7f, 0.7f, 60
            );
            sp.connection.send(packet);
        }
    }
}