package net.ultrad00d.ForgottenCantrips.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.definitions.MusicDiscDefinitions;
import net.ultrad00d.ForgottenCantrips.screen.MusicDiscSlotProvider;

@Mod.EventBusSubscriber(modid = ForgottenCantrips.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MusicDiscSlotEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingTickEvent event) {
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

        MusicDiscSlotProvider.stopDiscSound(player);
        ItemStack disc = MusicDiscSlotProvider.getStoredDisc(player);
        if (!disc.isEmpty()) {
            player.drop(disc, false);
        }
        MusicDiscSlotProvider.clearStoredDisc(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerLoggedOutEvent event) {
        MusicDiscSlotProvider.setDiscPlaying(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        MusicDiscSlotProvider.setDiscPlaying(event.getEntity(), false);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
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
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;
        MusicDiscSlotProvider.stopDiscSound(player);
        if (level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            MusicDiscSlotProvider.setDiscPlaying(player, false);
            return;
        }
        ItemStack disc = MusicDiscSlotProvider.getStoredDisc(player);
        if (!disc.isEmpty()) {
            player.drop(disc, false);
            MusicDiscSlotProvider.clearStoredDisc(player);
        }
    }
}
