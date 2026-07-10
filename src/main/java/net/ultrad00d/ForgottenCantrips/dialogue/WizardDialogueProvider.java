package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WizardDialogueProvider implements ICapabilitySerializable<CompoundTag> {
    public static Capability<WizardDialogueData> DIALOGUE_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    private final WizardDialogueData backend = new WizardDialogueData();
    private final LazyOptional<WizardDialogueData> optional = LazyOptional.of(() -> backend);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == DIALOGUE_CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        backend.saveNBT(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.loadNBT(nbt);
    }
}
