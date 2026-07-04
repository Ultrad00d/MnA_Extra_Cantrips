package net.ultrad00d.ForgottenCantrips;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.ultrad00d.ForgottenCantrips.registry.*;
import org.slf4j.Logger;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
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
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralBoatRenderer;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralDonkeyRenderer;
import net.ultrad00d.ForgottenCantrips.config.IlluminationConfig;
import net.ultrad00d.ForgottenCantrips.screen.SharedInventoryScreen;
import net.ultrad00d.ForgottenCantrips.definitions.MusicDiscDefinitions;
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
        ItemsRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        EffectRegistry.register(modEventBus);
        PotionRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

    }

    private static void setDiscPlaying(Player player, boolean playing) {
        player.getPersistentData().putBoolean(MusicDiscDefinitions.DISC_PLAYING, playing);
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
            EntityRenderers.register(
                    EntityRegistry.SPECTRAL_BOAT.get(),
                    SpectralBoatRenderer::new
            );
            EntityRenderers.register(
                    EntityRegistry.SPECTRAL_DONKEY.get(),
                    SpectralDonkeyRenderer::new
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
        jukeboxTick(event);
    }
    
    public static ItemStack getStoredDisc(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(MusicDiscDefinitions.DISC_ITEM)) return ItemStack.EMPTY;
        return ItemStack.of(data.getCompound(MusicDiscDefinitions.DISC_ITEM));
    }

    public static long getDiscDuration(int itemId) {
        return MusicDiscDefinitions.DISC_DURATIONS.getOrDefault(itemId, 3700L) + 60;
    }

    public static void setStoredDisc(Player player, ItemStack disc, long startTime, long duration) {
        CompoundTag data = player.getPersistentData();
        data.put(MusicDiscDefinitions.DISC_ITEM, disc.copyWithCount(1).save(new CompoundTag()));
        data.putLong(MusicDiscDefinitions.DISC_START, startTime);
        data.putLong(MusicDiscDefinitions.DISC_DURATION, duration);
        data.putBoolean(MusicDiscDefinitions.DISC_PLAYING, true);
    }

    private static void clearStoredDisc(Player player) {
        CompoundTag data = player.getPersistentData();
        data.remove(MusicDiscDefinitions.DISC_ITEM);
        data.remove(MusicDiscDefinitions.DISC_START);
        data.remove(MusicDiscDefinitions.DISC_DURATION);
        data.remove(MusicDiscDefinitions.DISC_PLAYING);
    }

    public static void stopDiscSound(Player player) {
        if (!(player instanceof ServerPlayer sp)) return;
        var packet = new ClientboundStopSoundPacket((ResourceLocation) null, SoundSource.RECORDS);
        var level = sp.serverLevel();
        level.getChunkSource().chunkMap.broadcast(sp, packet);
        sp.connection.send(packet);
    }

    private void jukeboxTick(LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;

        CompoundTag data = player.getPersistentData();
        if (!data.contains(MusicDiscDefinitions.DISC_ITEM)) return;
        if (!data.getBoolean(MusicDiscDefinitions.DISC_PLAYING)) return;

        long start = data.getLong(MusicDiscDefinitions.DISC_START);
        long duration = data.getLong(MusicDiscDefinitions.DISC_DURATION);
        long elapsed = level.getGameTime() - start;
        if (elapsed < duration + 10) return;

        stopDiscSound(player);
        ItemStack disc = getStoredDisc(player);
        if (!disc.isEmpty()) {
            player.drop(disc, false);
        }
        clearStoredDisc(player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        IlluminationEffect.onPlayerLogout(event);
        setDiscPlaying(event.getEntity(), false);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        setDiscPlaying(event.getEntity(), false);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        Player original = event.getOriginal();
        Player player = event.getEntity();
        CompoundTag oldData = original.getPersistentData();
        if (!oldData.contains(MusicDiscDefinitions.DISC_ITEM)) return;

        CompoundTag newData = player.getPersistentData();
        newData.put(MusicDiscDefinitions.DISC_ITEM, oldData.getCompound(MusicDiscDefinitions.DISC_ITEM));
        newData.putLong(MusicDiscDefinitions.DISC_START, oldData.getLong(MusicDiscDefinitions.DISC_START));
        newData.putLong(MusicDiscDefinitions.DISC_DURATION, oldData.getLong(MusicDiscDefinitions.DISC_DURATION));
        newData.remove(MusicDiscDefinitions.DISC_PLAYING);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;
        stopDiscSound(player);
        if (level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            setDiscPlaying(player, false);
            return;
        }
        ItemStack disc = getStoredDisc(player);
        if (!disc.isEmpty()) {
            player.drop(disc, false);
            clearStoredDisc(player);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("forgotten_cantrips")
                .then(Commands.literal("music_disc")
                    .then(Commands.literal("drop")
                        .executes(ctx -> dropMusicDisc(ctx.getSource().getPlayerOrException()))
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

    private static int dropMusicDisc(Player player) {
        ItemStack disc = getStoredDisc(player);
        if (disc.isEmpty()) {
            player.sendSystemMessage(Component.translatable("command.forgotten_cantrips.music_disc.drop.empty"));
            return 0;
        }
        stopDiscSound(player);
        clearStoredDisc(player);
        player.drop(disc, false);
        player.sendSystemMessage(Component.translatable("command.forgotten_cantrips.music_disc.drop.success"));
        return 1;
    }
}
