package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class OldWizard extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache CACHE = new SingletonAnimatableInstanceCache(this);
    public boolean isHeadLocked = true;
    public boolean isBookShown = false;
    public boolean isReading = false;

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.idle");
    private static final RawAnimation IDLE1_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.idle1");
    private static final RawAnimation IDLE2_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.idle2");
    private static final RawAnimation IDLE3_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.idle3");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("animation.old_wizard.walk");
    private static final RawAnimation RUN_ANIMATION = RawAnimation.begin().thenLoop("animation.old_wizard.run");
    private static final RawAnimation SPELL_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.spell");
    private static final RawAnimation ANGER_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.anger");
    private static final RawAnimation SHAKING_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.shaking");
    private static final RawAnimation GROW1_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.grow_1");
    private static final RawAnimation GROW2_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.grow_2");
    private static final RawAnimation SHOW_BOOK_ANIMATION = RawAnimation.begin().thenPlay("animation.old_wizard.book");
    private static final RawAnimation READ_BOOK_ANIMATION = RawAnimation.begin().thenLoop("animation.old_wizard.read");

    private RawAnimation currentIdleAnimation = IDLE_ANIMATION;
    private RawAnimation currentBookAnimation = SHOW_BOOK_ANIMATION;

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
        controllerRegistrar.add(new AnimationController<>(this, "bodyController", 5, this::body));

        controllerRegistrar.add(new AnimationController<>(this, "bookController", 0, this::book)
                .triggerableAnim("show_book", SHOW_BOOK_ANIMATION)
                .triggerableAnim("read_book", READ_BOOK_ANIMATION)
        );
    }

    // currently does not produce wanted results
    private PlayState body(AnimationState<OldWizard> animationState) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(WALK_ANIMATION);
            return PlayState.CONTINUE;
        }

        if (animationState.getController().hasAnimationFinished()) {
            this.pickRandomIdle();
            animationState.getController().forceAnimationReset();
        }

        animationState.getController().setAnimation(this.currentIdleAnimation);
        return PlayState.CONTINUE;
    }

    private void pickRandomIdle() {
        RandomSource random = this.getRandom();
        switch (random.nextInt(4)) {
            case 0 -> this.currentIdleAnimation = IDLE_ANIMATION;
            case 1 -> this.currentIdleAnimation = IDLE1_ANIMATION;
            case 2 -> this.currentIdleAnimation = IDLE2_ANIMATION;
            case 3 -> this.currentIdleAnimation = IDLE3_ANIMATION;
        }
    }

    // currently does not produce wanted results
    private PlayState book(AnimationState<OldWizard> animationState) {
        if (!isBookShown) {
            animationState.getController().forceAnimationReset();
            AnimatableManager<OldWizard> manager = getAnimatableInstanceCache().getManagerForId(this.getId());
            CoreGeoBone bookBone = manager.getBoneSnapshotCollection().get("book").getBone();
            if (bookBone != null) {
                bookBone.setScaleX(0.0F);
                bookBone.setScaleY(0.0F);
                bookBone.setScaleZ(0.0F);
            }
            return PlayState.STOP;
        }

        if (animationState.getController().hasAnimationFinished()) {
            triggerAnim("bookController", "read_book");
        }
//        return animationState.setAndContinue(isReading ? READ_BOOK_ANIMATION : SHOW_BOOK_ANIMATION);
        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            this.isBookShown = !this.isBookShown;
            this.isHeadLocked = this.isBookShown;
            if (!isBookShown) { isReading = false; }
        }
        return super.interactAt(player, hitPos, hand);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return CACHE; }
}
