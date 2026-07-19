package net.ultrad00d.ForgottenCantrips.client.renderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class EmpowerRuneRenderer
{
    private static final float RUNE_SIZE = 0.32F;
    private static final float RING_RADIUS = 0.78F;
    private static final float EFFECT_RADIUS_STEP = 0.16F;
    private static final float BASE_HEIGHT = 0.75F;
    private static final float EFFECT_HEIGHT_STEP = 0.42F;
    private static final float RUNE_ALPHA = 0.80F;

    private static final EmpowerRuneInfo[] RUNES = new EmpowerRuneInfo[] {
            new EmpowerRuneInfo(EffectRegistry.EMPOWER_DAMAGE_BUFF.get(), texture("empower_damage_buff"), 217, 72, 72, 16.0F / 22.0F, 10, 3, 6.0F),
            new EmpowerRuneInfo(EffectRegistry.EMPOWER_MANA_COST_BUFF.get(), texture("empower_mana_cost_buff"), 72, 168, 217, 16.0F / 22.0F, 10, 3, 4.0F),
            new EmpowerRuneInfo(EffectRegistry.EMPOWER_CANTRIP_BUFF.get(), texture("empower_cantrip_buff"), 143, 111, 217, 16.0F / 22.0F, 10, 3, 4.0F)
    };

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event)
    {
        Player player = event.getEntity();

        int activeEffectIndex = 0;
        for (EmpowerRuneInfo rune : RUNES)
        {
            MobEffectInstance instance = player.getEffect(rune.effect);
            if (instance == null)
            {
                continue;
            }

            int runeCount = instance.getAmplifier() + 1;
            renderEffectRunes(event, rune, runeCount, activeEffectIndex);
            activeEffectIndex++;
        }
    }

    private static void renderEffectRunes(RenderPlayerEvent.Post event, EmpowerRuneInfo rune, int runeCount, int activeEffectIndex)
    {
        if (runeCount <= 0)
        {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(rune.texture));

        float partialTick = event.getPartialTick();
        float time = event.getEntity().tickCount + partialTick;
        float height = BASE_HEIGHT + activeEffectIndex * EFFECT_HEIGHT_STEP;
        float radius = RING_RADIUS + activeEffectIndex * EFFECT_RADIUS_STEP;
        float orbitOffset = activeEffectIndex * 120.0F;
        float orbitDirection = 1.0F;
        if (activeEffectIndex % 2 == 1)
        {
            orbitDirection = -1.0F;
        }

        for (int i = 0; i < runeCount; i++)
        {
            float angle = (time * rune.orbitSpeed * orbitDirection + orbitOffset + (360.0F / runeCount) * i) * ((float)Math.PI / 180.0F);
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            float bob = (float)Math.sin(time * 0.12F + i) * 0.08F;

            poseStack.pushPose();
            poseStack.translate(x, height + bob, z);
            poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
            renderRuneQuad(poseStack, buffer, event.getPackedLight(), rune, time, i);
            poseStack.popPose();
        }
    }

    private static void renderRuneQuad(PoseStack poseStack, VertexConsumer buffer, int packedLight, EmpowerRuneInfo rune, float time, int runeIndex)
    {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f positionMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        float halfWidth = RUNE_SIZE * rune.aspectRatio / 2.0F;
        float halfHeight = RUNE_SIZE / 2.0F;
        float shimmer = 0.5F + (float)Math.sin(time * 0.22F + runeIndex * 1.7F) * 0.6F;
        float highlight = shimmer * 0.55F;
        int red = brighten(rune.red, highlight);
        int green = brighten(rune.green, highlight);
        int blue = brighten(rune.blue, highlight);
        int alpha = (int)(255 * RUNE_ALPHA * (0.75F + shimmer * 0.25F));
        int frame = ((int)(time / rune.frameTime)) % rune.frameCount;
        float minV = (float)frame / rune.frameCount;
        float maxV = (float)(frame + 1) / rune.frameCount;

        vertex(buffer, positionMatrix, normalMatrix, -halfWidth, -halfHeight, 0.0F, 0.0F, maxV, packedLight, red, green, blue, alpha);
        vertex(buffer, positionMatrix, normalMatrix, halfWidth, -halfHeight, 0.0F, 1.0F, maxV, packedLight, red, green, blue, alpha);
        vertex(buffer, positionMatrix, normalMatrix, halfWidth, halfHeight, 0.0F, 1.0F, minV, packedLight, red, green, blue, alpha);
        vertex(buffer, positionMatrix, normalMatrix, -halfWidth, halfHeight, 0.0F, 0.0F, minV, packedLight, red, green, blue, alpha);
    }

    private static int brighten(int color, float amount)
    {
        return (int)(color + (255 - color) * amount);
    }

    private static void vertex(VertexConsumer buffer, Matrix4f positionMatrix, Matrix3f normalMatrix, float x, float y, float z, float u, float v, int packedLight, int red, int green, int blue, int alpha)
    {
        buffer.vertex(positionMatrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static ResourceLocation texture(String name)
    {
        return fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/particle/" + name + ".png");
    }

    private static class EmpowerRuneInfo
    {
        private final MobEffect effect;
        private final ResourceLocation texture;
        private final int red;
        private final int green;
        private final int blue;
        private final float aspectRatio;
        private final int frameCount;
        private final int frameTime;
        private final float orbitSpeed;

        private EmpowerRuneInfo(MobEffect effect, ResourceLocation texture, int red, int green, int blue, float aspectRatio, int frameCount, int frameTime, float orbitSpeed)
        {
            this.effect = effect;
            this.texture = texture;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.aspectRatio = aspectRatio;
            this.frameCount = frameCount;
            this.frameTime = frameTime;
            this.orbitSpeed = orbitSpeed;
        }
    }
}
