package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.registry.ItemsRegistry;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class SpectralArmorEventHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static boolean isSpectral(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == ItemsRegistry.SPECTRAL_HELMET.get()
                || item == ItemsRegistry.SPECTRAL_CHESTPLATE.get()
                || item == ItemsRegistry.SPECTRAL_LEGGINGS.get()
                || item == ItemsRegistry.SPECTRAL_BOOTS.get();
    }

    private static ItemStack createSpectralStack(EquipmentSlot slot) {
        ItemStack stack = switch (slot) {
            case HEAD -> new ItemStack(ItemsRegistry.SPECTRAL_HELMET.get());
            case CHEST -> new ItemStack(ItemsRegistry.SPECTRAL_CHESTPLATE.get());
            case LEGS -> new ItemStack(ItemsRegistry.SPECTRAL_LEGGINGS.get());
            case FEET -> new ItemStack(ItemsRegistry.SPECTRAL_BOOTS.get());
            default -> throw new IllegalArgumentException("Not armor slot " + slot);
        };
        stack.getOrCreateTag().putBoolean("Unbreakable", true);

        return stack;
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect() != EffectRegistry.SPECTRAL_ARMOR.get()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (player.getItemBySlot(slot).isEmpty()) {
                player.setItemSlot(slot, createSpectralStack(slot));
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        handleEffectEnd(event);
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        handleEffectEnd(event);
    }

    private static void handleEffectEnd(MobEffectEvent event) {
        if (event.getEffectInstance() == null) return;
        if (event.getEffectInstance().getEffect() != EffectRegistry.SPECTRAL_ARMOR.get()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        cleanupSlots(player);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack from = event.getFrom();
        if (!isSpectral(from)) return;
        if (ItemStack.matches(from, event.getTo())) return;
        if (!(event.getEntity() instanceof Player player)) return;

        cleanupInventoryAndCursor(player);
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemEntity itemEntity = event.getEntity();
        if (isSpectral(itemEntity.getItem())) {
            event.setCanceled(true);
            itemEntity.discard();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (!player.hasEffect(EffectRegistry.SPECTRAL_ARMOR.get())) {
            cleanupSlots(player);
        }
        cleanupInventoryAndCursor(player);
    }

    private static void cleanupSlots(Player player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (isSpectral(player.getItemBySlot(slot))) {
                player.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    private static void cleanupInventoryAndCursor(Player player) {
        var items = player.getInventory().items;
        for (int i = 0; i < items.size(); i++) {
            if (isSpectral(items.get(i))) {
                items.set(i, ItemStack.EMPTY);
            }
        }

        if (player.containerMenu != null) {
            ItemStack carried = player.containerMenu.getCarried();
            if (isSpectral(carried)) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        event.getDrops().removeIf(itemEntity -> isSpectral(itemEntity.getItem()));
    }
}