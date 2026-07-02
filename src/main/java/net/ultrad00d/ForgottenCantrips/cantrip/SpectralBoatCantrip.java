package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

public class SpectralBoatCantrip {
    public static void delay(Player player, ICantrip cantrip, InteractionHand hand) {
        if (player.level() instanceof ServerLevel serverLevel)
            DelayedEventQueue.pushEvent(
                serverLevel,
                new TimedDelayedEvent<>(
                    "spectral_boat",
                    cantrip.getDelay() + 1,
                    null,
                    (id, data) -> run(player, cantrip, hand)
                )
            );
    }
    public static void run(Player player, ICantrip cantrip, InteractionHand hand) {
        double range;
        try
        {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } 
        catch (Throwable var14)
        {
            return;
        }

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
