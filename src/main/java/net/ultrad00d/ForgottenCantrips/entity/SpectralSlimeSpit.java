package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpectralSlimeSpit extends Projectile implements ItemSupplier
{
    private static final int NAUSEA_TICKS = 10 * 20;
    private static final double HOMING_RANGE = 16.0D;
    private static final double HOMING_STRENGTH = 0.18D;
    private static final double MAX_SPEED = 1.5D;
    private UUID targetUUID;
    private LivingEntity cachedTarget;

    public SpectralSlimeSpit(EntityType<? extends SpectralSlimeSpit> entityType, Level level)
    {
        super(entityType, level);
    }

    public SpectralSlimeSpit(Level level, SpectralSlime slime)
    {
        this(EntityRegistry.SPECTRAL_SLIME_SPIT.get(), level);
        this.setOwner(slime);
        this.setPos(
                slime.getX() - (double) (slime.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(slime.yBodyRot * ((float) Math.PI / 180F)),
                slime.getEyeY() - 0.1D,
                slime.getZ() + (double) (slime.getBbWidth() + 1.0F) * 0.5D * (double) Mth.cos(slime.yBodyRot * ((float) Math.PI / 180F))
        );
    }

    public void setTarget(@Nullable LivingEntity target)
    {
        this.cachedTarget = target;
        this.targetUUID = target == null ? null : target.getUUID();
    }

    @Override
    public void tick()
    {
        super.tick();
        this.homeTowardTarget();

        Vec3 movement = this.getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS)
        {
            this.onHit(hitResult);
        }

        double x = this.getX() + movement.x;
        double y = this.getY() + movement.y;
        double z = this.getZ() + movement.z;
        this.updateRotation();

        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir))
        {
            this.discard();
        }
        else if (this.isInWaterOrBubble())
        {
            this.discard();
        } 
        else 
        {
            this.setDeltaMovement(movement.scale(0.99F));
            if (!this.isNoGravity()) 
            {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.06D, 0.0D));
            }

            this.setPos(x, y, z);
        }
    }

    private void homeTowardTarget()
    {
        LivingEntity target = this.getHomingTarget();
        if (target == null || !target.isAlive() || this.distanceToSqr(target) > HOMING_RANGE * HOMING_RANGE)
        {
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        double speed = Math.min(MAX_SPEED, Math.max(0.6D, movement.length()));
        Vec3 desired = target.getEyePosition().subtract(this.position()).normalize().scale(speed);
        this.setDeltaMovement(movement.lerp(desired, HOMING_STRENGTH));
    }

    private @Nullable LivingEntity getHomingTarget()
    {
        if (this.cachedTarget != null && !this.cachedTarget.isRemoved())
        {
            return this.cachedTarget;
        }

        if (this.targetUUID != null && this.level() instanceof ServerLevel serverLevel)
        {
            Entity entity = serverLevel.getEntity(this.targetUUID);
            if (entity instanceof LivingEntity livingEntity)
            {
                this.cachedTarget = livingEntity;
                return livingEntity;
            }
        }

        return null;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult)
    {
        Entity owner = this.getOwner();
        Entity target = hitResult.getEntity();

        if (owner instanceof LivingEntity livingOwner && target != owner)
        {
            if (target.hurt(this.damageSources().mobProjectile(this, livingOwner), 1.0F) && target instanceof LivingEntity livingTarget)
            {
                livingTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, NAUSEA_TICKS, 0), livingOwner);
            }
        }

        if (!this.level().isClientSide())
        {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult)
    {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide())
        {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData()
    {
    }

    @Override
    public @NotNull ItemStack getItem()
    {
        return new ItemStack(ItemRegistry.SPECTRAL_SLIME_BALL.get());
    }
}
