package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class WizardDialogueData {
    private WizardGlobalState globalState = WizardGlobalState.NOT_MET;
    private final Map<String, WizardCantripBranchState> spellBranches = new HashMap<>();

    public WizardGlobalState getGlobalState() { return globalState; }
    public void setGlobalState(WizardGlobalState state) { this.globalState = state; }

    public WizardCantripBranchState getBranchState(String cantripId) {
        return spellBranches.getOrDefault(cantripId, WizardCantripBranchState.NOT_STARTED);
    }

    public void setBranchState(String cantripId, WizardCantripBranchState state) {
        spellBranches.put(cantripId, state);
    }

    // Standard save/load logic for NBT
    public void saveNBT(CompoundTag nbt) {
        nbt.putString("GlobalState", globalState.name());
        CompoundTag branchesNbt = new CompoundTag();
        spellBranches.forEach((cantripId, state) -> branchesNbt.putString(cantripId, state.name()));
        nbt.put("CantripBranches", branchesNbt);
    }

    public void loadNBT(CompoundTag nbt) {
        if (nbt.contains("GlobalState")) {
            this.globalState = WizardGlobalState.valueOf(nbt.getString("GlobalState"));
        }
        if (nbt.contains("SpellBranches")) {
            CompoundTag branchesNbt = nbt.getCompound("CantripBranches");
            for (String key : branchesNbt.getAllKeys()) {
                this.spellBranches.put(key, WizardCantripBranchState.valueOf(branchesNbt.getString(key)));
            }
        }
    }
}
