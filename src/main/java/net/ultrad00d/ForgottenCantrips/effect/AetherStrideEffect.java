package net.ultrad00d.ForgottenCantrips.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

public class AetherStrideEffect extends MobEffect {
    public AetherStrideEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xBBCCFF);
        this.addAttributeModifier(ForgeMod.STEP_HEIGHT_ADDITION.get(), 
            "7c8a7b3e-1a2b-4c3d-8e5f-6a7b8c9d0e1f", 
            1.0D, 
            AttributeModifier.Operation.ADDITION);

    }
}