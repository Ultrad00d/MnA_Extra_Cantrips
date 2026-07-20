package net.ultrad00d.ForgottenCantrips.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.ultrad00d.ForgottenCantrips.client.SpectralArmorClientExtensions;

import java.util.function.Consumer;

public class SpectralArmorItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6A"), // BOOTS
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0E"), // LEGS
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48D"), // CHEST
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB14F")  // HEAD
    };

    public SpectralArmorItem(ArmorMaterial pMaterial, ArmorItem.Type pType, Item.Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        HashMultimap<Attribute, AttributeModifier> multimap = HashMultimap.create(super.getAttributeModifiers(slot, stack));
        if (slot != this.getEquipmentSlot()) return multimap;

        int tier = 0;
        if (stack.hasTag() && stack.getTag().contains("SpectralTier")) {
            tier = stack.getTag().getInt("SpectralTier");
        }
        multimap.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()], "Spectral Defence", tier, AttributeModifier.Operation.ADDITION));
        return multimap;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SpectralArmorClientExtensions());
    }
}