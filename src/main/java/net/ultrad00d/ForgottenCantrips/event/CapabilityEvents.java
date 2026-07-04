package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.ResetVillagerTradingProgressCantripLogic;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import net.ultrad00d.ForgottenCantrips.item.MemoryEmeraldItem;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;
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




    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof SpectralDonkey donkey) { event.setCanceled(true); } //spectral donkey doesn't drop the saddle
    }



    @SubscribeEvent
    public static void onPlayerInteractEntity(PlayerInteractEvent.EntityInteractSpecific event) {

        if (event.getEntity().getItemInHand(event.getHand()).getItem() instanceof MemoryEmeraldItem) {
            if (event.getLevel().isClientSide()) {
                return;
            }

            Player player = event.getEntity();
            Entity target = event.getTarget();

            if (target.getType() == EntityType.VILLAGER) {
                if (((Villager) target).getVillagerData().getProfession() == VillagerProfession.NITWIT) {

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setResult(Event.Result.DENY);

                    player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.nitwit"));

                } else
                {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setResult(Event.Result.DENY);

                    if (ResetVillagerTradingProgressCantripLogic.applyMemories(event.getItemStack(), (Villager) target)) {
                        event.getItemStack().setCount(event.getItemStack().getCount() - 1);
                        player.getInventory().add(new ItemStack(Items.EMERALD, 1));
                    } else {
                        player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.empty_warning"));
                        return;
                    }
                }

                } else {
                    player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.reset_villager_trading_progress.not_a_villager"));
                    return;
                }


            }

        }
    }

