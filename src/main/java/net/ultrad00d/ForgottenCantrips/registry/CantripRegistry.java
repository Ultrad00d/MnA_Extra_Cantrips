package net.ultrad00d.ForgottenCantrips.registry;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;
import com.mna.api.tools.RLoc;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.CantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.EmpowerDamageBuffCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.EmpowerManaCostBuffCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.ForceConsumeCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.LightningCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralBedCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralBoatCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralDonkeyCantripLogic;
import net.ultrad00d.ForgottenCantrips.cantrip.SpectralSlimeCantripLogic;

public class CantripRegistry {
    private static int ICON_SHOW_TIME = 50;

    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new LightningCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBedCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBoatCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Force Consume Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "force_consume"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/force_consume.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new ForceConsumeCantripLogic(), true),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Donkey Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_donkey"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_donkey.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, (CantripLogic) new SpectralDonkeyCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
        // Spectral Slime Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_slime"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_slime.png"),
                1,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralSlimeCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setDelay(ICON_SHOW_TIME);
         // Damage Buff Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "dmg_buff"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/dmg_buff.png"),
                2,
                (player, cantrip, hand) -> run(player, cantrip, hand, new EmpowerDamageBuffCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Mana Cost Buff Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "mana_cost_buff"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/mana_cost_buff.png"),
                2,
                (player, cantrip, hand) -> run(player, cantrip, hand, new EmpowerManaCostBuffCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
    }

    public static void run(Player player, ICantrip cantrip, InteractionHand hand, CantripLogic cantripInstance, boolean instant) {
        if (player.level() instanceof ServerLevel serverLevel)
            DelayedEventQueue.pushEvent(
                serverLevel,
                new TimedDelayedEvent<>(
                    player.getUUID().toString() + "cantrip",
                    instant ? 1 : ICON_SHOW_TIME + 1,
                    null,
                    (id, data) -> cantripInstance.run(player, cantrip, hand)
                )
            );
    }
}
