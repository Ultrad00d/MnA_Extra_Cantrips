package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.capabilities.IPlayerProgression;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public abstract class EmpowerCantripLogic extends CantripLogic
{
    protected static void applyBuff(Player player, RegistryObject<MobEffect> effect)
    {
        if (player.level().isClientSide())
        {
            return;
        }

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

    protected static BuffTierRules getBuffTierRules(Player player)
    {
        int tier = getPlayerTier(player);

        switch (tier)
        {
            case 3:
                return new BuffTierRules(20 * 20, 2, 0);
            case 4:
                return new BuffTierRules(20 * 30, 2, 1);
            case 5:
                return new BuffTierRules(20 * 60, 3, 2);
            case 2:
            default:
                return new BuffTierRules(20 * 15, 1, 0);
        }
    }

    protected static int getPlayerTier(Player player)
    {
        IPlayerProgression progression = player.getCapability(ManaAndArtificeMod.getProgressionCapability()).orElse(null);

        if (progression == null)
        {
            return 0;
        }

        return progression.getTier();
    }

    protected static void enforceActiveBuffLimit(Player player, MobEffect buffToApply, int maxActiveBuffs)
    {
        int activeBuffs = countActiveForgottenBuffs(player, buffToApply);

        while (activeBuffs >= maxActiveBuffs)
        {
            MobEffect buffToRemove = findOldestOtherForgottenBuff(player, buffToApply);
            if (buffToRemove == null)
            {
                return;
            }

            player.removeEffect(buffToRemove);
            activeBuffs--;
        }
    }

    protected static int countActiveForgottenBuffs(Player player, MobEffect buffToApply)
    {
        int activeBuffs = 0;
        if (player.hasEffect(buffToApply))
        {
            activeBuffs = 1;
        }

        for (MobEffect buff : getForgottenBuffs())
        {
            if (buff != buffToApply && player.hasEffect(buff))
            {
                activeBuffs++;
            }
        }

        return activeBuffs;
    }

    protected static MobEffect findOldestOtherForgottenBuff(Player player, MobEffect buffToApply)
    {
        MobEffect oldestBuff = null;
        int shortestDuration = Integer.MAX_VALUE;

        for (MobEffect buff : getForgottenBuffs())
        {
            if (buff == buffToApply)
            {
                continue;
            }

            MobEffectInstance instance = player.getEffect(buff);
            if (instance != null && instance.getDuration() < shortestDuration)
            {
                oldestBuff = buff;
                shortestDuration = instance.getDuration();
            }
        }

        return oldestBuff;
    }

    protected static MobEffect[] getForgottenBuffs()
    {
        return new MobEffect[] {
                EffectRegistry.DMG_BUFF.get(),
                EffectRegistry.MANA_COST_BUFF.get(),
                EffectRegistry.CANTRIP_BUFF.get()
        };
    }

    protected static class BuffTierRules
    {
        private final int durationTicks;
        private final int maxActiveBuffs;
        private final int maxAmplifier;

        private BuffTierRules(int durationTicks, int maxActiveBuffs, int maxAmplifier)
        {
            this.durationTicks = durationTicks;
            this.maxActiveBuffs = maxActiveBuffs;
            this.maxAmplifier = maxAmplifier;
        }
    }
}
