package net.ultrad00d.ForgottenCantrips.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class SpectralArmorModel extends HumanoidModel<LivingEntity> {
    private final HumanoidModel<LivingEntity> original;
    private final ResourceLocation armorTexture;

    public SpectralArmorModel(ModelPart root, HumanoidModel<LivingEntity> original, ResourceLocation armorTexture) {
        super(root);
        this.original = original;
        this.armorTexture = armorTexture;
    }

    // Copy those animated body part rotations over to custom model
    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.original != null) {
            this.original.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.copyPropertiesTo(this.original);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        VertexConsumer translucentConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.armorTexture));

        float customAlpha = 0.6f;

        // Render the inner/original parts through our forced translucent buffer
        if (this.original != null) {
            this.original.renderToBuffer(poseStack, translucentConsumer, packedLight, packedOverlay, red, green, blue, customAlpha);
        } else {
            super.renderToBuffer(poseStack, translucentConsumer, packedLight, packedOverlay, red, green, blue, customAlpha);
        }
    }
}
