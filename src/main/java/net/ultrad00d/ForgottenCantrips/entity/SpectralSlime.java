package net.ultrad00d.ForgottenCantrips.entity;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;

public class SpectralSlime extends Slime implements OwnableEntity, RangedAttackMob {
    private static final String OWNER_TAG = "Owner";
    private static final double FOLLOW_START_DISTANCE_SQR   = 100.0D; // follow player when further than that
    private static final double FOLLOW_STOP_DISTANCE_SQR    = 4.0D; // stop following player when closer than that
    private static final double TELEPORT_DISTANCE_SQR       = 144.0D; // teleport to player when closer than that
    private static final double TARGET_RANGE                = 12.0D; // find targets in this range
    private static final double TARGET_TOO_CLOSE_SQUARED    = 24D; // back up from the target when closer than that
    private static final double BASE_SHOOT_RANGE            = 4.0D; // shoot at targets when closer than that
    private static final int SHOOT_COOLDOWN_TICKS           = 40; // shoot period in ticks
    private static final int MAX_SPECTRAL_SLIME_SIZE        = 8; // slimes can merge up to this size
    private static final float HITBOX_SCALE                 = 4.0F; // slime's hitbox scale
    private static final int MERGE_CHECK_INTERVAL_TICKS     = 20; // check for possible merges period in ticks
    private static final int MERGE_REQUIRED_SLIMES          = 3; // amount of slimes needed for merge
    private static final double MERGE_SEEK_RANGE            = 10.0D; // try getting closer to other slimes in that range
    private static final double MERGE_TOUCH_RANGE           = 0.6d; // try merging with other slimes in that range

    private UUID ownerUUID;
    private int shootCooldown;
    private int mergeCheckCooldown;

