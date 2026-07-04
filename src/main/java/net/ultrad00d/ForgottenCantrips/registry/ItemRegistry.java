// ItemsRegistry.java
package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<Item> SPECTRAL_HELMET = ITEMS.register("spectral_helmet",
            () -> new ArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_CHESTPLATE = ITEMS.register("spectral_chestplate",
            () -> new ArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_LEGGINGS = ITEMS.register("spectral_leggings",
            () -> new ArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_BOOTS = ITEMS.register("spectral_boots",
            () -> new ArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}