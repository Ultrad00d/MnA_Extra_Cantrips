package net.ultrad00d.ForgottenCantrips.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.ultrad00d.ForgottenCantrips.client.SpectralArmorClientExtensions;

import java.util.function.Consumer;

public class SpectralArmorItem extends ArmorItem {
    public SpectralArmorItem(ArmorMaterial pMaterial, ArmorItem.Type pType, Item.Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        // Point Forge to your custom Client Extensions configuration here!
        consumer.accept(new SpectralArmorClientExtensions());
    }
}