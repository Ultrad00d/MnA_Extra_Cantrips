package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class EmpowerManaCostBuffCantripLogic implements IEmpowerCantripLogic, ICantripLogic
{
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand)
    {
        applyBuff(player, EffectRegistry.MANA_COST_BUFF);
    }
}
