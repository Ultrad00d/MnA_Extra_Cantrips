package net.ultrad00d.ForgottenCantrips.integration;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class Helper {

    public static int getPlayerTier(Player player) {
        return player.getCapability(PlayerProgressionProvider.PROGRESSION)
                .map(IPlayerProgression::getTier)
                .orElse(0);
    }

    public static boolean isInBubbleColumn(Player player) {
        BlockPos pos = player.blockPosition();
        return player.level().getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
    }
}