    public SpectralSlime(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SpectralSlimeMoveControl(this);
        this.setSize(1, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override @NotNull protected ParticleOptions getParticleType() { return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ItemRegistry.SPECTRAL_SLIME_BALL.get())); }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel serverLevel) || this.isDeadOrDying()) return;

        if (this.shootCooldown > 0) --this.shootCooldown;

        if (this.mergeCheckCooldown > 0) {
            --this.mergeCheckCooldown;
        } else {
            this.mergeCheckCooldown = MERGE_CHECK_INTERVAL_TICKS;
            this.tryMergeWithNearbySlimes(serverLevel);
        }

        this.refreshTarget();
        LivingEntity target = this.getTarget();
        // if angry at target
        if (target != null && target.isAlive() && this.canAttack(target)) {
            double targetDistance = this.distanceToSqr(target);
            double shootRangeSqr = this.getShootRangeSqr();

            if (targetDistance > shootRangeSqr) {
                // Target is too far: chase
                this.moveToward(target, true);
            } else if (targetDistance < TARGET_TOO_CLOSE_SQUARED) {
                // Target is too close: back away while facing target
                this.moveAwayFrom(target);
            } else {
                // Ideal distance: hold position and look at target
                this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
                this.getLookControl().setLookAt(target, 10.0F, this.getMaxHeadXRot());
            }

            // Shoot when in range and line of sight is clear
            if (this.shootCooldown <= 0 && this.hasLineOfSight(target) && targetDistance <= shootRangeSqr) {
                this.performRangedAttack(target, 1.0F);
                this.shootCooldown = SHOOT_COOLDOWN_TICKS;
            }
            return;
        }

        SpectralSlime mergeTarget = this.findNearestMergeCandidate(serverLevel);
        if (mergeTarget != null) {
            this.moveToward(mergeTarget, true);
            return;
        }

        LivingEntity owner = this.getOwner();
        if (owner != null && owner.isAlive()) {
            double ownerDistance = this.distanceToSqr(owner);
            if (ownerDistance >= TELEPORT_DISTANCE_SQR) {
                this.moveTo(owner.getX(), owner.getY(), owner.getZ(), this.getYRot(), this.getXRot());
            } else if (ownerDistance > FOLLOW_START_DISTANCE_SQR) {
                this.moveToward(owner, false);
            } else if (ownerDistance <= FOLLOW_STOP_DISTANCE_SQR) {
                this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
            }
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float velocity) {
        if (!this.canAttack(target) || this.level().isClientSide()) return;

        SpectralSlimeSpit spit = new SpectralSlimeSpit(this.level(), this);
        spit.setTarget(target);
        double x = target.getX() - this.getX();
        double y = target.getY(1/3d) - spit.getY();
        double z = target.getZ() - this.getZ();
        double arc = Math.sqrt(x * x + z * z) * 0.2D;
        spit.shoot(x, y + arc, z, 1.5F, 10.0F);
        this.level().addFreshEntity(spit);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target) {
        if (target instanceof Player || target == this.getOwner()) return false;
        return super.canAttack(target);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if (target instanceof Player || target == this.getOwner()) return false;
        return super.doHurtTarget(target);
    }

    @Override
    protected void dealDamage(@NotNull LivingEntity target) {
        if (target instanceof Player) return;

        if (this.isAlive() && this.distanceToSqr(target) < 1.44D && this.hasLineOfSight(target)
                && target.hurt(this.damageSources().mobAttack(this), 1.0F)) {
            this.playSound(net.minecraft.sounds.SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.doEnchantDamageEffects(this, target);
        }
    }

    @Override public boolean removeWhenFarAway(double distanceToClosestPlayer) { return false; }
    @Override public void checkDespawn() { }

    @Override public boolean shouldDropExperience() { return false; }
    @Override @NotNull public EntityDimensions getDimensions(@NotNull Pose pose) { return super.getDimensions(pose).scale(HITBOX_SCALE); }
    @Override protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) { return 0.625F * dimensions.height; }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.ownerUUID != null) nbt.putUUID(OWNER_TAG, this.ownerUUID);
        nbt.putInt("Size", this.getSize());
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.hasUUID(OWNER_TAG)) this.ownerUUID = nbt.getUUID(OWNER_TAG);
        if (nbt.contains("Size")) this.setSize(nbt.getInt("Size"), false);
    }

    public @Nullable UUID getOwnerUUID() { return ownerUUID; }
    public void setOwnerUUID(UUID ownerUUID) { this.ownerUUID = ownerUUID; }

    @Override
    public @Nullable LivingEntity getOwner() {
        if (this.ownerUUID == null || !(this.level() instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    private void refreshTarget() {
        LivingEntity currentTarget = this.getTarget();
        // Target is already set
        if (currentTarget != null && currentTarget.isAlive() && this.canAttack(currentTarget)) return;


        List<Monster> targets = this.level().getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(this.getTargetSearchRange()),
                monster -> monster.isAlive() && this.canAttack(monster) && this.hasLineOfSight(monster)
        );

        targets.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .ifPresent(this::setTarget);
    }

    private double getShootRangeSqr() {
        double range = BASE_SHOOT_RANGE * (1 << Math.max(0, this.getSize() - 1));
        return range * range;
    }
    private double getTargetSearchRange() { return Math.max(TARGET_RANGE, Math.sqrt(this.getShootRangeSqr())); }

    private void tryMergeWithNearbySlimes(ServerLevel level) {
        if (this.isRemoved() || this.getSize() >= MAX_SPECTRAL_SLIME_SIZE || this.ownerUUID == null) return;

        List<SpectralSlime> matchingSlimes = level.getEntitiesOfClass(
                SpectralSlime.class,
                this.getBoundingBox().inflate(MERGE_TOUCH_RANGE),
                slime -> slime != this
                        && slime.isAlive()
                        && !slime.isRemoved()
                        && slime.getSize() == this.getSize()
                        && this.ownerUUID.equals(slime.getOwnerUUID())
        );

        if (matchingSlimes.size() < MERGE_REQUIRED_SLIMES - 1) return;
        matchingSlimes.sort(Comparator.comparingDouble(this::distanceToSqr));
        for (int i = 0; i < MERGE_REQUIRED_SLIMES - 1; ++i) {
            matchingSlimes.get(i).discard();
        }

        this.setSize(Math.min(MAX_SPECTRAL_SLIME_SIZE, this.getSize() + 1), true);
        this.setHealth(this.getMaxHealth());
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 2.0F, 0.5F);
        level.sendParticles(
                this.getParticleType(),
                this.getX(), this.getY(0.5D), this.getZ(),
                30, 0.5D, 0.5D, 0.5D, 0.15D
        );
    }

    private @Nullable SpectralSlime findNearestMergeCandidate(ServerLevel level) {
        if (this.isRemoved() || this.getSize() >= MAX_SPECTRAL_SLIME_SIZE || this.ownerUUID == null) return null;

        List<SpectralSlime> nearbySlimes = level.getEntitiesOfClass(
                SpectralSlime.class,
                this.getBoundingBox().inflate(MERGE_SEEK_RANGE),
                slime -> slime != this
                        && slime.isAlive()
                        && !slime.isRemoved()
                        && slime.getSize() == this.getSize()
                        && this.ownerUUID.equals(slime.getOwnerUUID())
        );
        if (nearbySlimes.size() < MERGE_REQUIRED_SLIMES - 1) return null;

        return nearbySlimes.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElse(null);
    }

    private void moveToward(LivingEntity entity, boolean aggressive) {
        double x = entity.getX() - this.getX();
        double z = entity.getZ() - this.getZ();
        float yaw = (float) (Mth.atan2(z, x) * (180F / (float) Math.PI)) - 90.0F;

        if (this.getMoveControl() instanceof SpectralSlimeMoveControl slimeMoveControl) {
            slimeMoveControl.setDirection(yaw, aggressive, false);
        }

        this.getMoveControl().setWantedPosition(entity.getX(), entity.getY(), entity.getZ(), 1.0);
        this.getLookControl().setLookAt(entity, 10.0F, this.getMaxHeadXRot());
    }

    private void moveAwayFrom(LivingEntity entity) {
        double x = entity.getX() - this.getX();
        double z = entity.getZ() - this.getZ();
        // Keep yaw pointing AT the entity so head and body look at target
        float yaw = (float) (Mth.atan2(z, x) * (180F / (float) Math.PI)) - 90.0F;

        if (this.getMoveControl() instanceof SpectralSlimeMoveControl slimeMoveControl) {
            slimeMoveControl.setDirection(yaw, true, true); // backUp = true
        }

        // Set wanted position behind the slime to keep MoveControl active
        double backX = this.getX() - x;
        double backZ = this.getZ() - z;
        this.getMoveControl().setWantedPosition(backX, entity.getY(), backZ, 1.0);
        this.getLookControl().setLookAt(entity, 10.0F, this.getMaxHeadXRot());
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide()) {
            this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, 0);
            // TODO: add functionality to spectral slimeballs, before adding a way to get them
//            ItemStack slimeBall = new ItemStack(ItemRegistry.SPECTRAL_SLIME_BALL.get());
//            if (!player.getInventory().add(slimeBall)) {
//                player.drop(slimeBall, false);
//            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        int i = this.getSize();
        if (!this.level().isClientSide() && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float)i / 4.0F;
            int j = i / 2;
            int k = 2;

            for(int l = 0; l < k; ++l) {
                float f1 = ((float)(l % 2) - 0.5F) * f;
                float f2 = -0.5f * f;
                SpectralSlime slime = (SpectralSlime) this.getType().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }

                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(j, true);
                    slime.setOwnerUUID(this.getOwnerUUID());
                    slime.moveTo(this.getX() + (double)f1, this.getY() + 0.5D, this.getZ() + (double)f2, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(slime);
                }
            }
        }
        this.setRemoved(reason);
        this.invalidateCaps();
        this.brain.clearMemories();
    }

    private class OwnerHurtTargetGoal extends TargetGoal {
        public OwnerHurtTargetGoal()
        {
            super(SpectralSlime.this, false);
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = SpectralSlime.this.getOwner();
            LivingEntity target = owner != null ? owner.getLastHurtMob() : null;
            return target != null && SpectralSlime.this.canAttack(target);
        }

        @Override
        public void start() {
            LivingEntity owner = SpectralSlime.this.getOwner();
            if (owner != null) SpectralSlime.this.setTarget(owner.getLastHurtMob());
            super.start();
        }
    }

    private class OwnerHurtByTargetGoal extends TargetGoal {
        public OwnerHurtByTargetGoal() {
            super(SpectralSlime.this, false);
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = SpectralSlime.this.getOwner();
            LivingEntity target = owner != null ? owner.getLastHurtByMob() : null;
            return target != null && SpectralSlime.this.canAttack(target);
        }

        @Override
        public void start() {
            LivingEntity owner = SpectralSlime.this.getOwner();
            if (owner != null) SpectralSlime.this.setTarget(owner.getLastHurtByMob());
            super.start();
        }
    }

    private static class SpectralSlimeMoveControl extends MoveControl {
        private final Slime slime;
        private float wantedYaw;
        private boolean aggressive;
        private boolean backUp; // Added flag for backing up
        private int jumpDelay;

        public SpectralSlimeMoveControl(Slime slime) {
            super(slime);
            this.slime = slime;
            this.wantedYaw = 180.0F * slime.getYRot() / (float) Math.PI;
        }

        // Updated setDirection to accept backUp
        public void setDirection(float yaw, boolean aggressive, boolean backUp) {
            this.wantedYaw = yaw;
            this.aggressive = aggressive;
            this.backUp = backUp;
        }

        @Override
        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.wantedYaw, 90.0F));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yHeadRot = this.mob.getYRot();

            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
                return;
            }

            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround()) {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = 10 + this.slime.getRandom().nextInt(20);
                    if (this.aggressive) {
                        this.jumpDelay /= 3;
                    }

                    this.slime.getJumpControl().jump();
                    // Apply forward (1.0F) or backward (-1.0F) motion vector on jump
                    this.slime.zza = this.backUp ? -1.0F : 1.0F;
                } else {
                    this.slime.xxa = 0.0F;
                    this.slime.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            } else {
                // Maintain velocity while in the air
                this.slime.zza = this.backUp ? -1.0F : 1.0F;
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
        }
    }
}
