package net.ultrad00d.ForgottenCantrips.client.renderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;

public class SpectralBoatRenderer extends EntityRenderer<SpectralBoat>
{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/entity/spectral_boat.png");

    private final BoatModel model;

    public SpectralBoatRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        shadowRadius = 0.8F;
        model = new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK)));
    }

    @Override
    public void render(SpectralBoat boat, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight)
    {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTime = boat.getHurtTime() - partialTicks;
        float damage = Math.max(boat.getDamage() - partialTicks, 0.0F);
        if (hurtTime > 0.0F)
        {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * boat.getHurtDir()));
        }

        float bubbleAngle = boat.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F))
        {
            poseStack.mulPose(new Quaternionf().setAngleAxis(bubbleAngle * ((float) Math.PI / 180.0F), 1.0F, 0.0F, 1.0F));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        model.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);

        VertexConsumer boatBuffer = buffer.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(
                poseStack,
                boatBuffer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        if (!boat.isUnderWater())
        {
            VertexConsumer waterMask = buffer.getBuffer(RenderType.waterMask());
            model.waterPatch().render(poseStack, waterMask, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
        super.render(boat, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SpectralBoat boat)
    {
        return TEXTURE;
    }
}
