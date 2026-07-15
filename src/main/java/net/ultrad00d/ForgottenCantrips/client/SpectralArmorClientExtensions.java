package net.ultrad00d.ForgottenCantrips.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralArmorModel;
import org.jetbrains.annotations.NotNull;

public class SpectralArmorClientExtensions implements IClientItemExtensions {
    @Override
    public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> original) {
        ModelPart modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(
                armorSlot == EquipmentSlot.LEGS ? ModelLayers.PLAYER_INNER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR
        );
        String layerName = (armorSlot == EquipmentSlot.LEGS) ? "layer_2" : "layer_1";
        ResourceLocation correctTexture = ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/models/armor/spectral_" + layerName + ".png");

        return new SpectralArmorModel(modelPart, (HumanoidModel<LivingEntity>) original, correctTexture);
    }
}
