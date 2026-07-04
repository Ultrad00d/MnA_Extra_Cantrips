package net.ultrad00d.ForgottenCantrips.cantrip;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID)
public class SpectralArmorEventHandler {

    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (event.getEffectInstance().getEffect() != EffectRegistry.SPECTRAL_ARMOR.get()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (player.getItemBySlot(slot).isEmpty()) {
                player.setItemSlot(slot, SpectralArmorTag.createSpectralStack(slot));
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

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (SpectralArmorTag.isSpectral(player.getItemBySlot(slot))) {
                player.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();

        if (!SpectralArmorTag.isSpectral(from)) return;
        if (ItemStack.matches(from, to)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        String removedUid = SpectralArmorTag.getUid(from);
        if (removedUid == null) return;

        removeByUid(player, removedUid);
    }

    private static void removeByUid(Player player, String uid) {
        if (player.containerMenu != null) {
            ItemStack carried = player.containerMenu.getCarried();
            if (uid.equals(SpectralArmorTag.getUid(carried))) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        }

        var items = player.getInventory().items;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (uid.equals(SpectralArmorTag.getUid(stack))) {
                items.set(i, ItemStack.EMPTY);
            }
        }

        double x = player.getX(), y = player.getY(), z = player.getZ();
        var area = new net.minecraft.world.phys.AABB(x - 16, y - 16, z - 16, x + 16, y + 16, z + 16);
        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (ItemEntity itemEntity : serverLevel.getEntitiesOfClass(ItemEntity.class, area)) {
                if (uid.equals(SpectralArmorTag.getUid(itemEntity.getItem()))) {
                    itemEntity.discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        ItemEntity itemEntity = event.getEntity();
        if (SpectralArmorTag.isSpectral(itemEntity.getItem())) {
            event.setCanceled(true);
            itemEntity.discard();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        boolean hasEffect = player.hasEffect(EffectRegistry.SPECTRAL_ARMOR.get());
        if (!hasEffect) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                ItemStack equipped = player.getItemBySlot(slot);
                if (SpectralArmorTag.isSpectral(equipped)) {
                    player.setItemSlot(slot, ItemStack.EMPTY);
                }
            }
        }

        var items = player.getInventory().items;
        for (int i = 0; i < items.size(); i++) {
            if (SpectralArmorTag.isSpectral(items.get(i))) {
                items.set(i, ItemStack.EMPTY);
            }
        }

        if (player.containerMenu != null) {
            ItemStack carried = player.containerMenu.getCarried();
            if (SpectralArmorTag.isSpectral(carried)) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
        }
    }
}