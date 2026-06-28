package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryProvider;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class CapabilityEvents {
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "shared_inventory"), new SharedInventoryProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        event.getOriginal().getCapability(SharedInventoryProvider.PLAYER_INVENTORY_CAP).ifPresent(oldCap -> {
            event.getEntity().getCapability(SharedInventoryProvider.PLAYER_INVENTORY_CAP).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });
    }

    @SubscribeEvent
    public static void onContainerOpened(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof HorseInventoryMenu && event.getEntity() instanceof ServerPlayer player && player.getVehicle() != null && player.getVehicle() instanceof SpectralDonkey horse) {
            horse.openSpectralChest(event.getEntity());
        }

        if (event.getContainer() instanceof ChestMenu && event.getEntity() instanceof ServerPlayer player && player.getVehicle() != null && player.getVehicle() instanceof SpectralBoat boat) {
            boat.openSpectralChest(event.getEntity());
        }
    }

}
