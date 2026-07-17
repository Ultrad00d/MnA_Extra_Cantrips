package net.ultrad00d.ForgottenCantrips.util;

import net.minecraft.world.level.Level;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class TimeUtil {
    public static boolean isFishingDay(Level level) {
        long gameTime = level.getGameTime();
        long currentDay = (ForgottenCantrips.DEBUG_DAY_OVERRIDE != -1)
                ? ForgottenCantrips.DEBUG_DAY_OVERRIDE
                : (gameTime / 24000L);

        return currentDay % 3 == 0;
    }
}
