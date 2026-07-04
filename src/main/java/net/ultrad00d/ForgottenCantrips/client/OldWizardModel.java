package net.ultrad00d.ForgottenCantrips.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class OldWizardModel extends GeoModel<OldWizard> {
    @Override
    public ResourceLocation getModelResource(OldWizard animatable) {
        return fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "geo/wizard.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(OldWizard animatable) {
        return fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/entity/wizard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(OldWizard animatable) {
        return fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "animations/wizard.animation.json");
    }

    @Override
    public void setCustomAnimations(OldWizard animatable, long instanceId, AnimationState<OldWizard> animationState) {
        if (!animatable.isHeadLocked()) facePlayer(animationState);
    }

    private void facePlayer(AnimationState<OldWizard> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityModelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityModelData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityModelData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
