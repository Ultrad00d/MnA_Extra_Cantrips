package net.ultrad00d.ForgottenCantrips;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.gui.font.glyphs.BakedGlyph.Effect;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralBoatRenderer;
import net.ultrad00d.ForgottenCantrips.block.SpectralBedBlock;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.CantripRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.PotionRegistry;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgottenCantrips.MOD_ID)
public class ForgottenCantrips {
    public static final String MOD_ID = "forgotten_cantrips"; // lowercase, no spaces, numbers, _ and -
    public static final Logger LOGGER = LogUtils.getLogger();

    public ForgottenCantrips() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        BlockRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        EffectRegistry.register(modEventBus);
        PotionRegistry.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CantripRegistry::register);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> EntityRenderers.register(
                    EntityRegistry.SPECTRAL_BOAT.get(),
                    SpectralBoatRenderer::new
            ));
        }
    }

    @SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {

    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(EffectRegistry.UNDYING.get());
        if (effect == null) return;

        if (entity.getHealth() - event.getAmount() > 1e-5) return;

        event.setCanceled(true);
        entity.removeEffect(EffectRegistry.UNDYING.get());
        entity.setHealth(1.0F);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 2));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 25, 2));
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.TOTEM_USE, entity.getSoundSource(), 1.0F, 1.0F);

        if (entity instanceof ServerPlayer sp) {
            var packet = new ClientboundLevelParticlesPacket(
                ParticleTypes.TOTEM_OF_UNDYING, true,
                sp.getX(), sp.getY() + 1.0, sp.getZ(),
                0.7f, 1.2f, 0.7f, 0.7f, 60
            );
            sp.connection.send(packet);
        }
    }
}
