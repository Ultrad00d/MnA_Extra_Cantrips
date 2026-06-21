package net.ultrad00d.ForgottenCantrips;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultrad00d.ForgottenCantrips.block.SpectralBedBlock;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.CantripRegistry;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgottenCantrips.MOD_ID)
public class ForgottenCantrips {
    public static final String MOD_ID = "forgotten_cantrips"; // lowercase, no spaces, numbers, _ and -
    public static final Logger LOGGER = LogUtils.getLogger();

    public ForgottenCantrips() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);

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

        }
    }

    @SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {

    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (event.getEntity().level().isClientSide) return;
        var player = event.getEntity();
        BlockPos bedPos = player.getSleepingPos().orElse(null);
        if (bedPos == null) return;
        var be = player.level().getBlockEntity(bedPos);
        if (be instanceof SpectralBedBlockEntity spectralBed) {
            spectralBed.markUsed();
        }
    }

    private static final Set<BlockPos> spectralBeds = new HashSet<>();

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.isClientSide) return;
        long dayTime = event.level.getDayTime() % 24000L;
        if (dayTime >= 13000L) return;
        if (!(event.level instanceof net.minecraft.server.level.ServerLevel serverLevel)) return;
        Set<BlockPos> toRemove = new HashSet<>();
        for (BlockPos pos : spectralBeds) {
            var be = serverLevel.getBlockEntity(pos);
            if (!(be instanceof SpectralBedBlockEntity spectralBed)) {
                toRemove.add(pos);
                continue;
            }
            if (spectralBed.isUsed()) {
                toRemove.add(pos);
                removeSpectralBed(serverLevel, pos);
            } else {
                BlockState state = be.getBlockState();
                if (state.getBlock() instanceof BedBlock && !state.getValue(BedBlock.OCCUPIED)) {
                    toRemove.add(pos);
                    removeSpectralBed(serverLevel, pos);
                }
            }
        }
        spectralBeds.removeAll(toRemove);
    }

    public static void trackBed(BlockPos headPos) {
        spectralBeds.add(headPos);
    }

    private static void removeSpectralBed(Level level, BlockPos headPos) {
        BlockState headState = level.getBlockState(headPos);
        if (headState.getBlock() instanceof BedBlock) {
            level.removeBlock(headPos, false);
            BlockPos footPos = headPos.relative(headState.getValue(BedBlock.FACING).getOpposite());
            BlockState footState = level.getBlockState(footPos);
            if (footState.is(BlockRegistry.SPECTRAL_BED.get())) {
                level.removeBlock(footPos, false);
            }
        }
    }
}
