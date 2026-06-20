package net.ultrad00d.ForgottenCantrips;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultrad00d.ForgottenCantrips.block.SpectralBedBlock;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.CantripRegistry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(ForgottenCantrips.MOD_ID)
public class ForgottenCantrips {
    public static final String MOD_ID = "forgotten_cantrips";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ForgottenCantrips() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRegistry.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CantripRegistry::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}
    }

    @SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {}

    private int tickCounter = 0;

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.isClientSide()) return;

        long dayTime = event.level.getDayTime() % 24000L;
        if (dayTime >= 6000L) return;

        tickCounter++;
        if (tickCounter % 5 != 0) return;

        List<BlockPos> toDestroy = new ArrayList<>();

        if (event.level instanceof ServerLevel sl) {
            for (var player : sl.players()) {
                int cx = player.getBlockX() >> 4;
                int cz = player.getBlockZ() >> 4;
                int radius = sl.getServer().getPlayerList().getViewDistance();

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        var chunk = sl.getChunkSource().getChunk(cx + dx, cz + dz, false);
                        if (chunk == null) continue;
                        chunk.getBlockEntities().forEach((pos, be) -> {
                            if (be instanceof net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity) {
                                var state = chunk.getBlockState(pos);
                                if (state.getBlock() instanceof SpectralBedBlock) {
                                    toDestroy.add(pos.immutable());
                                }
                            }
                        });
                    }
                }
            }
        }

        for (BlockPos pos : toDestroy) {
            var state = event.level.getBlockState(pos);
            if (!(state.getBlock() instanceof SpectralBedBlock)) continue;

            var facing = state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING);
            var part = state.getValue(net.minecraft.world.level.block.BedBlock.PART);
            BlockPos otherPart = part == net.minecraft.world.level.block.state.properties.BedPart.HEAD
                    ? pos.relative(facing.getOpposite())
                    : pos.relative(facing);

            event.level.destroyBlock(pos, false);
            event.level.destroyBlock(otherPart, false);
        }
    }
}
