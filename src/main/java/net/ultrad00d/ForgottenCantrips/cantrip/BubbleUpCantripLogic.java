package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class BubbleUpCantripLogic extends CantripLogic {
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!player.isUnderWater()) {
            player.sendSystemMessage(
                    Component.translatable("cantrip.forgotten_cantrips.bubble_up.not_submerged"));
            return;
        }

        player.addEffect(new MobEffectInstance(
                EffectRegistry.BUBBLE_UP.get(),
                600,
                0,
                false,
                true,
                true
        ));
    }
}