package net.ultrad00d.ForgottenCantrips;

import com.mna.api.items.MACreativeTabs;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.CantripRegistry;
import net.ultrad00d.ForgottenCantrips.registry.ItemRegistry;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgottenCantrips.MOD_ID)
public class ForgottenCantrips {
    public static final String MOD_ID = "forgotten_cantrips"; // lowercase, no spaces, numbers, _ and -
    public static final Logger LOGGER = LogUtils.getLogger();

    public ForgottenCantrips() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CantripRegistry::register);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MACreativeTabs.GENERAL) {
            event.accept(ItemRegistry.ANCIENT_SCROLL);
        }
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
}
