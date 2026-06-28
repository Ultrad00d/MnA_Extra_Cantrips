package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkHooks;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryMenu;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryProvider;
import org.jetbrains.annotations.NotNull;

public class SpectralDonkey extends AbstractChestedHorse {
    private static final int LIFETIME_TICKS = 30 * 20;
    private boolean isPersistent = false;
    private int noRiderTicks;

    public SpectralDonkey(EntityType<? extends SpectralDonkey> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isPersistent) return;
        if (level().isClientSide()) return;

        if (!getPassengers().isEmpty()) {
            noRiderTicks = 0;
            return;
        }

        if (++noRiderTicks >= LIFETIME_TICKS) { discard(); }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractChestedHorse.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.0F).add(Attributes.ATTACK_KNOCKBACK).add(Attributes.JUMP_STRENGTH, 1.45F);
    }

    protected void randomizeAttributes(@NotNull RandomSource randomSource) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0F);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3F);
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(1.35F);
        this.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).setBaseValue(0.05F);
    }

    public float getStepHeight() {
        return 1.75F;
    }

    protected boolean canParent() {
        return false;
    }

    public boolean shouldDropExperience() {
        return false;
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean canStandOnFluid(FluidState state) {
        return state.is(Fluids.WATER) && this.getForward().y > (double)-0.2F && !this.isUnderWater();
    }

    protected float getWaterSlowDown() {
        return 1.0F;
    }

    public boolean canHoldItem(ItemStack stack) {
        return false;
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player == getOwner()) {
            if (!player.isSecondaryUseActive()) {
                this.doPlayerRide(player);
                return InteractionResult.SUCCESS;
            }

            if (!this.level().isClientSide) {
                this.openSpectralChest(player);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        } else {
            if (!this.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_donkey.notowner"), true);
            }
            return InteractionResult.FAIL;
        }
    }

    public void openSpectralChest(Player player) {
        player.getCapability(SharedInventoryProvider.PLAYER_INVENTORY_CAP).ifPresent(cap -> {
            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                    (id, playerInv, p) -> new SharedInventoryMenu(id, playerInv, cap.getInventory()),
                    Component.translatable("container.forgotten_cantrips.spectral_chest")
            ));
        });
    }

    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }
    protected void playStepSound(BlockPos pos, BlockState state) {}
    protected void playJumpSound() {}

    public boolean isPersistent() { return isPersistent; }
    public void setPersistent(boolean persistent) { isPersistent = persistent; }
}
