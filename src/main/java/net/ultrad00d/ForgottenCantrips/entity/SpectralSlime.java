package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpectralSlime extends Slime implements OwnableEntity
{
    private static final String OWNER_TAG = "Owner";
    private static final double FOLLOW_OWNER_DISTANCE_SQR = 64.0D;
    private UUID ownerUUID;

    public SpectralSlime(EntityType<? extends Slime> entityType, Level level)
    {
        super(entityType, level);
        this.setSize(1, true);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal());
        this.targetSelector.addGoal(3, new OwnerHurtByTargetGoal());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    protected ParticleOptions getParticleType() {
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

        LivingEntity owner = this.getOwner();
        if (owner != null && this.getTarget() == null && this.distanceToSqr(owner) >= FOLLOW_OWNER_DISTANCE_SQR)
        {
            this.getNavigation().moveTo(owner, 1.25D);
        }
    }

    @Override
    public boolean canAttack(LivingEntity target)
    {
        if (target instanceof Player || target == this.getOwner())
        {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target)
    {
        if (target instanceof Player || target == this.getOwner())
        {
            return false;
        }
        return super.doHurtTarget(target);
    }

    @Override
    protected void dealDamage(LivingEntity target)
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
    public boolean shouldDropExperience()
    {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        if (this.ownerUUID != null)
        {
            tag.putUUID(OWNER_TAG, this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
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
}
