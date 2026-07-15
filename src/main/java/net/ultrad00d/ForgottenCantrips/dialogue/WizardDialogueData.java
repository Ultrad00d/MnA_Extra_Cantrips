package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class WizardDialogueData {
    private WizardGlobalState globalState = WizardGlobalState.NOT_MET;
    private final Map<String, WizardCantripBranchState> dialogueBranches = new HashMap<>();

    public WizardGlobalState getGlobalState() { return globalState; }
    public void setGlobalState(WizardGlobalState state) { this.globalState = state; }

    public WizardCantripBranchState getBranchState(String cantripId) {
        return dialogueBranches.getOrDefault(cantripId, WizardCantripBranchState.NOT_STARTED);
    }

    public void setBranchState(String cantripId, WizardCantripBranchState state) {
        dialogueBranches.put(cantripId, state);
    }

    // Standard save/load logic for NBT
    public void saveNBT(CompoundTag nbt) {
        nbt.putString("GlobalState", globalState.name());
        CompoundTag branchesNbt = new CompoundTag();
        dialogueBranches.forEach((cantripId, state) -> branchesNbt.putString(cantripId, state.name()));
        nbt.put("DialogueBranches", branchesNbt);
    }

    public void loadNBT(CompoundTag nbt) {
        if (nbt.contains("GlobalState")) {
            this.globalState = WizardGlobalState.valueOf(nbt.getString("GlobalState"));
        }
        if (nbt.contains("DialogueBranches")) {
            CompoundTag branchesNbt = nbt.getCompound("DialogueBranches");
            for (String key : branchesNbt.getAllKeys()) {
                this.dialogueBranches.put(key, WizardCantripBranchState.valueOf(branchesNbt.getString(key)));
            }
        }
    }
}
