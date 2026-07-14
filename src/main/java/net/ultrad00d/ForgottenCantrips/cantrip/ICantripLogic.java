package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface ICantripLogic {
    void run(Player player, ICantrip cantrip, InteractionHand hand);
}
