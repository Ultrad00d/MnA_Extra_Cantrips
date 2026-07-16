package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.ultrad00d.ForgottenCantrips.dialogue.DialogueChoice;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogue;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogueProvider;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardGlobalState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class OldWizard extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache CACHE = new SingletonAnimatableInstanceCache(this);
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return CACHE; }

    // --- SYNCHRONIZED ENTITY DATA ACCESSORS ---
    private static final EntityDataAccessor<Boolean> HEAD_LOCKED = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WIZARD_ANIMATION_STATE = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WIZARD_FRIENDSHIP_STATE = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FRIENDSHIP_STATE_TIMER = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);
    // client side tickers
    private int idleTickCooldown = 80;
    private boolean isWaitingForNextIdle = true;

    // --- FRIENDSHIP STATES ---
    private static final int STATE_FRIENDLY = 0;
    private static final int STATE_NOT_FRIENDLY = 1;
    private static final int STATE_DISAPPOINTED = 2;

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAD_LOCKED, true);
        this.entityData.define(WIZARD_ANIMATION_STATE, 0);
        this.entityData.define(WIZARD_FRIENDSHIP_STATE, STATE_FRIENDLY);
        this.entityData.define(FRIENDSHIP_STATE_TIMER, 0);
    }

    // --- GECKOLIB ANIMATION PATHS ---
    private static final String STILL_ANIMATION = "animation.old_wizard.still";
    private static final String IDLE1_ANIMATION = "animation.old_wizard.idle1";
    private static final String IDLE2_ANIMATION = "animation.old_wizard.idle2";
    private static final String IDLE3_ANIMATION = "animation.old_wizard.idle3";
    private static final String IDLE4_ANIMATION = "animation.old_wizard.idle4";
    private static final String WALK_ANIMATION = "animation.old_wizard.walk";
    private static final String RUN_ANIMATION = "animation.old_wizard.run";
    private static final String SPELL_ANIMATION = "animation.old_wizard.spell";
    private static final String ANGER_ANIMATION = "animation.old_wizard.anger";
    private static final String SHAKING_ANIMATION = "animation.old_wizard.shaking";
    private static final String GROW1_ANIMATION = "animation.old_wizard.grow_1";
    private static final String GROW2_ANIMATION = "animation.old_wizard.grow_2";
    private static final String SHOW_BOOK_ANIMATION = "animation.old_wizard.book";
    private static final String READ_BOOK_ANIMATION = "animation.old_wizard.read";

    private String currentIdleAnimation = STILL_ANIMATION;


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

    @Override public boolean canChangeDimensions() { return false; }

    @Override
    protected void registerGoals() {
        // this.goalSelector.addGoal(1, new );
        // TODO: add Old Wizard behaviour here
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide()) {
            // Manage state timers
            int friendshipStateTimer = this.getFriendshipStateTimer();
            if (friendshipStateTimer > 0) {
                this.setFriendshipStateTimer(--friendshipStateTimer);
                if (friendshipStateTimer == 0) {
                    handleFriendshipStateTransition();
                }
            }
        } else {
            if (this.idleTickCooldown > 0) {
                this.idleTickCooldown--;
                if (idleTickCooldown % 10 == 0) System.out.println("Idle Cooldown: " + this.idleTickCooldown);
            }
        }
    }

    private void handleFriendshipStateTransition() {
        int currentState = this.getWizardFriendshipState();
        if (currentState == STATE_DISAPPOINTED) {
            this.setWizardFriendshipState(STATE_NOT_FRIENDLY);
            this.setFriendshipStateTimer(12000);
        } else if (currentState == STATE_NOT_FRIENDLY) {
            this.setWizardFriendshipState(STATE_FRIENDLY);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        AnimationController<OldWizard> controller = new AnimationController<>(this, this::handleAnimationState);
        controllerRegistrar.add(controller.transitionLength(5));
    }

    private PlayState handleAnimationState(AnimationState<OldWizard> animationState) {
        RawAnimation builder = RawAnimation.begin();

        if (this.getDeltaMovement().add(0.0F, -this.getDeltaMovement().y, 0.0F).length() >= 0.1) {
            builder.thenPlay(WALK_ANIMATION);
        } else {
            Action action = getAnimationState();
            switch (action) {
                case SPELL -> {
                    builder.thenPlay(SPELL_ANIMATION);
                }
                case SHOW_BOOK -> {
                    if (animationState.getController().hasAnimationFinished()) {
                        builder.thenPlay(READ_BOOK_ANIMATION);
                        this.setAnimationState(Action.READING);
                    }
                    builder.thenPlay(SHOW_BOOK_ANIMATION);
                }
                case READING -> {
                    builder.thenPlay(READ_BOOK_ANIMATION);
                }
                default -> {
                    if (this.idleTickCooldown > 0) {
                        this.currentIdleAnimation = STILL_ANIMATION;
                    } else if (this.isWaitingForNextIdle) {
                        this.currentIdleAnimation = pickRandomIdle();
                        System.out.println(this.currentIdleAnimation);
                        this.isWaitingForNextIdle = false;
                    } else if (animationState.getController().hasAnimationFinished()) {
                        this.idleTickCooldown = 80;
                        this.isWaitingForNextIdle = true;
                        this.currentIdleAnimation = STILL_ANIMATION;
                    }

                    builder.thenPlay(currentIdleAnimation);
                }
            }
        }

        return animationState.setAndContinue(builder);
    }

    public enum Action {
        IDLE,
        SPELL,
        SHOW_BOOK,
        READING
    }

    private String pickRandomIdle() {
        RandomSource random = this.getRandom();
        int roll = random.nextInt(100);

        if (roll < 40) {
            return IDLE1_ANIMATION; // 40% chance
        } else if (roll < 60) {
            return IDLE2_ANIMATION; // 20% chance
        } else if (roll < 80) {
            return IDLE3_ANIMATION; // 20% chance
        } else {
            return IDLE4_ANIMATION; // 20% chance
        }
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            // Shift Right Click TODO: make just RCM
            if (player.isSecondaryUseActive()) {
                player.getCapability(WizardDialogueProvider.WIZARD_DIALOGUE_CAP).ifPresent(cap -> {
                    if (cap.getGlobalState() == WizardGlobalState.NOT_MET) {
                        WizardDialogue.sendWizardReply(player,
                                cap,
                                "intro.1",
                                DialogueChoice.CONTINUE,
                                DialogueChoice.BYE
                        );
                        return;
                    }

                    WizardDialogue.sendWizardReply(player,
                            cap,
                            "back_again.1",
                            DialogueChoice.CONTINUE,
                            DialogueChoice.BYE
                    );
                });
                return InteractionResult.SUCCESS;
            }

            // Right Click
            this.setAnimationState(getAnimationState() == Action.SHOW_BOOK || getAnimationState() == Action.READING ? Action.IDLE : Action.SHOW_BOOK);
            player.sendSystemMessage(Component.literal("Book shown?: " + this.getAnimationState()));

            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.GENERIC_KILL)) {
            return super.hurt(source, amount);
        }

        if (source.getEntity() instanceof LivingEntity attacker) {
            this.setTarget(attacker);
            this.setAggressive(true);
            // Switch state machine to Fight Mode here
        }

        return false;
    }

    public boolean isHeadLocked() { return this.entityData.get(HEAD_LOCKED); }
    public void setHeadLocked(boolean locked) { this.entityData.set(HEAD_LOCKED, locked); }

    public Action getAnimationState() { return Action.values()[this.entityData.get(WIZARD_ANIMATION_STATE)]; }
    public void setAnimationState(Action action) { this.entityData.set(WIZARD_ANIMATION_STATE, action.ordinal()); }

    public int getWizardFriendshipState() { return this.entityData.get(WIZARD_FRIENDSHIP_STATE); }
    public void setWizardFriendshipState(int state) { this.entityData.set(WIZARD_FRIENDSHIP_STATE, state); }

    public int getFriendshipStateTimer() { return this.entityData.get(FRIENDSHIP_STATE_TIMER); }
    public void setFriendshipStateTimer(int tick) { this.entityData.set(FRIENDSHIP_STATE_TIMER, tick); }
}
