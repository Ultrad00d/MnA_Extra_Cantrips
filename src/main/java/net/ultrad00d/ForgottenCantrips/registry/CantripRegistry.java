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
    private static int ICON_SHOW_TIME = 50;

    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new LightningCantrip(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBedCantrip(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBoatCantrip(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Force Consume Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "force_consume"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/force_consume.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new ForceConsumeCantrip(), true),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Donkey Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_donkey"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_donkey.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, (Cantrip) new SpectralDonkeyCantrip(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
    }

    public static void run(Player player, ICantrip cantrip, InteractionHand hand, Cantrip cantripInstance, boolean instant) {
        if (!cantripInstance.precond(player, cantrip, hand)) return;
        if (player.level() instanceof ServerLevel serverLevel)
            DelayedEventQueue.pushEvent(
                serverLevel,
                new TimedDelayedEvent<>(
                    cantripInstance.getName(),
                    instant ? 1 : cantrip.getDelay(),
                    null,
                    (id, data) -> cantripInstance.run(player, cantrip, hand)
                )
            );
    }
}
