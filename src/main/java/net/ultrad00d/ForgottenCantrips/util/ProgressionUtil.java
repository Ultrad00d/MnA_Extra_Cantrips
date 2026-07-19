package net.ultrad00d.ForgottenCantrips.util;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class ProgressionUtil {
    public static int getPlayerTier(Player player) {
        return player.getCapability(PlayerProgressionProvider.PROGRESSION)
                .map(IPlayerProgression::getTier)
                .orElse(0);
    }

    public static boolean hasAdvancement(Player player, ResourceLocation advancementId) {
        if (player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = serverPlayer.getServer().getAdvancements().getAdvancement(advancementId);
            if (advancement != null) return serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone();
        }
        return false;
    }

    public static void awardAdvancement(Player player, ResourceLocation advancementId) {
        if (player instanceof ServerPlayer serverPlayer) {
            Advancement advancement = serverPlayer.getServer().getAdvancements().getAdvancement(advancementId);
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, "criterion");
            }
        }
    }
}
