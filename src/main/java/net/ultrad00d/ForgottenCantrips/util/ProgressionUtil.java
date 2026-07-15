package net.ultrad00d.ForgottenCantrips.util;

import com.mna.api.capabilities.IPlayerProgression;
import com.mna.capabilities.playerdata.progression.PlayerProgressionProvider;
import net.minecraft.world.entity.player.Player;

public class ProgressionUtil {
    public static int getPlayerTier(Player player) {
        return player.getCapability(PlayerProgressionProvider.PROGRESSION)
                .map(IPlayerProgression::getTier)
                .orElse(0);
    }
}
