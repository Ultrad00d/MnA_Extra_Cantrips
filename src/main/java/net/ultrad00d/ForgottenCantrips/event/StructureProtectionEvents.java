package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.util.StructureUtil;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StructureProtectionEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (!player.isCreative()) {
            if (StructureUtil.isInsideProtectedStructure(event.getLevel(), event.getPos())) {
                player.sendSystemMessage(Component.literal("no."));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();
        if (StructureUtil.isInsideProtectedStructure(entity.level(), entity.blockPosition())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            event.getAffectedBlocks().removeIf(pos -> StructureUtil.isInsideProtectedStructure(level, pos));
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && !player.isCreative()) {
            if (StructureUtil.isInsideProtectedStructure(event.getLevel(), event.getPos())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player.isCreative()) return;

        // only serverside protection; clientside is left with ghost items atm
        if (StructureUtil.isInsideProtectedStructure(event.getLevel(), event.getPos())) {
            BlockState state = event.getLevel().getBlockState(event.getPos());

            // If they are right-clicking a flower pot (to insert or extract flowers)
            if (state.getBlock() instanceof FlowerPotBlock) {
                event.setCanceled(true);
            }
        }
    }
}
