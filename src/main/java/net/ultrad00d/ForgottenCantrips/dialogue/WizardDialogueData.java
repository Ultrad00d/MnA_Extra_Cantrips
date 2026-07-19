package net.ultrad00d.ForgottenCantrips.dialogue;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class WizardDialogueData {
    private WizardGlobalState globalState = WizardGlobalState.NOT_MET;
    private final Map<String, WizardCantripBranchState> dialogueBranches = new HashMap<>();

    private final Map<String, Long> branchUnlockTimes = new HashMap<>();

    public WizardGlobalState getGlobalState() { return globalState; }
    public void setGlobalState(WizardGlobalState state) { this.globalState = state; }

    public void setBranchState(String cantripId, WizardCantripBranchState state) { dialogueBranches.put(cantripId, state); }
    public WizardCantripBranchState getBranchState(String cantripId) { return dialogueBranches.getOrDefault(cantripId, WizardCantripBranchState.NOT_STARTED); }
    public WizardCantripBranchState getBranchState(String cantripId, long currentTicks) {
        WizardCantripBranchState state = dialogueBranches.getOrDefault(cantripId, WizardCantripBranchState.NOT_STARTED);
        long unlockTime = this.getUnlockTime(cantripId);

        if (currentTicks >= unlockTime) {
            if (state == WizardCantripBranchState.PART_1_ITEM_GIVEN) {
                state = WizardCantripBranchState.PART_2;
                this.setBranchState(cantripId, state);
            } else if (state == WizardCantripBranchState.PART_2_ITEM_GIVEN) {
                state = WizardCantripBranchState.PART_3;
                this.setBranchState(cantripId, state);
            }
        }
        return state;
    }

    public long getUnlockTime(String cantripId) { return branchUnlockTimes.getOrDefault(cantripId, 0L); }
    public void setUnlockTime(String cantripId, long time) { branchUnlockTimes.put(cantripId, time); }

    // Standard save/load logic for NBT
    public void saveNBT(CompoundTag nbt) {
        nbt.putString("GlobalState", globalState.name());
        CompoundTag branchesNbt = new CompoundTag();
        dialogueBranches.forEach((cantripId, state) -> branchesNbt.putString(cantripId, state.name()));
        nbt.put("DialogueBranches", branchesNbt);

        CompoundTag unlocksNbt = new CompoundTag();
        branchUnlockTimes.forEach(unlocksNbt::putLong);
        nbt.put("BranchUnlockTimes", unlocksNbt);
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
        if (nbt.contains("BranchUnlockTimes")) {
            CompoundTag unlocksNbt = nbt.getCompound("BranchUnlockTimes");
            for (String key : unlocksNbt.getAllKeys()) {
                this.branchUnlockTimes.put(key, unlocksNbt.getLong(key));
            }
        }
    }
}
