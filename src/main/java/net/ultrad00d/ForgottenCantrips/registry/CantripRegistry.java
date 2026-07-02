package net.ultrad00d.ForgottenCantrips.registry;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;
import com.mna.api.tools.RLoc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.ultrad00d.ForgottenCantrips.cantrip.Cantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.ForceConsumeCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.LightningCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralBedCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralBoatCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralDonkeyCantrip;
import net.minecraft.world.entity.player.Player;

public class CantripRegistry {
    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new LightningCantrip()),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBedCantrip()),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBoatCantrip()),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Force Consume Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "force_consume"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/force_consume.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new ForceConsumeCantrip()),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(0);
        // Spectral Donkey Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_donkey"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_donkey.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, (Cantrip) new SpectralDonkeyCantrip()),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
    }

    public static void run(Player player, ICantrip cantrip, InteractionHand hand, Cantrip cantripInstance) {
        if (!cantripInstance.precond(player, cantrip, hand)) return;
        if (player.level() instanceof ServerLevel serverLevel)
            DelayedEventQueue.pushEvent(
                serverLevel,
                new TimedDelayedEvent<>(
                    cantripInstance.getName(),
                    cantrip.getDelay() + 1,
                    null,
                    (id, data) -> cantripInstance.run(player, cantrip, hand)
                )
            );
    }
}
