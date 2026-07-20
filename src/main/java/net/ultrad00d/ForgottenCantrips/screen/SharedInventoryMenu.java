package net.ultrad00d.ForgottenCantrips.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.ultrad00d.ForgottenCantrips.registry.MenuRegistry;
import org.jetbrains.annotations.NotNull;

public class SharedInventoryMenu extends AbstractContainerMenu {
    private static final int SHOWN_SLOT_COUNT = 27;
    private final IItemHandler inventory;

    public SharedInventoryMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, new ItemStackHandler(27));
    }

    public SharedInventoryMenu(int containerId, Inventory playerInv, IItemHandler spectralChest) {
        super(MenuRegistry.SHARED_INVENTORY_MENU.get(), containerId);
        this.inventory = spectralChest;

        this.initializeSlots(playerInv, spectralChest);
    }

    private void initializeSlots(Inventory playerInventory, IItemHandler spectralChest) {
        addChestSlots(spectralChest);
        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }
    private void addChestSlots(IItemHandler spectralChest) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new SlotItemHandler(spectralChest, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
    }
    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, 9 + col + row * 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) { return true; }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < SHOWN_SLOT_COUNT) {
                if (!this.moveItemStackTo(itemstack1, SHOWN_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, SHOWN_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}