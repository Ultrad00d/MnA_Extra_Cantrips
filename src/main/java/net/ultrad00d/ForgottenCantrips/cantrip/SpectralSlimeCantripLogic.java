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
import net.ultrad00d.ForgottenCantrips.registry.EffectRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

public class SpectralSlimeCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "spectral_slime"; }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        double range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());

        Level level = player.level();
        Vec3 target = player.pick(range, 0.0F, true).getLocation();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            SpectralSlime slime = EntityRegistry.SPECTRAL_SLIME.get().create(serverLevel);
            if (slime != null) {
                slime.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(slime.blockPosition()), MobSpawnType.TRIGGERED, null, null);
                slime.setOwnerUUID(player.getUUID());
                slime.setSize(getEmpoweredSlimeSize(player), true);
                slime.moveTo(target.x, target.y, target.z, player.getYRot(), 0.0F);
                serverLevel.addFreshEntity(slime);
            }
        }
    }

    private int getEmpoweredSlimeSize(Player player) {
        if (!player.hasEffect(EffectRegistry.EMPOWER_CANTRIP_BUFF.get())) {
            return 1;
        }

        return Math.min(8, 2 + player.getEffect(EffectRegistry.EMPOWER_CANTRIP_BUFF.get()).getAmplifier());
    }
}
