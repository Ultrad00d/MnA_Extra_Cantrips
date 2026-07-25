package net.ultrad00d.ForgottenCantrips.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.ultrad00d.ForgottenCantrips.entity.SpectralSlimeSpit;
import org.jetbrains.annotations.NotNull;

public class SpectralSlimeSpitRenderer extends ThrownItemRenderer<SpectralSlimeSpit> {
    public SpectralSlimeSpitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull SpectralSlimeSpit entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float scale = entity.getSpitScale();
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
