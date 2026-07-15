package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public enum SpectralArmorMaterial implements ArmorMaterial {
    SPECTRAL_ARMOR("spectral", 26, new int[]{1, 4, 2, 1}, 0f, SoundEvents.ARMOR_EQUIP_CHAIN);
    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final SoundEvent equipSound;
    private final float toughness;

    private static final int[] BASE_DURABILITY = {1, 1, 1, 1};

    SpectralArmorMaterial(String name, int durabilityMultiplier, int[] protectionAmounts, float toughness, SoundEvent equipSound) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.equipSound = equipSound;
        this.toughness = toughness;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {
        return BASE_DURABILITY[pType.ordinal()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type pType) {
        return this.protectionAmounts[pType.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return ForgottenCantrips.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }
    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
