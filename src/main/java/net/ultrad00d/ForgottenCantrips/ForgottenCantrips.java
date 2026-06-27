package net.ultrad00d.ForgottenCantrips;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.client.gui.font.glyphs.BakedGlyph.Effect;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.ultrad00d.ForgottenCantrips.client.renderer.SpectralBoatRenderer;
import net.ultrad00d.ForgottenCantrips.config.IlluminationConfig;
import net.ultrad00d.ForgottenCantrips.block.SpectralBedBlock;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.CantripRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.PotionRegistry;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgottenCantrips.MOD_ID)
public class ForgottenCantrips {
    public static final String MOD_ID = "forgotten_cantrips"; // lowercase, no spaces, numbers, _ and -
    public static final Logger LOGGER = LogUtils.getLogger();
    
    private static final Map<UUID, BlockPos> PREVIOUS_POSITION = new HashMap<>();
    private static final Map<UUID, Set<BlockPos>> ILLUMINATION_LIGHTS = new HashMap<>();

    private static final String DISC_ROOT = "forgotten_cantrips_disc";
    private static final String DISC_ITEM = DISC_ROOT + "_item";
    private static final String DISC_START = DISC_ROOT + "_start";
    private static final String DISC_DURATION = DISC_ROOT + "_duration";
    private static final String DISC_PLAYING = DISC_ROOT + "_playing";

    private static final Map<Integer, Long> DISC_DURATIONS = Map.ofEntries(
        Map.entry(Item.getId(Items.MUSIC_DISC_13), 3700L),
        Map.entry(Item.getId(Items.MUSIC_DISC_CAT), 3700L),
        Map.entry(Item.getId(Items.MUSIC_DISC_BLOCKS), 6900L),
        Map.entry(Item.getId(Items.MUSIC_DISC_CHIRP), 3700L),
        Map.entry(Item.getId(Items.MUSIC_DISC_FAR), 3480L),
        Map.entry(Item.getId(Items.MUSIC_DISC_MALL), 3940L),
        Map.entry(Item.getId(Items.MUSIC_DISC_MELLOHI), 1920L),
        Map.entry(Item.getId(Items.MUSIC_DISC_STAL), 3000L),
        Map.entry(Item.getId(Items.MUSIC_DISC_STRAD), 3760L),
        Map.entry(Item.getId(Items.MUSIC_DISC_WARD), 5020L),
        Map.entry(Item.getId(Items.MUSIC_DISC_11), 1420L),
        Map.entry(Item.getId(Items.MUSIC_DISC_WAIT), 4760L),
        Map.entry(Item.getId(Items.MUSIC_DISC_PIGSTEP), 2960L),
        Map.entry(Item.getId(Items.MUSIC_DISC_OTHERSIDE), 3900L),
        Map.entry(Item.getId(Items.MUSIC_DISC_5), 3500L),
        Map.entry(Item.getId(Items.MUSIC_DISC_RELIC), 4360L)
    );

    public ForgottenCantrips() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IlluminationConfig.SPEC);

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

    @SubscribeEvent
    public void onLivingTick(LivingTickEvent event) {
        illuminationTick(event);
        jukeboxTick(event);
    }
    public void illuminationTick(LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        var level = player.level();
        if (level.isClientSide) return;

        var effect = player.getEffect(EffectRegistry.ILLUMINATION.get());
        if (effect == null) {
            var removed = ILLUMINATION_LIGHTS.remove(player.getUUID());
            if (removed != null)
                for (BlockPos pos : removed)
                    if (level.getBlockState(pos).is(Blocks.LIGHT))
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }

        if (player.tickCount % IlluminationConfig.TICK_PERIOD.get() != 0) return;

        int radius = Math.min(switch (effect.getAmplifier()) {
            case 0 -> 2;
            case 1 -> 3;
            default -> 4;
        } + IlluminationConfig.RADIUS_EXT.get(), IlluminationConfig.MAX_RADIUS.get());

        var center = player.blockPosition();
        if (center.equals(PREVIOUS_POSITION.get(player.getUUID()))) return;
        var current = new HashSet<BlockPos>();

        for (int x = -radius; x <= radius; x++)
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                for (int y = -1; y <= 1; y++) {
                    var pos = center.offset(x, y, z);
                    if (level.getBlockState(pos).isAir())
                        current.add(pos.immutable());
                }
            }


        var previous = ILLUMINATION_LIGHTS.getOrDefault(player.getUUID(), Set.of());

        for (BlockPos pos : previous)
            if (!current.contains(pos))
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        for (BlockPos pos : current)
            if (!previous.contains(pos))
                level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);

        ILLUMINATION_LIGHTS.put(player.getUUID(), current);
        PREVIOUS_POSITION.put(player.getUUID(), center);
    }
    public static ItemStack getStoredDisc(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(DISC_ITEM)) return ItemStack.EMPTY;
        return ItemStack.of(data.getCompound(DISC_ITEM));
    }

    public static long getDiscDuration(int itemId) {
        return DISC_DURATIONS.getOrDefault(itemId, 3700L) + 60;
    }

    public static void setStoredDisc(Player player, ItemStack disc, long startTime, long duration) {
        CompoundTag data = player.getPersistentData();
        data.put(DISC_ITEM, disc.copyWithCount(1).save(new CompoundTag()));
        data.putLong(DISC_START, startTime);
        data.putLong(DISC_DURATION, duration);
        data.putBoolean(DISC_PLAYING, true);
    }

    private static void clearStoredDisc(Player player) {
        CompoundTag data = player.getPersistentData();
        data.remove(DISC_ITEM);
        data.remove(DISC_START);
        data.remove(DISC_DURATION);
        data.remove(DISC_PLAYING);
    }

    private static void setDiscPlaying(Player player, boolean playing) {
        player.getPersistentData().putBoolean(DISC_PLAYING, playing);
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
        if (!data.contains(DISC_ITEM)) return;
        if (!data.getBoolean(DISC_PLAYING)) return;

        long start = data.getLong(DISC_START);
        long duration = data.getLong(DISC_DURATION);
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
        var player = event.getEntity();
        var removed = ILLUMINATION_LIGHTS.remove(player.getUUID());
        if (removed != null) {
            var level = player.level();
            for (BlockPos pos : removed)
                if (level.getBlockState(pos).is(Blocks.LIGHT))
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        setDiscPlaying(player, false);
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
        if (!oldData.contains(DISC_ITEM)) return;

        CompoundTag newData = player.getPersistentData();
        newData.put(DISC_ITEM, oldData.getCompound(DISC_ITEM));
        newData.putLong(DISC_START, oldData.getLong(DISC_START));
        newData.putLong(DISC_DURATION, oldData.getLong(DISC_DURATION));
        newData.remove(DISC_PLAYING);
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
