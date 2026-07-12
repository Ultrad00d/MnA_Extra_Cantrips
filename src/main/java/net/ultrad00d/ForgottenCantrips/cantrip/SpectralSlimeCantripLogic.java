package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.ultrad00d.ForgottenCantrips.entity.SpectralSlime;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

public class SpectralSlimeCantripLogic extends CantripLogic {
    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        double range;
        try {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } catch (Throwable ignored) {
            return;
        }

        Level level = player.level();
        Vec3 target = player.pick(range, 0.0F, true).getLocation();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            SpectralSlime slime = EntityRegistry.SPECTRAL_SLIME.get().create(serverLevel);
            if (slime != null) {
                slime.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(slime.blockPosition()), MobSpawnType.TRIGGERED, null, null);
                slime.setOwnerUUID(player.getUUID());
                slime.setSize(1, true);
                slime.moveTo(target.x, target.y, target.z, player.getYRot(), 0.0F);
                serverLevel.addFreshEntity(slime);
            }
        }
    }
}
