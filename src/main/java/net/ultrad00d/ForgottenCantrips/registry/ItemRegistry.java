package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.item.*;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<Item> MEMORY_EMERALD = registerItem(MemoryEmeraldItem.NAME,
            MemoryEmeraldItem::new);

    public static final RegistryObject<Item> SPECTRAL_HELMET = registerItem("spectral_helmet",
            () -> new SpectralArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_CHESTPLATE = registerItem("spectral_chestplate",
            () -> new SpectralArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_LEGGINGS = registerItem("spectral_leggings",
            () -> new SpectralArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_BOOTS = registerItem("spectral_boots",
            () -> new SpectralArmorItem(SpectralArmorMaterial.SPECTRAL_ARMOR, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> SPECTRAL_SLIME_BALL = registerItem("spectral_slime_ball",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ANCIENT_SCROLL = registerItem("ancient_scroll",
            () -> new AncientScrollItem(new Item.Properties()));

    public static final RegistryObject<Item> OLD_WIZARD_ICON = registerItem("old_wizard_icon",
            () -> new Item(new Item.Properties()));


    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
