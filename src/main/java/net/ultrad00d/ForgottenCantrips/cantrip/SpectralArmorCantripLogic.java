package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class SpectralArmorCantripLogic extends CantripLogic {
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        player.addEffect(new MobEffectInstance(
                EffectRegistry.SPECTRAL_ARMOR.get(),
                300,
                0,
                false,
                true,
                true
        ));
    }
}