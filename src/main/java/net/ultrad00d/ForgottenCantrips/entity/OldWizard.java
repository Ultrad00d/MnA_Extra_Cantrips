package net.ultrad00d.ForgottenCantrips.entity;

import com.mna.blocks.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.dialogue.DialogueChoice;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogue;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogueProvider;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardGlobalState;
import net.ultrad00d.ForgottenCantrips.structure.OldWizardsFishingSavedData;
import net.ultrad00d.ForgottenCantrips.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
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
    public static final double POI_REACH_THRESHOLD = 1.5 /5;
    public static final double WALK_ANIMATION_THRESHOLD = 0.3;

    // --- SYNCHRONIZED ENTITY DATA ACCESSORS ---
    private static final EntityDataAccessor<Boolean> HEAD_LOCKED = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> WIZARD_ACTION = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WIZARD_FRIENDSHIP_STATE = SynchedEntityData.defineId(OldWizard.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEAD_LOCKED, true);
        this.entityData.define(WIZARD_ACTION, 0);
        this.entityData.define(WIZARD_FRIENDSHIP_STATE, STATE_FRIENDLY);
    }

    // server side tickers
    private int friendshipStateTimer = 0;
    private int spellTick = 0;
    private boolean hasGardenedToday = false; // todo: add this to nbt // todo: reset during sleep
    // client side tickers
    private int idleTickCooldown = 80;

    private boolean isWaitingForNextIdle = true;
    // --- FRIENDSHIP STATES ---
    private static final int STATE_FRIENDLY = 0;
    private static final int STATE_NOT_FRIENDLY = 1;

    private static final int STATE_DISAPPOINTED = 2;

    // --- GECKOLIB ANIMATION PATHS ---
    private static final String STILL_ANIMATION     = "animation.old_wizard.still";
    private static final String IDLE1_ANIMATION     = "animation.old_wizard.idle1";
    private static final String IDLE2_ANIMATION     = "animation.old_wizard.idle2";
    private static final String IDLE3_ANIMATION     = "animation.old_wizard.idle3";
    private static final String IDLE4_ANIMATION     = "animation.old_wizard.idle4";
    private static final String WALK_ANIMATION      = "animation.old_wizard.walk";
    private static final String RUN_ANIMATION       = "animation.old_wizard.run";
    private static final String SPELL_ANIMATION     = "animation.old_wizard.spell";
    private static final String ANGER_ANIMATION     = "animation.old_wizard.anger";
    private static final String SHAKING_ANIMATION   = "animation.old_wizard.shaking";
    private static final String GROW1_ANIMATION     = "animation.old_wizard.grow_1";
    private static final String GROW2_ANIMATION     = "animation.old_wizard.grow_2";
    private static final String SHOW_BOOK_ANIMATION = "animation.old_wizard.book";
    private static final String READ_BOOK_ANIMATION = "animation.old_wizard.read";
    private static final int SPELL_ANIMATION_TICK_DURATION = 80; // ticks

    private String currentIdleAnimation = STILL_ANIMATION;


    // House Structure info
    private BlockPos homePos = null;
    private Rotation houseRotation = Rotation.NONE;
    private BlockPos gardenPos = null;
    private Rotation gardenRotation = null;

    // these are relative to the spawner block
    public enum OldWizardPOI {
        HALLWAY(6, 0, 4),
        FISHING_POINT       (11, -1, 4),
        BOOKSHELF_1         (-2, 0, 0),
        BOOKSHELF_2         (-3, 0, 1),
        BED                 (4, 4, 0),
        TOWER               (3, 13, 8),
        BREWERY             (-1, 4, 2),
        KITCHEN             (0, 0, 9),

        // Block POI
        FRONT_DOOR_SIGN     (9, 2, 4),
        FIRE_1              (-5, 0, 5),
        FIRE_2              (-5, 0, 6),
        FIRE_3              (-5, 0, 7),
        LIGHT_KITCHEN       (2, 1, 10),
        CANDLES_OFFICE      (2, 1, -2),
        LIGHT_OFFICE        (5, 2, -1),
        LIGHT_BEDROOM       (1, 5, 0);
        // TODO: add random POIs to go to when IDLE. UPD: Are they needed at all? One can look at any random pos nearby maybe

        private final int x;
        private final int y;
        private final int z;
        OldWizardPOI(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
        public BlockPos pos() { return new BlockPos(x, y ,z); }
        public int x() { return x; }
        public int y() { return y; }
        public int z() { return z; }
    }

    // ENTITY CREATION BLOCK
    public OldWizard(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, WALK_SPEED / 5)
                .add(Attributes.JUMP_STRENGTH, 0.5D)
//                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), -0.5D)
                .build();
    }
    @Override public boolean canChangeDimensions() { return false; }
    // ENTITY CREATION BLOCK OVER

    @Override
    protected void registerGoals() {
        // TODO: add common behavior: float, open doors, look at player, random look around, etc.
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        // TODO: fix head being locked
    }

    /** CORE TICKING METHOD */
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

            Action currentAction = this.getCurrentAction();
            if (currentAction == Action.IDLE || currentAction == Action.STILL) handleDailyRoutine();

            handleActionState(this.getCurrentAction());

            ForgottenCantrips.LOGGER.info("My action is: {}", getCurrentAction());
        } else {
            if (this.idleTickCooldown > 0) {
                this.idleTickCooldown--;
            }
        }
    }

    public enum Action {
        STILL, // does idle animations in this state
        IDLE,  // walks around the house in this state
        WALKING_TO_FISH,
        RETURNING_HOME,
        SLEEPING,
        WALKING_TO_GARDEN,
        SPELL_GROWING_CROPS,
        COLLECTING_CROPS,
        WALKING_TO_BOOKSHELF,
        GROWING_BEARD,
        SHOW_BOOK,
        READING,
        SHRINKING_BEARD,
        WALKING_TO_CAULDRON,
        BREWING,
        WALKING_TO_MANA,
        MAGIC_WORK,
        SPELL_LIGHTS_ON,
        SPELL_LIGHTS_OFF,
        SLEEP
    }

    /** Initiates routines based on daytime when IDLE or STILL */
    private void handleDailyRoutine() {
        if (TimeUtil.isFishingDay(this.level())) {
            this.setCurrentAction(Action.WALKING_TO_FISH);
            return;
        }

        long dayTime = this.level().getDayTime() % 24000L;
        // Morning (0 - 4000)
        if (dayTime >= 0 && dayTime < 1000) {
            if (!this.hasGardenedToday) this.setCurrentAction(Action.WALKING_TO_GARDEN);
        }
        // Noon (4000 - 8000)
        else if (dayTime >= 4000 && dayTime < 8000) {
            // TODO: come up with a reading routine
            RandomSource random = this.getRandom();

            switch (random.nextInt(3)) {
                case 0 -> this.setCurrentAction(Action.WALKING_TO_BOOKSHELF);
                case 1 -> this.setCurrentAction(Action.WALKING_TO_CAULDRON);
                case 2 -> this.setCurrentAction(Action.WALKING_TO_MANA);
            }
        }
        // Evening (8000 - 12500)
        else if (dayTime >= 8000 && dayTime < 12500) {
            this.setCurrentAction(Action.SPELL_LIGHTS_ON);
        }
        // Night (12500 - 24000)
        else if (dayTime >= 12500) {
            this.setCurrentAction(Action.SPELL_LIGHTS_OFF);
        }
    }

    /** Main action handler */
    private void handleActionState(Action action) {
        switch (action) {
            case WALKING_TO_FISH -> {
                this.goTo(OldWizardPOI.FISHING_POINT, WALK_SPEED);

                if (this.distanceToPOISqr(OldWizardPOI.FISHING_POINT) < POI_REACH_THRESHOLD) {
                    completeFishingTripTransition();
                }
            }
            case RETURNING_HOME -> {
                this.goTo(OldWizardPOI.HALLWAY, WALK_SPEED);

                if (this.distanceToPOISqr(OldWizardPOI.HALLWAY) < POI_REACH_THRESHOLD) {
                    this.removeMissingSign();
                    this.getNavigation().stop();
                    this.setCurrentAction(Action.IDLE);
                }
            }
            case WALKING_TO_GARDEN -> {
                this.goTo(gardenPos, WALK_SPEED);
                this.hasGardenedToday = true;

                if (this.distanceToSqr(gardenPos.getX(), gardenPos.getY(), gardenPos.getZ()) < POI_REACH_THRESHOLD) {
                    this.getNavigation().stop();
                    this.setCurrentAction(Action.SPELL_GROWING_CROPS);
                    this.spellTick = SPELL_ANIMATION_TICK_DURATION;
                }
            }
            case SPELL_GROWING_CROPS -> {
                if (this.spellTick > 0) {
                    this.spellTick--;
                } else {
                    this.setCurrentAction(Action.COLLECTING_CROPS);
                }
            }
            case COLLECTING_CROPS -> {
                // TODO: implement
                ForgottenCantrips.LOGGER.info("Finished collecting crops, going home");
                this.setCurrentAction(Action.RETURNING_HOME);
            }
            default -> {
                // TODO: to be implemented
                this.setCurrentAction(Action.IDLE);
            }
        }
    }

    /** Sends Old Wizard to the give POI */
    public void goTo(OldWizardPOI poi, double speed) {
        if (this.homePos == null) return;
        BlockPos pos = this.getWorldPos(poi.pos());
        this.goTo(pos, speed);
    }
    /** Sends Old Wizard to the given world pos */
    public void goTo(BlockPos worldPos, double speed) {
        this.getNavigation().moveTo(worldPos.getX() + 0.5d, worldPos.getY(), worldPos.getZ() + 0.5d, speed);
    }
    /** Returns distance between Old Wizard and given POI */
    private double distanceToPOISqr(OldWizardPOI poi) {
        if (this.homePos == null) return Double.MAX_VALUE;
        BlockPos worldPos = this.getWorldPos(poi.pos());
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
    /** Initiates geckolib animation based on current action; changes actions on some animations */
    private PlayState handleAnimationState(AnimationState<OldWizard> animationState) {
        RawAnimation builder = RawAnimation.begin();
        Vec3 deltaMovement = this.getDeltaMovement();
        deltaMovement = deltaMovement.add(0, -deltaMovement.y(), 0);
//        System.out.println(deltaMovement);
        if (deltaMovement.length() > WALK_ANIMATION_THRESHOLD) {
            return animationState.setAndContinue(builder.thenPlay(WALK_ANIMATION));
        }

        Action action = getCurrentAction();
        switch (action) {
            case SPELL_GROWING_CROPS -> {
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
            case STILL -> {
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

    /** Picks random idle animation out of the collection */
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

    /** Used to send dialogue messages on right click */
    @NotNull
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!player.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            // TODO: insert friendship mechanic here
            if (player.isSecondaryUseActive()) {
                player.sendSystemMessage(Component.literal("My garden is at " + this.gardenPos));
                player.sendSystemMessage(Component.literal("Distance to my garden is " + this.distanceToSqr(gardenPos.getX(), gardenPos.getY(), gardenPos.getZ())));
                return InteractionResult.SUCCESS;
            }

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
        if (this.houseRotation != null) nbt.putInt("HouseRotation", this.houseRotation.ordinal());
        if (this.gardenPos != null) nbt.putLong("GardenPos", this.gardenPos.asLong());
        if (this.gardenRotation != null) nbt.putInt("GardenRotation", this.gardenRotation.ordinal());
        nbt.putInt("FriendshipTimer", this.friendshipStateTimer);
        nbt.putInt("Action", getCurrentAction().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("HomePos")) this.homePos = BlockPos.of(nbt.getLong("HomePos"));
        if (nbt.contains("HouseRotation")) {
            int rotationIndex = nbt.getInt("HouseRotation");
            if (rotationIndex >= 0 && rotationIndex < Rotation.values().length) {
                this.houseRotation = Rotation.values()[rotationIndex];
            } else {
                this.houseRotation = Rotation.NONE;
            }
        }
        if (nbt.contains("GardenPos")) this.gardenPos = BlockPos.of(nbt.getLong("GardenPos"));
        if (nbt.contains("GardenRotation")) {
            int rotationIndex = nbt.getInt("GardenRotation");
            if (rotationIndex >= 0 && rotationIndex < Rotation.values().length) {
                this.gardenRotation = Rotation.values()[rotationIndex];
            } else {
                this.gardenRotation = Rotation.NONE;
            }
        }

        this.friendshipStateTimer = nbt.getInt("FriendshipTimer");
        this.setCurrentAction(Action.values()[nbt.getInt("Action")]); // TODO: insert safe reading here
    }

    public void setHomePos(BlockPos pos) { this.homePos = pos; }
    public void setHouseRotation(Rotation rotation) { this.houseRotation = rotation; }
    public void setGarden(Tuple<BlockPos, Rotation> tuple) {
        if (tuple == null) return;
        this.gardenPos = tuple.getA().below(); // 1 block offset so the result is ground and is reachable
        this.gardenRotation = tuple.getB();
    }

    /** Returns world coordinates from rotating given block pos with house rotation */
    public BlockPos getWorldPos(BlockPos toRotate) {
        BlockPos rotatedOffset = toRotate.rotate(this.houseRotation);
        return this.homePos.offset(rotatedOffset);
    }

    /** Adds translated text to the OldWizardPOI.FRONT_DOOR_SIGN */
    private void placeFishingSign() {
        updateFishingSign(
                Component.translatable("structure.forgotten_cantrips.old_wizard_house.fishing_sign.1"),
                Component.translatable("structure.forgotten_cantrips.old_wizard_house.fishing_sign.2"),
                true
        );
    }
    /** Empties the sign at OldWizardPOI.FRONT_DOOR_SIGN */
    public void removeMissingSign() {
        updateFishingSign(Component.empty(), Component.empty(), false);
    }
    private void updateFishingSign(Component line2, Component line3, boolean isGlowing) {
        if (this.homePos == null) return;

        BlockPos signPos = getWorldPos(OldWizardPOI.FRONT_DOOR_SIGN.pos());
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
    /** Logs, saves and discards this wizard */
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

    /** Adds light to the house */
    private void turnOnLights() { toggleLights(true); }
    /** Removes light from the house */
    private void turnOffLights() { toggleLights(false); }
    private void toggleLights(boolean active) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        BlockState fireState = active ? Blocks.FIRE.defaultBlockState() : Blocks.AIR.defaultBlockState();
        serverLevel.setBlock(getWorldPos(OldWizardPOI.FIRE_1.pos()), fireState, 3);
        serverLevel.setBlock(getWorldPos(OldWizardPOI.FIRE_2.pos()), fireState, 3);
        serverLevel.setBlock(getWorldPos(OldWizardPOI.FIRE_3.pos()), fireState, 3);

        BlockState lightState = active ? BlockInit.MAGE_LIGHT.get().defaultBlockState() : Blocks.AIR.defaultBlockState();
        serverLevel.setBlock(getWorldPos(OldWizardPOI.LIGHT_KITCHEN.pos()), lightState, 3);
        serverLevel.setBlock(getWorldPos(OldWizardPOI.LIGHT_OFFICE.pos()), lightState, 3);
        serverLevel.setBlock(getWorldPos(OldWizardPOI.LIGHT_BEDROOM.pos()), lightState, 3);

        // 3. Handle the office candles (Lit true vs. false)
        BlockState candleState = Blocks.CANDLE.defaultBlockState()
                .setValue(CandleBlock.CANDLES, 3)
                .setValue(CandleBlock.LIT, active);
        serverLevel.setBlock(getWorldPos(OldWizardPOI.CANDLES_OFFICE.pos()), candleState, 3);
    }
}
