package net.ultrad00d.ForgottenCantrips.entity;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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

public class SpectralSlime extends Slime implements OwnableEntity, RangedAttackMob
{
    private static final String OWNER_TAG = "Owner";
    private static final double FOLLOW_START_DISTANCE_SQR = 100.0D;
    private static final double FOLLOW_STOP_DISTANCE_SQR = 4.0D;
    private static final double TELEPORT_DISTANCE_SQR = 144.0D;
    private static final double TARGET_RANGE = 12.0D;
    private static final double BASE_SHOOT_RANGE = 4.0D;
    private static final int SHOOT_COOLDOWN_TICKS = 40;
    private static final int MERGE_REQUIRED_SLIMES = 4;
    private static final int MAX_SPECTRAL_SLIME_SIZE = 8;
    private static final int MERGE_CHECK_INTERVAL_TICKS = 20;
    private static final float HITBOX_SCALE = 4.0F;
    private UUID ownerUUID;
    private int shootCooldown;
    private int mergeCheckCooldown;

    public SpectralSlime(EntityType<? extends Slime> entityType, Level level)
    {
        super(entityType, level);
        this.moveControl = new SpectralSlimeMoveControl(this);
        this.setSize(1, true);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal());
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @NotNull
    @Override
    protected ParticleOptions getParticleType()
    {
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ItemRegistry.SPECTRAL_SLIME_BALL.get()));
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.level().isClientSide())
        {
            return;
        }

        if (this.shootCooldown > 0)
        {
            --this.shootCooldown;
        }

        if (this.mergeCheckCooldown > 0)
        {
            --this.mergeCheckCooldown;
        }
        else
        {
            this.mergeCheckCooldown = MERGE_CHECK_INTERVAL_TICKS;
            this.tryMergeWithNearbySlimes();
        }

        this.refreshTarget();
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive() && this.canAttack(target))
        {
            double targetDistance = this.distanceToSqr(target);
            double shootRangeSqr = this.getShootRangeSqr();
            if (targetDistance > shootRangeSqr)
            {
                this.moveToward(target, true);
            }
            else
            {
                this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
                this.getLookControl().setLookAt(target, 10.0F, this.getMaxHeadXRot());
            }

            if (this.shootCooldown <= 0 && targetDistance <= shootRangeSqr && this.hasLineOfSight(target))
            {
                this.performRangedAttack(target, 1.0F);
                this.shootCooldown = SHOOT_COOLDOWN_TICKS;
            }
            return;
        }

        LivingEntity owner = this.getOwner();
        if (owner != null && owner.isAlive())
        {
            double ownerDistance = this.distanceToSqr(owner);
            if (ownerDistance >= TELEPORT_DISTANCE_SQR)
            {
                this.moveTo(owner.getX(), owner.getY(), owner.getZ(), this.getYRot(), this.getXRot());
            }
            else if (ownerDistance > FOLLOW_START_DISTANCE_SQR)
            {
                this.moveToward(owner, false);
            }
            else if (ownerDistance <= FOLLOW_STOP_DISTANCE_SQR)
            {
                this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
            }
        }
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float velocity)
    {
        if (!this.canAttack(target) || this.level().isClientSide())
        {
            return;
        }

        SpectralSlimeSpit spit = new SpectralSlimeSpit(this.level(), this);
        spit.setTarget(target);
        double x = target.getX() - this.getX();
        double y = target.getY(0.3333333333333333D) - spit.getY();
        double z = target.getZ() - this.getZ();
        double arc = Math.sqrt(x * x + z * z) * 0.2D;
        spit.shoot(x, y + arc, z, 1.5F, 10.0F);
        this.level().addFreshEntity(spit);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity target)
    {
        if (target instanceof Player || target == this.getOwner())
        {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity target)
    {
        if (target instanceof Player || target == this.getOwner())
        {
            return false;
        }
        return super.doHurtTarget(target);
    }

    @Override
    protected void dealDamage(@NotNull LivingEntity target)
    {
        if (target instanceof Player || target == this.getOwner())
        {
            return;
        }

        if (this.isAlive() && this.distanceToSqr(target) < 1.44D && this.hasLineOfSight(target) && target.hurt(this.damageSources().mobAttack(this), 1.0F))
        {
            this.playSound(net.minecraft.sounds.SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.doEnchantDamageEffects(this, target);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer)
    {
        return false;
    }

    @Override
    public void checkDespawn()
    {
    }

    @Override
    public void remove(Entity.@NotNull RemovalReason reason)
    {
        if (!this.level().isClientSide() && this.isDeadOrDying() && this.getSize() > 1)
        {
            this.setSize(1, false);
        }

        super.remove(reason);
    }

    @Override
    public boolean shouldDropExperience()
    {
        return false;
    }

    @NotNull
    @Override
    public EntityDimensions getDimensions(@NotNull Pose pose)
    {
        return super.getDimensions(pose).scale(HITBOX_SCALE);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) {
        return 0.625F * dimensions.height;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        if (this.ownerUUID != null)
        {
            tag.putUUID(OWNER_TAG, this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(OWNER_TAG))
        {
            this.ownerUUID = tag.getUUID(OWNER_TAG);
        }
        this.setSize(1, false);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public @Nullable UUID getOwnerUUID()
    {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID)
    {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public @Nullable LivingEntity getOwner()
    {
        if (this.ownerUUID == null || !(this.level() instanceof ServerLevel serverLevel))
        {
            return null;
        }
        return serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    private void refreshTarget() {
        LivingEntity currentTarget = this.getTarget();
        if (currentTarget != null && currentTarget.isAlive() && this.canAttack(currentTarget))
        {
            return;
        }

        List<Monster> targets = this.level().getEntitiesOfClass(
                Monster.class,
                this.getBoundingBox().inflate(this.getTargetSearchRange()),
                monster -> monster.isAlive() && this.canAttack(monster) && this.hasLineOfSight(monster)
        );

        targets.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .ifPresent(this::setTarget);
    }

    private double getShootRangeSqr()
    {
        double range = BASE_SHOOT_RANGE * (1 << Math.max(0, this.getSize() - 1));
        return range * range;
    }

    private double getTargetSearchRange()
    {
        return Math.max(TARGET_RANGE, Math.sqrt(this.getShootRangeSqr()));
    }

    private void tryMergeWithNearbySlimes()
    {
        if (this.isRemoved() || this.getSize() >= MAX_SPECTRAL_SLIME_SIZE || this.ownerUUID == null)
        {
            return;
        }

        List<SpectralSlime> matchingSlimes = this.level().getEntitiesOfClass(
                SpectralSlime.class,
                this.getBoundingBox().inflate(3.0D),
                slime -> slime != this
                        && slime.isAlive()
                        && !slime.isRemoved()
                        && slime.getSize() == this.getSize()
                        && this.ownerUUID.equals(slime.getOwnerUUID())
        );

        if (matchingSlimes.size() < MERGE_REQUIRED_SLIMES - 1)
        {
            return;
        }

        matchingSlimes.sort(Comparator.comparingDouble(this::distanceToSqr));
        for (int i = 0; i < MERGE_REQUIRED_SLIMES - 1; ++i)
        {
            matchingSlimes.get(i).discard();
        }

        this.setSize(Math.min(MAX_SPECTRAL_SLIME_SIZE, this.getSize() + 1), true);
        this.setHealth(this.getMaxHealth());
    }

    private void moveToward(LivingEntity entity, boolean aggressive) {
        double x = entity.getX() - this.getX();
        double z = entity.getZ() - this.getZ();
        float yaw = (float) (Mth.atan2(z, x) * (180F / (float) Math.PI)) - 90.0F;

        if (this.getMoveControl() instanceof SpectralSlimeMoveControl slimeMoveControl)
        {
            slimeMoveControl.setDirection(yaw, aggressive);
        }

        this.getMoveControl().setWantedPosition(entity.getX(), entity.getY(), entity.getZ(), 1.0);
        this.getLookControl().setLookAt(entity, 10.0F, this.getMaxHeadXRot());
    }

    private class OwnerHurtTargetGoal extends net.minecraft.world.entity.ai.goal.target.TargetGoal
    {
        public OwnerHurtTargetGoal()
        {
            super(SpectralSlime.this, false);
        }

        @Override
        public boolean canUse()
        {
            LivingEntity owner = SpectralSlime.this.getOwner();
            LivingEntity target = owner != null ? owner.getLastHurtMob() : null;
            return target != null && SpectralSlime.this.canAttack(target);
        }

        @Override
        public void start()
        {
            LivingEntity owner = SpectralSlime.this.getOwner();
            if (owner != null)
            {
                SpectralSlime.this.setTarget(owner.getLastHurtMob());
            }
            super.start();
        }
    }

    private class OwnerHurtByTargetGoal extends net.minecraft.world.entity.ai.goal.target.TargetGoal
    {
        public OwnerHurtByTargetGoal()
        {
            super(SpectralSlime.this, false);
        }

        @Override
        public boolean canUse()
        {
            LivingEntity owner = SpectralSlime.this.getOwner();
            LivingEntity target = owner != null ? owner.getLastHurtByMob() : null;
            return target != null && SpectralSlime.this.canAttack(target);
        }

        @Override
        public void start()
        {
            LivingEntity owner = SpectralSlime.this.getOwner();
            if (owner != null)
            {
                SpectralSlime.this.setTarget(owner.getLastHurtByMob());
            }
            super.start();
        }
    }

    private static class SpectralSlimeMoveControl extends MoveControl
    {
        private final Slime slime;
        private float wantedYaw;
        private boolean aggressive;
        private int jumpDelay;

        public SpectralSlimeMoveControl(Slime slime)
        {
            super(slime);
            this.slime = slime;
            this.wantedYaw = 180.0F * slime.getYRot() / (float) Math.PI;
        }

        public void setDirection(float yaw, boolean aggressive)
        {
            this.wantedYaw = yaw;
            this.aggressive = aggressive;
        }

        @Override
        public void tick()
        {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.wantedYaw, 90.0F));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yHeadRot = this.mob.getYRot();

            if (this.operation != MoveControl.Operation.MOVE_TO)
            {
                this.mob.setZza(0.0F);
                return;
            }

            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround())
            {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                if (this.jumpDelay-- <= 0) {
                    this.jumpDelay = 10 + this.slime.getRandom().nextInt(20);
                    if (this.aggressive) {
                        this.jumpDelay /= 3;
                    }

                    this.slime.getJumpControl().jump();
                } else {
                    this.slime.xxa = 0.0F;
                    this.slime.zza = 0.0F;
                    this.mob.setSpeed(0.0F);
                }
            }
            else
            {
                this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }
        }
    }
}
