package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public interface ICantripLogic {
    void run(Player player, ICantrip cantrip, InteractionHand hand);
    String getCantripId();

    default String getLangKey(String key) {
        return "cantrip." + ForgottenCantrips.MOD_ID + "." + this.getCantripId() + "." + key;
    }
}
