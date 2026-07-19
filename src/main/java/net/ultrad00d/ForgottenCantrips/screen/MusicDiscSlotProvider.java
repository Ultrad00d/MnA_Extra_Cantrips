package net.ultrad00d.ForgottenCantrips.screen;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.definitions.MusicDiscDefinitions;

public class MusicDiscSlotProvider {

    public static void setDiscPlaying(Player player, boolean playing) {
        player.getPersistentData().putBoolean(MusicDiscDefinitions.DISC_PLAYING, playing);
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

    public static void clearStoredDisc(Player player) {
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

    public static void playMusicDisc(Player player, ItemStack disc, Level level) {
        MusicDiscSlotProvider.stopDiscSound(player);
        ItemStack old = MusicDiscSlotProvider.getStoredDisc(player);
        if (!old.isEmpty()) {
            player.drop(old, false);
        }

        RecordItem record = (RecordItem) disc.getItem();
        long duration = MusicDiscSlotProvider.getDiscDuration(Item.getId(disc.getItem()));
        MusicDiscSlotProvider.setStoredDisc(player, disc, level.getGameTime(), duration);

        var sound = record.getSound();
        var packet = new ClientboundSoundEntityPacket(
            Holder.direct(sound),
            SoundSource.RECORDS,
            player,
            1.0F, 1.0F,
            player.getRandom().nextLong()
        );
        ((ServerLevel) level).getChunkSource().chunkMap.broadcast(player, packet);
        if (player instanceof ServerPlayer sp) sp.connection.send(packet);
    }

    public static int dropMusicDisc(Player player) {
        ItemStack disc = getStoredDisc(player);
        if (disc.isEmpty()) {
            player.sendSystemMessage(Component.translatable("command." + ForgottenCantrips.MOD_ID + ".music_disc.drop.empty"));
            return 0;
        }
        stopDiscSound(player);
        clearStoredDisc(player);
        player.drop(disc, false);
        player.sendSystemMessage(Component.translatable("command." + ForgottenCantrips.MOD_ID + ".music_disc.drop.success"));
        return 1;
    }
}
