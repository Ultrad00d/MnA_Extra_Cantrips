package net.ultrad00d.ForgottenCantrips.registry;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

import com.mna.api.tools.RLoc;

import net.minecraft.world.item.ItemStack;
import net.ultrad00d.ForgottenCantrips.cantrip.ForceConsumeCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.LightningCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralBedCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralBoatCantrip;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralDonkeyCantrip;

public class CantripRegistry {
    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                LightningCantrip::delay,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                SpectralBedCantrip::delay,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                SpectralBoatCantrip::delay,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Force Consume Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "force_consume"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/force_consume.png"),
                1,
                ForceConsumeCantrip::run,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Donkey Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_donkey"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_donkey.png"),
                1,
                SpectralDonkeyCantrip::delay,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
    }
}
