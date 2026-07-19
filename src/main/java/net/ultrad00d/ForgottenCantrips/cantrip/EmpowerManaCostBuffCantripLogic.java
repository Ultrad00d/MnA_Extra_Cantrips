package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class EmpowerManaCostBuffCantripLogic implements IEmpowerCantripLogic, ICantripLogic {
    @Override
    public String getCantripId() { return "empower_mana_cost_buff"; }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand)
    {
        applyBuff(player, EffectRegistry.EMPOWER_MANA_COST_BUFF);
    }
}
