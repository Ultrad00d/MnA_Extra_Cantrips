package net.ultrad00d.ForgottenCantrips.registry;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;
import com.mna.api.tools.RLoc;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;

import net.minecraft.network.chat.Component;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.cantrip.*;
import net.minecraft.world.entity.player.Player;

public class CantripRegistry {
    private static final int ICON_SHOW_TIME = 50;

    public static final int LIGHTNING_TIER = 3;
    public static final int SPECTRAL_BED_TIER = 1;
    public static final int SPECTRAL_BOAT_TIER = 1;
    public static final int FORCE_CONSUME_TIER = 1;
    public static final int SPECTRAL_DONKEY_TIER = 1;

    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Allowed shapes: [tier] "namespace:path"
        // [1] mna:manaweave_patterns/square                       [2] mna:manaweave_patterns/knot
        // [1] mna:manaweave_patterns/triangle                     [1] mna:manaweave_patterns/circle
        // [3] mna:manaweave_patterns/split_triangle               [1] mna:manaweave_patterns/slash
        // [3] mna:manaweave_patterns/bolt                         [2] mna:manaweave_patterns/knot2
        // [4] mna:manaweave_patterns/hourglass                    [2] mna:manaweave_patterns/knot3
        // [5] mna:manaweave_patterns/inverted_split_triangle      [4] mna:manaweave_patterns/star
        // [1] mna:manaweave_patterns/backslash                    [2] mna:manaweave_patterns/diamond
        // [5] mna:manaweave_patterns/infinity                     [2] mna:manaweave_patterns/knot4
        // [3] mna:manaweave_patterns/inverted_triangle

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "lightning"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/lightning.png"),
                LIGHTNING_TIER,
                (player, cantrip, hand) -> run(player, cantrip, hand, new LightningCantripLogic(), false),
                ItemStack.EMPTY,
                        RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
        ).setRequiredAdvancement(fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "uc1/part_3"));

        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "spectral_bed"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/spectral_bed.png"),
                SPECTRAL_BED_TIER,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBedCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setRequiredAdvancement(fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "uc2/part_3"));
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "spectral_boat"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/spectral_boat.png"),
                SPECTRAL_BOAT_TIER,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralBoatCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Force Consume Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "force_consume"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/force_consume.png"),
                FORCE_CONSUME_TIER,
                (player, cantrip, hand) -> run(player, cantrip, hand, new ForceConsumeCantripLogic(), true),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Donkey Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "spectral_donkey"),
                fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/spectral_donkey.png"),
                SPECTRAL_DONKEY_TIER,
                (player, cantrip, hand) -> run(player, cantrip, hand, new SpectralDonkeyCantripLogic(), false),
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setRequiredAdvancement(fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "uc3/part_3"));
    }

    public static boolean advancementCheck(Player player, ICantrip cantrip) {
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null) return false;

            Advancement advancement = server.getAdvancements().getAdvancement(cantrip.getRequiredAdvancement());
            if (advancement == null) return false;

            AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
            return progress.isDone();
        }
        return false;
    }

    public static boolean allowedToCast(Player player, ICantrip cantrip) {
        if (!advancementCheck(player, cantrip)) {
            player.sendSystemMessage(
                    Component.translatable("cantrip."+ ForgottenCantrips.MOD_ID + ".locked.pre")
                            .append(Component.translatable("cantrip." + ForgottenCantrips.MOD_ID + "." + cantrip.getId().getPath()))
                            .append(Component.translatable("cantrip." + ForgottenCantrips.MOD_ID + ".locked.post")));
            return false;
        }
        return true;
    }

    public static void run(Player player, ICantrip cantrip, InteractionHand hand, ICantripLogic cantripInstance, boolean instant) {
        if (!allowedToCast(player, cantrip)) return;
        if (player.level() instanceof ServerLevel serverLevel)
            DelayedEventQueue.pushEvent(
                serverLevel,
                new TimedDelayedEvent<>(
                    player.getUUID() + "cantrip",
                    instant ? 1 : ICON_SHOW_TIME + 1,
                    null,
                    (id, data) -> cantripInstance.run(player, cantrip, hand)
                )
            );
    }
}
