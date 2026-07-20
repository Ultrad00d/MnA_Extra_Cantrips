package net.ultrad00d.ForgottenCantrips.cantrip;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.util.ProgressionUtil;

public interface IEmpowerCantripLogic {
    default void applyBuff(Player player, RegistryObject<MobEffect> effect) {
        if (player.level().isClientSide()) return;

        BuffTierRules rules = getBuffTierRules(player);
        MobEffect mobEffect = effect.get();
        MobEffectInstance current = player.getEffect(mobEffect);

        enforceActiveBuffLimit(player, mobEffect, rules.maxActiveBuffs);

        int amplifier;
        if (current == null)
        {
            amplifier = 0;
        }
        else
        {
            amplifier = current.getAmplifier() + 1;
        }

        amplifier = Math.min(amplifier, rules.maxAmplifier);

        player.addEffect(new MobEffectInstance(mobEffect, rules.durationTicks, amplifier, false, false, true));
    }

    default BuffTierRules getBuffTierRules(Player player) {
        int tier = ProgressionUtil.getPlayerTier(player);

        return switch (tier) {
            case 3 -> new BuffTierRules(20 * 20, 2, 0);
            case 4 -> new BuffTierRules(20 * 30, 2, 1);
            case 5 -> new BuffTierRules(20 * 60, 3, 2);
            default -> new BuffTierRules(20 * 15, 1, 0);
        };
    }

    default void enforceActiveBuffLimit(Player player, MobEffect buffToApply, int maxActiveBuffs) {

        int activeBuffs = countActiveForgottenBuffs(player, buffToApply);

        while (activeBuffs >= maxActiveBuffs) {
            MobEffect buffToRemove = findOldestOtherForgottenBuff(player, buffToApply);
            if (buffToRemove == null) return;

            player.removeEffect(buffToRemove);
            activeBuffs--;
        }
    }

    default int countActiveForgottenBuffs(Player player, MobEffect buffToApply) {
        int activeBuffs = 0;
        if (player.hasEffect(buffToApply)) {
            activeBuffs = 1;
        }

        for (MobEffect buff : getForgottenBuffs()) {
            if (buff != buffToApply && player.hasEffect(buff)) {
                activeBuffs++;
            }
        }

        return activeBuffs;
    }

    default MobEffect findOldestOtherForgottenBuff(Player player, MobEffect buffToApply) {
        MobEffect oldestBuff = null;
        int shortestDuration = Integer.MAX_VALUE;

        for (MobEffect buff : getForgottenBuffs()) {
            if (buff == buffToApply) continue;

            MobEffectInstance instance = player.getEffect(buff);
            if (instance != null && instance.getDuration() < shortestDuration) {
                oldestBuff = buff;
                shortestDuration = instance.getDuration();
            }
        }

        return oldestBuff;
    }

    default MobEffect[] getForgottenBuffs() {
        return new MobEffect[] {
                EffectRegistry.EMPOWER_DAMAGE_BUFF.get(),
                EffectRegistry.EMPOWER_MANA_COST_BUFF.get(),
                EffectRegistry.EMPOWER_CANTRIP_BUFF.get()
        };
    }

    record BuffTierRules(int durationTicks, int maxActiveBuffs, int maxAmplifier) {}
}
