package net.ultrad00d.ForgottenCantrips.cantrip;

import java.util.UUID;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.ultrad00d.ForgottenCantrips.registry.ItemsRegistry;

public final class SpectralArmorTag {

    public static final String TAG_FLAG = "forgotten_cantrips_spectral_armor";
    public static final String TAG_UID = "forgotten_cantrips_spectral_uid";

    private SpectralArmorTag() {}

    public static boolean isSpectral(ItemStack stack) {
        return !stack.isEmpty() && stack.hasTag() && stack.getTag().getBoolean(TAG_FLAG);
    }

    public static String getUid(ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(TAG_UID)) {
            return stack.getTag().getString(TAG_UID);
        }
        return null;
    }

    public static ItemStack createSpectralStack(EquipmentSlot slot) {
        Item item = switch (slot) {
            case HEAD -> ItemsRegistry.SPECTRAL_HELMET.get();
            case CHEST -> ItemsRegistry.SPECTRAL_CHESTPLATE.get();
            case LEGS -> ItemsRegistry.SPECTRAL_LEGGINGS.get();
            case FEET -> ItemsRegistry.SPECTRAL_BOOTS.get();
            default -> throw new IllegalArgumentException("Not armor slot: " + slot);
        };
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putBoolean(TAG_FLAG, true);
        stack.getTag().putString(TAG_UID, UUID.randomUUID().toString());
        return stack;
    }
}