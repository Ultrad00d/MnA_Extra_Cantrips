package net.ultrad00d.ForgottenCantrips.client.renderer;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import org.jetbrains.annotations.NotNull;

public class SpectralDonkeyRenderer extends AbstractHorseRenderer<SpectralDonkey, HorseModel<SpectralDonkey>> {
    private static final ResourceLocation SPECTRAL_DONKEY_TEXTURE = ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/entity/spectral_donkey.png");

    public SpectralDonkeyRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.bakeLayer(ModelLayers.DONKEY)), 0.87F);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull SpectralDonkey entity) {
        return SPECTRAL_DONKEY_TEXTURE;
    }

    protected RenderType getRenderType(@NotNull SpectralDonkey entity, boolean visible, boolean notInvisibleToPlayer, boolean glowing) {
        return visible && !notInvisibleToPlayer && !glowing ? RenderType.entityTranslucentCull(SPECTRAL_DONKEY_TEXTURE) : super.getRenderType(entity, visible, notInvisibleToPlayer, glowing);
    }
}
