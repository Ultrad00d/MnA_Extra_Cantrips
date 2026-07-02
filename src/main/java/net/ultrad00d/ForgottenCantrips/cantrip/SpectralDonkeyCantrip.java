package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

import net.minecraft.world.item.Items;

public class SpectralDonkeyCantrip {
    public static void delay(Player player, ICantrip cantrip, InteractionHand hand) {
        if (player.level() instanceof ServerLevel serverLevel)
            DelayedEventQueue.pushEvent(
                serverLevel,
                new TimedDelayedEvent<>(
                    "spectral_bed",
                    cantrip.getDelay() + 1,
                    null,
                    (id, data) -> run(player, cantrip, hand)
                )
            );
    }
    public static void run(Player player, ICantrip cantrip, InteractionHand hand) {
        HitResult hitResult = player.pick(player.getBlockReach(), 0.0F, false);

//        if (hitResult.getType() != BlockHitResult.Type.BLOCK) {
//            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_donkey.toofar"));
//            return;
//        }

        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            Vec3 target = hitResult.getLocation();
            SpectralDonkey spectralDonkey = EntityRegistry.SPECTRAL_DONKEY.get().create(serverLevel);
            if (spectralDonkey != null) {
                DifficultyInstance difficultyInstance = serverLevel.getCurrentDifficultyAt(new BlockPos((int) target.x, (int) target.y, (int) target.z));
                spectralDonkey.finalizeSpawn(serverLevel, difficultyInstance, MobSpawnType.TRIGGERED, null, null);
                spectralDonkey.setOwnerUUID(player.getUUID());
                spectralDonkey.invulnerableTime = 60;
                spectralDonkey.setAge(0);
                spectralDonkey.tameWithName(player);
                spectralDonkey.equipSaddle(null);
                spectralDonkey.moveTo(target.x, target.y, target.z);
                ItemStack offhandStack = player.getItemInHand(InteractionHand.MAIN_HAND.equals(hand) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                if (offhandStack.is(Items.GOLDEN_CARROT)) {
                    spectralDonkey.setPersistent(true);
                    offhandStack.shrink(1);
                }
                serverLevel.addFreshEntity(spectralDonkey);
            }
        }
    }
}
