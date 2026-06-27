package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryMenu;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpectralBoat extends ChestBoat implements OwnableEntity {
    private static final int EMPTY_LIFETIME_TICKS = 30 * 20;
    private static final String EMPTY_TICKS_TAG = "EmptyTicks";
    private UUID ownerUUID;

    private int emptyTicks;

    public SpectralBoat(EntityType<? extends Boat> entityType, Level level)
    {
        super(entityType, level);
        setVariant(Type.OAK);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (level().isClientSide())
        {
            return;
        }

        if (getPassengers().isEmpty())
        {
            if (++emptyTicks >= EMPTY_LIFETIME_TICKS)
            {
                discard();
            }
        }
        else
        {
            emptyTicks = 0;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt(EMPTY_TICKS_TAG, emptyTicks);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        emptyTicks = tag.getInt(EMPTY_TICKS_TAG);
    }

    @Override
    public Item getDropItem()
    {
        return Items.AIR;
    }

    public InteractionResult interact(Player player, InteractionHand pHand) {
        if (player.isSecondaryUseActive() && player.getUUID().equals(this.getOwnerUUID())) {
            if (!this.level().isClientSide) {
                player.getCapability(SharedInventoryProvider.PLAYER_INVENTORY_CAP).ifPresent(cap -> {
                    NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                            (id, playerInv, p) -> new SharedInventoryMenu(id, playerInv, cap.getInventory()),
                            Component.translatable("container.forgotten_cantrips.spectral_chest")
                    ));
                });
                return InteractionResult.CONSUME;
            }
            return InteractionResult.SUCCESS;
        } else {
            return super.interact(player, pHand);
        }
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
