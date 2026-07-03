package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class OldWizard extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache CACHE = new SingletonAnimatableInstanceCache(this);

    public OldWizard(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 1D)
                .add(Attributes.JUMP_STRENGTH, 0.5D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), -0.5D)
                .build();
    }

    @Override
    protected void registerGoals() {
        // this.goalSelector.addGoal(1, new );
        // add Old Wizard behaviour here
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "idle", 0, this::idle));
    }

    private PlayState idle(AnimationState<OldWizard> animationState) {
        int variant = (int) (Math.random() * 4);
        switch (variant) {
            case 1 -> { return animationState.setAndContinue(RawAnimation.begin().then("animation.old_wizard.idle1", Animation.LoopType.PLAY_ONCE)); }
            case 2 -> { return animationState.setAndContinue(RawAnimation.begin().then("animation.old_wizard.idle2", Animation.LoopType.PLAY_ONCE)); }
            case 3 -> { return animationState.setAndContinue(RawAnimation.begin().then("animation.old_wizard.idle3", Animation.LoopType.PLAY_ONCE)); }
            default -> { return animationState.setAndContinue(RawAnimation.begin().then("animation.old_wizard.idle", Animation.LoopType.PLAY_ONCE)); }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return CACHE;
    }
}
