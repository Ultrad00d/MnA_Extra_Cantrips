package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;
import net.ultrad00d.ForgottenCantrips.structure.OldWizardsFishingSavedData;
import net.ultrad00d.ForgottenCantrips.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class OldWizardEvents {
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel serverLevel) {
            // If it is NOT the 3rd day, look for entities that need to come home
            if (!TimeUtil.isFishingDay(serverLevel)) {
                OldWizardsFishingSavedData database = OldWizardsFishingSavedData.get(serverLevel);

                List<UUID> wizardsToRestore = new ArrayList<>(database.getFishingWizards().keySet());

                // Iterate over stored data and restore them
                for (UUID id : wizardsToRestore) {
                    CompoundTag nbt = database.retrieveAndRemoveEntity(id);
                    if (nbt != null) {
                        OldWizard entity = EntityRegistry.OLD_WIZARD.get().create(serverLevel);
                        if (entity != null) {
                            entity.load(nbt);
                            serverLevel.addFreshEntity(entity);
                            entity.setCurrentAction(OldWizard.Action.RETURNING_HOME);
                        }
                    }
                }
            }
        }
    }
}
