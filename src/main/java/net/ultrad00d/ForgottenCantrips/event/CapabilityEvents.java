package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.dialogue.WizardDialogueProvider;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryProvider;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class CapabilityEvents {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "shared_inventory"), new SharedInventoryProvider());
            event.addCapability(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "wizard_dialogue"), new WizardDialogueProvider());
        }
    }
}
