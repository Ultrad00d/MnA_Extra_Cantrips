package net.ultrad00d.ForgottenCantrips.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.client.OldWizardModel;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class OldWizardRenderer extends GeoEntityRenderer<OldWizard> {
    public OldWizardRenderer(EntityRendererProvider.Context context) {
        super(context, new OldWizardModel());
    }

    @Override
    public ResourceLocation getTextureLocation(OldWizard animatable) {
        return fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/entity/wizard.png");
    }

//    could be used to scale entity (make a baby)
//    @Override
//    public void render(OldWizard entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
//        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//    }
}
