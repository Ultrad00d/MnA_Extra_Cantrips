package net.ultrad00d.ForgottenCantrips.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class HelperUtil {
    public static boolean isInBubbleColumn(Player player) {
        BlockPos pos = player.blockPosition();
        return player.level().getBlockState(pos).is(Blocks.BUBBLE_COLUMN);
    }
}
