package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class ColossusOakCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "colossus_oak"; }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        HitResult hitResult = player.pick(player.getBlockReach(), 0.0F, false);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable(getLangKey("toofar")));
            return;
        }

        BlockPos hitPos = ((BlockHitResult) hitResult).getBlockPos();
        ColossusOakGrower grower = new ColossusOakGrower();
        ColossusOakGrower.Outcome outcome = grower.grow(serverLevel, hitPos);

        player.sendSystemMessage(Component.translatable(getLangKey(langKeyFor(outcome))));
    }

    private static String langKeyFor(ColossusOakGrower.Outcome outcome) {
        return switch (outcome) {
            case GREW -> "grew";
            case BRANCHED -> "branched";
            case ROOT_DESTROYED -> "root_destroyed";
            case NOT_GROWABLE -> "not_growable";
            case CAST_ON_LEAF -> "cast_on_leaf";
        };
    }
}
