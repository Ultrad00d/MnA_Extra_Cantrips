package net.ultrad00d.ForgottenCantrips.registry;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.timing.DelayedEventQueue;
import com.mna.api.timing.TimedDelayedEvent;

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
    private static final com.mna.cantrips.CantripRegistry REGISTRY = com.mna.cantrips.CantripRegistry.INSTANCE;

    public static void register() {
        for (var forgottenCantrip : CantripType.values()) {
            ICantrip registered = REGISTRY.registerCantrip(
                    fromNamespaceAndPath(ForgottenCantrips.MOD_ID, forgottenCantrip.getId()),
                    fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "textures/gui/cantrips/" + forgottenCantrip.getId() + ".png"),
                    forgottenCantrip.getTier(),
                    (player, cantrip, hand) -> run(player, cantrip, hand, forgottenCantrip.getLogic(), forgottenCantrip.isInstant()),
                    ItemStack.EMPTY,
                    forgottenCantrip.getShapes()
            );
            if (forgottenCantrip.getAdvancementPath() != null) {
                registered.setRequiredAdvancement(
                        fromNamespaceAndPath(ForgottenCantrips.MOD_ID, forgottenCantrip.getAdvancementPath())
                );
            }
        }
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
