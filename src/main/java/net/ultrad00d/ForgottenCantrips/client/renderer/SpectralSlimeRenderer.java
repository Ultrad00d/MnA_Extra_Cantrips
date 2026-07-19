package net.ultrad00d.ForgottenCantrips.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.SpectralSlime;
import org.jetbrains.annotations.NotNull;

public class SpectralSlimeRenderer extends MobRenderer<SpectralSlime, SlimeModel<SpectralSlime>> {
    private static final ResourceLocation SPECTRAL_SLIME_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/entity/spectral_slime.png");

    public SpectralSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        this.addLayer(new OuterLayer(this, context.getModelSet()));
    }

    @Override
    public void render(@NotNull SpectralSlime slime, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = 0.25F * (float) slime.getSize();
        super.render(slime, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(@NotNull SpectralSlime slime, @NotNull PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.999F, 0.999F, 0.999F);
        poseStack.translate(0.0F, 0.001F, 0.0F);
        float size = (float) slime.getSize();
        float squish = Mth.lerp(partialTickTime, slime.oSquish, slime.squish) / (size * 0.5F + 1.0F);
        float inverseSquish = 1.0F / (squish + 1.0F);
        poseStack.scale(inverseSquish * size, 1.0F / inverseSquish * size, inverseSquish * size);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SpectralSlime slime) {
        return SPECTRAL_SLIME_TEXTURE;
    }

    private static class OuterLayer extends RenderLayer<SpectralSlime, SlimeModel<SpectralSlime>> {
        private static final float ALPHA = 0.55F;
        private final EntityModel<SpectralSlime> model;

        public OuterLayer(RenderLayerParent<SpectralSlime, SlimeModel<SpectralSlime>> renderer, EntityModelSet modelSet) {
            super(renderer);
            this.model = new SlimeModel<>(modelSet.bakeLayer(ModelLayers.SLIME_OUTER));
        }

        @Override
        public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull SpectralSlime slime, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean glowingInvisible = minecraft.shouldEntityAppearGlowing(slime) && slime.isInvisible();
            if (slime.isInvisible() && !glowingInvisible) {
                return;
            }

            RenderType renderType = glowingInvisible
                    ? RenderType.outline(this.getTextureLocation(slime))
                    : RenderType.entityTranslucent(this.getTextureLocation(slime));
            VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(slime, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(slime, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(slime, 0.0F), 1.0F, 1.0F, 1.0F, ALPHA);
        }
    }
}
