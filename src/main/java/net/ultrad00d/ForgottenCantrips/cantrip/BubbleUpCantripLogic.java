package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;

public class BubbleUpCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "bubble_up"; }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!player.isUnderWater()) {
            player.sendSystemMessage(Component.translatable(getLangKey("not_submerged")));
            return;
        }

        if (isInBubbleColumn(player)) {
            player.sendSystemMessage(Component.translatable(getLangKey("bubble_column")));
            return;
        }

        if (!hasEnoughWaterDepth(player)) {
            player.sendSystemMessage(Component.translatable(getLangKey("shallow_water")));
            return;
        }

        player.addEffect(new MobEffectInstance(
                EffectRegistry.BUBBLE_UP.get(),
                240,
                0,
                false,
                true,
                true
        ));
    }

    private boolean hasEnoughWaterDepth(Player player) {
        BlockPos pos = player.blockPosition();

        return player.level().getBlockState(pos.above(2)).getBlock() == Blocks.WATER &&
                player.level().getBlockState(pos.above(3)).getBlock() == Blocks.WATER;
    }

    private boolean isInBubbleColumn(Player player) {
        BlockPos pos = player.blockPosition();
        return player.level().getBlockState(pos).getBlock() == Blocks.BUBBLE_COLUMN;
    }
}