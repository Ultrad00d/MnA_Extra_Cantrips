package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.integration.Helper;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class SpectralArmorEventHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private static boolean isSpectral(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item == ItemRegistry.SPECTRAL_HELMET.get()
                || item == ItemRegistry.SPECTRAL_CHESTPLATE.get()
                || item == ItemRegistry.SPECTRAL_LEGGINGS.get()
                || item == ItemRegistry.SPECTRAL_BOOTS.get();
    }

    private static ItemStack createSpectralStack(EquipmentSlot slot, int tier) {
        ItemStack stack = switch (slot) {
            case HEAD -> new ItemStack(ItemRegistry.SPECTRAL_HELMET.get());
            case CHEST -> new ItemStack(ItemRegistry.SPECTRAL_CHESTPLATE.get());
            case LEGS -> new ItemStack(ItemRegistry.SPECTRAL_LEGGINGS.get());
            case FEET -> new ItemStack(ItemRegistry.SPECTRAL_BOOTS.get());
            default -> throw new IllegalArgumentException("Not armor slot " + slot);
        };
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("Unbreakable", true);
        tag.putInt("SpectralTier", tier);

        return stack;
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect() != EffectRegistry.SPECTRAL_ARMOR.get()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        int tier = Helper.getPlayerTier(player);
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (player.getItemBySlot(slot).isEmpty()) {
                player.setItemSlot(slot, createSpectralStack(slot, tier));
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

        cleanupArmorSlots(player);
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


    private static void cleanupArmorSlots(Player player) {
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

        ItemStack carried = player.containerMenu.getCarried();
        if (isSpectral(carried)) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        event.getDrops().removeIf(itemEntity -> isSpectral(itemEntity.getItem()));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            cleanupArmorSlots(player);
        }
    }
}