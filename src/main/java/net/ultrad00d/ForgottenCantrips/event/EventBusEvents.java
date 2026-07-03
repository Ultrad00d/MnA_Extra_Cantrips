package net.ultrad00d.ForgottenCantrips.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventBusEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.SPECTRAL_DONKEY.get(), SpectralDonkey.createAttributes());
        event.put(EntityRegistry.OLD_WIZARD.get(), OldWizard.createAttributes());
    }
}
