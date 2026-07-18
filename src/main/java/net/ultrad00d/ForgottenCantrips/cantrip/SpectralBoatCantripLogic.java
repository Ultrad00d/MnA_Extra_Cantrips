package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

public class SpectralBoatCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "spectral_boat"; }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        double range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());

        Level level = player.level();
        Vec3 target = player.pick(range, 0.0F, true).getLocation();

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel)
        {
            SpectralBoat boat = EntityRegistry.SPECTRAL_BOAT.get().create(serverLevel);
            if (boat != null)
            {
                boat.moveTo(target.x, target.y, target.z, player.getYRot(), 0.0F);
                boat.setOwnerUUID(player.getUUID());
                serverLevel.addFreshEntity(boat);
            }
        }
    }
}
