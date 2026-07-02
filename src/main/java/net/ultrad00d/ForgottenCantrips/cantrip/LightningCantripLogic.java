package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class LightningCantripLogic extends CantripLogic {
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        double range;
        try {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } catch (Throwable var14) {
            range = 4.5F;
        }
        Vec3 target = player.pick(range, 0.0F, true).getLocation();
        Level level = player.level();
        if (!level.canSeeSky(new BlockPos((int) target.x, (int) target.y, (int) target.z))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.lightning.obstructed"));
            return;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(target);
                lightning.setCause((ServerPlayer) player);
                serverLevel.addFreshEntity(lightning);
            }
        }
    }
}
