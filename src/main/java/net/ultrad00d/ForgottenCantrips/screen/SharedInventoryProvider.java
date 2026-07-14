package net.ultrad00d.ForgottenCantrips.screen;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class SharedInventoryProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private static final Component CONTAINER_TITLE = Component.translatable("container." + ForgottenCantrips.MOD_ID + ".spectral_chest");

    public static Capability<SharedInventoryProvider> PLAYER_INVENTORY_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    private final ItemStackHandler inventory = new ItemStackHandler(27);
    private final LazyOptional<SharedInventoryProvider> optional = LazyOptional.of(() -> this);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == PLAYER_INVENTORY_CAP) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.put("Inventory", inventory.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inventory.deserializeNBT(nbt.getCompound("Inventory"));
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }
}