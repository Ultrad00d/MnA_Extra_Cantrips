package net.ultrad00d.ForgottenCantrips;

import org.slf4j.Logger;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultrad00d.ForgottenCantrips.client.renderer.EmpowerRuneRenderer;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralBoatRenderer;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralDonkeyRenderer;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralSlimeRenderer;
import net.ultrad00d.ForgottenCantrips.config.IlluminationConfig;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.CantripRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;
import net.ultrad00d.ForgottenCantrips.registry.MenuRegistry;
import net.ultrad00d.ForgottenCantrips.registry.PotionRegistry;
import net.ultrad00d.ForgottenCantrips.screen.MusicDiscSlotProvider;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryScreen;
import net.ultrad00d.ForgottenCantrips.effect.IlluminationEffect;
import net.ultrad00d.ForgottenCantrips.effect.UndyingEffect;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgottenCantrips.MOD_ID)
public class ForgottenCantrips {
    public static final String MOD_ID = "forgotten_cantrips"; // lowercase, no spaces, numbers, _ and -
    public static final Logger LOGGER = LogUtils.getLogger();

    public ForgottenCantrips() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IlluminationConfig.SPEC);

        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        EffectRegistry.register(modEventBus);
        PotionRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);

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
            MinecraftForge.EVENT_BUS.register(EmpowerRuneRenderer.class);
            EntityRenderers.register(
                    EntityRegistry.SPECTRAL_BOAT.get(),
                    SpectralBoatRenderer::new
            );
            EntityRenderers.register(
                    EntityRegistry.SPECTRAL_DONKEY.get(),
                    SpectralDonkeyRenderer::new
            );
            EntityRenderers.register(
                    EntityRegistry.SPECTRAL_SLIME.get(),
                    SpectralSlimeRenderer::new
            );
            MenuScreens.register(
                    MenuRegistry.SHARED_INVENTORY_MENU.get(),
                    SharedInventoryScreen::new
            );
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent event) {
        UndyingEffect.onLivingDamage(event);
    }

    @SubscribeEvent
    public void onLivingTick(LivingTickEvent event) {
        IlluminationEffect.onLivingTick(event);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        IlluminationEffect.onPlayerLogout(event);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("forgotten_cantrips")
                .then(Commands.literal("music_disc")
                    .then(Commands.literal("drop")
                        .executes(ctx -> MusicDiscSlotProvider.dropMusicDisc(ctx.getSource().getPlayerOrException()))
                    )
                )
                .then(Commands.literal("illumination")
                .then(Commands.literal("max_radius")
                    .then(Commands.argument("value", IntegerArgumentType.integer(2, 8))
                        .executes(ctx -> setConfigValue(ctx, "Illumination.max_radius", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
                .then(Commands.literal("tick_period")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 20))
                        .executes(ctx -> setConfigValue(ctx, "Illumination.tick_period", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
                .then(Commands.literal("radius_ext")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 2))
                        .executes(ctx -> setConfigValue(ctx, "Illumination.radius_ext", IntegerArgumentType.getInteger(ctx, "value")))
                    )
                )
            )
        );
    }
    private static int setConfigValue(CommandContext<CommandSourceStack> ctx, String key, int value) {
        ForgeConfigSpec.ConfigValue<Integer> configValue = switch (key) {
            case "Illumination.max_radius" -> IlluminationConfig.MAX_RADIUS;
            case "Illumination.tick_period" -> IlluminationConfig.TICK_PERIOD;
            case "Illumination.radius_ext" -> IlluminationConfig.RADIUS_EXT;
            default -> null;
        };

        if (configValue == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown config key: " + key));
            return 0;
        }

        configValue.set(value);

        var modConfig = ConfigTracker.INSTANCE.fileMap().get(MOD_ID + "-common.toml");
        if (modConfig != null) modConfig.save();

        ctx.getSource().sendSuccess(() -> Component.literal("Set " + key + " to " + value), true);
        return 1;
    }

}
