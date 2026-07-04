package net.ultrad00d.ForgottenCantrips.cantrip;

import java.util.UUID;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.registry.ItemsRegistry;

public class SpectralArmorCantripLogic extends CantripLogic {

    private static final String TAG_ID = "forgotten_cantrips_spectral_id";

    private static final int EXPIRE_TICKS = 15 * 40;

    private static final double SEARCH_RADIUS = 16.0D;

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        boolean armorApplied = false;
        String spectralId = UUID.randomUUID().toString();

        // AC3 (заготовка): защита должна масштабироваться от tier/level магии игрока.
        // Пока нет доступа к реальному API прогрессии mna, используем заглушку.
        // Когда появится реальный метод получения магического уровня игрока,
        // нужно заменить getMagicTierPlaceholder(...) на настоящий вызов.
        int magicTier = getMagicTierPlaceholder(player);

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsRegistry.SPECTRAL_HELMET.get()));
            armorApplied = true;
        }

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) {
            player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemsRegistry.SPECTRAL_CHESTPLATE.get()));
            armorApplied = true;
        }

        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) {
            player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemsRegistry.SPECTRAL_LEGGINGS.get()));
            armorApplied = true;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) {
            player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemsRegistry.SPECTRAL_BOOTS.get()));
            armorApplied = true;
        }

        if (armorApplied) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARMOR_EQUIP_LEATHER, player.getSoundSource(), 1.0F, 1.0F);

            if (player.level() instanceof ServerLevel serverLevel) {
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();

                DelayedEventQueue.pushEvent(
                    serverLevel,
                    new TimedDelayedEvent<>(
                        spectralId + "_spectral_armor_expire",
                        EXPIRE_TICKS,
                        null,
                        (id, data) -> expireSpectralArmor(serverLevel, player, spectralId, x, y, z)
                    )
                );
            }
        } else {
            player.sendSystemMessage(Component.translatable("cantrip." + ForgottenCantrips.MOD_ID + ".leather_armor.full"));
        }
    }

    /**
     * AC3 заглушка: замени на реальный метод получения уровня/tier магии игрока
     * из API mna, когда он станет доступен (например, что-то вроде
     * PlayerMagicData.get(player).getTier() или аналог).
     */
    private static int getMagicTierPlaceholder(Player player) {
        return 1;
    }

    private static boolean isTaggedStack(ItemStack stack, String spectralId) {
        return !stack.isEmpty()
            && stack.hasTag()
            && spectralId.equals(stack.getTag().getString(TAG_ID));
    }

    private static void expireSpectralArmor(ServerLevel serverLevel, Player player, String spectralId, double x, double y, double z) {
        boolean removedFromPlayer = false;

        // 1. Проверяем слоты брони игрока
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack equipped = player.getItemBySlot(slot);
            if (isTaggedStack(equipped, spectralId)) {
                player.setItemSlot(slot, ItemStack.EMPTY);
                removedFromPlayer = true;
            }
        }

        // 2. Проверяем инвентарь игрока (если снял и положил в сумку)
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (isTaggedStack(stack, spectralId)) {
                inventory.setItem(i, ItemStack.EMPTY);
                removedFromPlayer = true;
            }
        }

        AABB area = new AABB(x - SEARCH_RADIUS, y - SEARCH_RADIUS, z - SEARCH_RADIUS,
                              x + SEARCH_RADIUS, y + SEARCH_RADIUS, z + SEARCH_RADIUS);
        for (ItemEntity itemEntity : serverLevel.getEntitiesOfClass(ItemEntity.class, area)) {
            if (isTaggedStack(itemEntity.getItem(), spectralId)) {
                itemEntity.discard();
            }
        }

        if (removedFromPlayer) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_BREAK, player.getSoundSource(), 0.7F, 1.0F);
        }
    }
}