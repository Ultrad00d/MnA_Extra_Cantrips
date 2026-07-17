package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.dialogue.DialogueChoice;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogue;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogueProvider;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardGlobalState;
import net.ultrad00d.ForgottenCantrips.structure.OldWizardsFishingSavedData;
import net.ultrad00d.ForgottenCantrips.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class OldWizard extends PathfinderMob implements GeoEntity {
    // GECKOLIB
    private final AnimatableInstanceCache CACHE = new SingletonAnimatableInstanceCache(this);
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return CACHE; }

    // MOVEMENT
    public static final double WALK_SPEED = 0.9;
    public static final double RUN_SPEED = 1.5;
    public static final double POI_REACH_THRESHOLD = 0.5;

    // --- SYNCHRONIZED ENTITY DATA ACCESSORS ---
    private static final EntityDataAccessor<Boolean> HEAD_LOCKED = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WIZARD_ACTION = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WIZARD_FRIENDSHIP_STATE = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);

    // server side tickers
    private int friendshipStateTimer = 0;

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
        this.entityData.define(WIZARD_ACTION, 0);
        this.entityData.define(WIZARD_FRIENDSHIP_STATE, STATE_FRIENDLY);
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


    // House Structure info
    private BlockPos homePos = null;
    private Rotation structureRotation = Rotation.NONE;

    public enum OldWizardPOI {
        // these are relative to the jack o' lantern (spawner block)
        PORCH               (6, 0, 4),
        FRONT_DOOR_SIGN     (9, 2, 4),
        FISHING_POINT       (11, -1, 4),
        BOOKSHELF_1         (-2, 0, 0),
        BOOKSHELF_2         (-3, 0, 1),
        BED                 (4, 4, 0),
        TOWER               (3, 13, 8),
        BREWERY             (-1, 4, 2),
        KITCHEN             (0, 0, 9);

        private final int x;
        private final int y;
        private final int z;
        OldWizardPOI(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
        public BlockPos pos() { return new BlockPos(x, y ,z); }
        public int x() { return x; }
        public int y() { return y; }
        public int z() { return z; }
    }

    public OldWizard(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, WALK_SPEED)
                .add(Attributes.JUMP_STRENGTH, 0.5D)
//                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), -0.5D)
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
            // Manage friendship timers
            int friendshipStateTimer = this.getFriendshipStateTimer();
            if (friendshipStateTimer > 0) {
                this.setFriendshipStateTimer(--friendshipStateTimer);
                if (friendshipStateTimer == 0) {
                    handleFriendshipStateTransition();
                }
            }
            if (TimeUtil.isFishingDay(this.level()) && this.getCurrentAction() != Action.WALKING_TO_FISH) {
                this.setCurrentAction(Action.WALKING_TO_FISH);
            }

            handleActionState(this.getCurrentAction());
        } else {
            if (this.idleTickCooldown > 0) {
                this.idleTickCooldown--;
            }
        }
    }

    public enum Action {
        IDLE,
        SPELL,
        SHOW_BOOK,
        READING,
        WALKING_TO_FISH,
        RETURNING_HOME
    }

    private void handleActionState(Action action) {
        switch (action) {
            case WALKING_TO_FISH -> {
                this.goTo(OldWizardPOI.FISHING_POINT, WALK_SPEED);

                if (this.distanceToPOISqr(OldWizardPOI.FISHING_POINT) < POI_REACH_THRESHOLD) {
                    completeFishingTripTransition();
                }
            }
            case RETURNING_HOME -> {
                this.goTo(OldWizardPOI.PORCH, WALK_SPEED);

                if (this.distanceToPOISqr(OldWizardPOI.PORCH) < POI_REACH_THRESHOLD) {
                    this.setCurrentAction(Action.IDLE);
                }
            }
            default -> {
                // TODO: to be implemented
            }
        }
    }
    public void goTo(OldWizardPOI poi, double speed) {
        BlockPos pos = this.getWorldPos(poi.pos());
        if (pos == null) return;
        this.getNavigation().moveTo(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d, speed);
    }

    private double distanceToPOISqr(OldWizardPOI poi) {
        BlockPos worldPos = this.getWorldPos(poi.pos());
        if (worldPos == null) return Double.MAX_VALUE;
        return this.distanceToSqr(worldPos.getX() + 0.5d, worldPos.getY(), worldPos.getZ() + 0.5d);
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

    // ANIMATION BLOCK START
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        AnimationController<OldWizard> controller = new AnimationController<>(this, this::handleAnimationState);
        controllerRegistrar.add(controller.transitionLength(5));
    }

    private PlayState handleAnimationState(AnimationState<OldWizard> animationState) {
        RawAnimation builder = RawAnimation.begin();


        Action action = getCurrentAction();
        switch (action) {
            case WALKING_TO_FISH, RETURNING_HOME -> {
                builder.thenPlay(WALK_ANIMATION);
            }
            case SPELL -> {
                builder.thenPlay(SPELL_ANIMATION);
            }
            case SHOW_BOOK -> {
                if (animationState.getController().hasAnimationFinished()) {
                    builder.thenPlay(READ_BOOK_ANIMATION);
                    this.setCurrentAction(Action.READING);
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
                    this.isWaitingForNextIdle = false;
                } else if (animationState.getController().hasAnimationFinished()) {
                    this.idleTickCooldown = 80;
                    this.isWaitingForNextIdle = true;
                    this.currentIdleAnimation = STILL_ANIMATION;
                }

                builder.thenPlay(currentIdleAnimation);
            }
        }


        return animationState.setAndContinue(builder);
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
    // ANIMATION BLOCK OVER

    @NotNull
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            // TODO: insert friendship mechanic here
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

    public Action getCurrentAction() { return Action.values()[this.entityData.get(WIZARD_ACTION)]; }
    public void setCurrentAction(Action action) { this.entityData.set(WIZARD_ACTION, action.ordinal()); }

    public int getWizardFriendshipState() { return this.entityData.get(WIZARD_FRIENDSHIP_STATE); }
    public void setWizardFriendshipState(int state) { this.entityData.set(WIZARD_FRIENDSHIP_STATE, state); }

    public int getFriendshipStateTimer() { return this.friendshipStateTimer; }
    public void setFriendshipStateTimer(int tick) { this.friendshipStateTimer = tick; }

    // --- HOUSE STRUCTURE ---
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        if (this.homePos != null) nbt.putLong("HomePos", this.homePos.asLong());
        if (this.structureRotation != null) nbt.putInt("StructureRotation", this.structureRotation.ordinal());
        nbt.putInt("FriendshipTimer", this.friendshipStateTimer);
        nbt.putInt("Action", getCurrentAction().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("HomePos")) this.homePos = BlockPos.of(nbt.getLong("HomePos"));
        if (nbt.contains("StructureRotation")) {
            int rotationIndex = nbt.getInt("StructureRotation");
            if (rotationIndex >= 0 && rotationIndex < Rotation.values().length) {
                this.structureRotation = Rotation.values()[rotationIndex];
            } else {
                this.structureRotation = Rotation.NONE;
            }
        }
        this.friendshipStateTimer = nbt.getInt("FriendshipTimer");
        this.setCurrentAction(Action.values()[nbt.getInt("Action")]); // TODO: insert safe reading here
    }

    public void setHomePos(BlockPos pos) { this.homePos = pos; }
    public void setStructureRotation(Rotation rotation) { this.structureRotation = rotation; }
    @Nullable
    public BlockPos getWorldPos(BlockPos toRotate) {
        if (this.homePos == null) return null;
        BlockPos rotatedOffset = toRotate.rotate(this.structureRotation);
        return this.homePos.offset(rotatedOffset);
    }

    public void placeFishingSign() {
        updateFishingSign(
                Component.translatable("structure.forgotten_cantrips.old_wizard_house.fishing_sign.1"),
                Component.translatable("structure.forgotten_cantrips.old_wizard_house.fishing_sign.2"),
                true
        );
    }
    public void removeFishingSign() {
        updateFishingSign(Component.empty(), Component.empty(), false);
    }
    private void updateFishingSign(Component line2, Component line3, boolean isGlowing) {
        BlockPos signPos = getWorldPos(OldWizardPOI.FRONT_DOOR_SIGN.pos());
        if (signPos == null) return;

        if (this.level().getBlockEntity(signPos) instanceof HangingSignBlockEntity signBlockEntity) {
            SignText updatedText = signBlockEntity.getFrontText()
                    .setMessage(2, line2)
                    .setMessage(3, line3)
                    .setColor(DyeColor.WHITE)
                    .setHasGlowingText(isGlowing);

            signBlockEntity.setText(updatedText, true);
            signBlockEntity.setChanged();

            var blockState = signBlockEntity.getBlockState();
            this.level().sendBlockUpdated(signPos, blockState, blockState, 3);
        }
    }

    private void completeFishingTripTransition() {
        if (this.level() instanceof ServerLevel serverLevel) {
            ForgottenCantrips.LOGGER.info("[DEBUG] Entity {} at {} started fishing. Day: {}, SignPos: {}",
                    this.getUUID(), this.blockPosition(), (this.level().getGameTime() / 24000L), this.getWorldPos(OldWizardPOI.FRONT_DOOR_SIGN.pos()));
            placeFishingSign();

            // Save to the disk
            CompoundTag nbt = new CompoundTag();
            this.saveWithoutId(nbt);
            OldWizardsFishingSavedData.get(serverLevel).saveEntity(this.getUUID(), nbt);
            this.discard();
        }
    }
}
