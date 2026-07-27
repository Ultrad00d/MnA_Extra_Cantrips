package net.ultrad00d.ForgottenCantrips.spell;

import com.mna.api.ManaAndArtificeMod;
import com.mna.api.spells.adjusters.SpellAdjustingContext;
import com.mna.api.spells.adjusters.SpellCastStage;
import com.mna.api.spells.attributes.Attribute;
import com.mna.api.spells.base.IModifiedSpellPart;
import com.mna.api.spells.parts.SpellEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class SpellBuffAdjusters
{
    private static final float DAMAGE_BONUS_PER_LEVEL = 0.10F;
    private static final float MANA_COST_REDUCTION_PER_LEVEL = 0.10F;

    public static void register()
    {
        if (ManaAndArtificeMod.getSpellHelper() == null)
        {
            ForgottenCantrips.LOGGER.warn("Mana and Artifice spell helper is not ready; skipping Forgotten Cantrips spell adjusters.");
            return;
        }

        ManaAndArtificeMod.registerSpellAdjuster(SpellBuffAdjusters::hasDmgBuff, SpellBuffAdjusters::applyDmgBuff);
        ManaAndArtificeMod.registerSpellAdjuster(SpellBuffAdjusters::hasManaCostBuff, SpellBuffAdjusters::applyManaCostBuff);
    }

    private static boolean hasDmgBuff(SpellAdjustingContext context)
    {
        return context.stage == SpellCastStage.CASTING && hasEffect(context.caster, EffectRegistry.EMPOWER_DAMAGE_BUFF.get());
    }

    private static boolean hasManaCostBuff(SpellAdjustingContext context)
    {
        return context.stage == SpellCastStage.CASTING && hasEffect(context.caster, EffectRegistry.EMPOWER_MANA_BUFF.get());
    }

    private static void applyDmgBuff(SpellAdjustingContext context)
    {
        int level = getEffectLevel(context.caster, EffectRegistry.EMPOWER_DAMAGE_BUFF.get());
        float damageMultiplier = 1.0F + DAMAGE_BONUS_PER_LEVEL * level;

        for (IModifiedSpellPart<SpellEffect> component : context.spell.getComponents())
        {
            if (component.getContainedAttributes().contains(Attribute.DAMAGE))
            {
                float damage = component.getValue(Attribute.DAMAGE);
                component.setValue(Attribute.DAMAGE, damage * damageMultiplier);
            }
        }
    }

    private static void applyManaCostBuff(SpellAdjustingContext context)
    {
        int level = getEffectLevel(context.caster, EffectRegistry.EMPOWER_MANA_BUFF.get());
        float multiplier = Math.max(0.0F, 1.0F - MANA_COST_REDUCTION_PER_LEVEL * level);
        float manaCost = context.spell.getManaCost();

        context.spell.setManaCost(manaCost * multiplier);
    }

    private static boolean hasEffect(LivingEntity entity, MobEffect effect)
    {
        return entity != null && entity.hasEffect(effect);
    }

    private static int getEffectLevel(LivingEntity entity, MobEffect effect)
    {
        if (entity == null)
        {
            return 0;
        }

        MobEffectInstance instance = entity.getEffect(effect);
        if (instance == null)
        {
            return 0;
        }

        return instance.getAmplifier() + 1;
    }
}
