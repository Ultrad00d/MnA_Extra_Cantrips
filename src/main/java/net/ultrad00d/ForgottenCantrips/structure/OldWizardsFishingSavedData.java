package net.ultrad00d.ForgottenCantrips.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OldWizardsFishingSavedData extends SavedData {
    private final Map<UUID, CompoundTag> fishingWizards = new HashMap<>();

    public static OldWizardsFishingSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                OldWizardsFishingSavedData::load,
                OldWizardsFishingSavedData::new,
                "fishing_old_wizards"
        );
    }

    public Map<UUID, CompoundTag> getFishingWizards() { return this.fishingWizards; }

    public void saveEntity(UUID id, CompoundTag nbt) {
        fishingWizards.put(id, nbt);
        this.setDirty();
    }

    public CompoundTag retrieveAndRemoveEntity(UUID id) {
        CompoundTag data = fishingWizards.remove(id);
        this.setDirty();
        return data;
    }

    public boolean isFishing(UUID id) { return fishingWizards.containsKey(id); }

    public static OldWizardsFishingSavedData load(CompoundTag tag) {
        OldWizardsFishingSavedData data = new OldWizardsFishingSavedData();
        ListTag list = tag.getList("Entities", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            UUID id = entry.getUUID("UUID");
            CompoundTag entityData = entry.getCompound("EntityData");
            data.fishingWizards.put(id, entityData);
        }
        return data;
    }

    @NotNull
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, CompoundTag> entry : fishingWizards.entrySet()) {
            CompoundTag nbtEntry = new CompoundTag();
            nbtEntry.putUUID("UUID", entry.getKey());
            nbtEntry.put("EntityData", entry.getValue());
            list.add(nbtEntry);
        }
        tag.put("Entities", list);
        return tag;
    }
}
