package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryProvider;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class SpectralDonkeyEvents {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        // The dying player's capabilities are already invalidated by this point (Entity#remove
        // runs before PlayerEvent.Clone fires), so they must be revived to read them here.
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(SharedInventoryProvider.PLAYER_INVENTORY_CAP).ifPresent(oldCap ->
                event.getEntity().getCapability(SharedInventoryProvider.PLAYER_INVENTORY_CAP).ifPresent(newCap ->
                        newCap.deserializeNBT(oldCap.serializeNBT())));
        event.getOriginal().invalidateCaps();
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

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof SpectralDonkey) { event.setCanceled(true); } //spectral donkey doesn't drop the saddle
    }
}
