package net.ultrad00d.ForgottenCantrips.registry;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.tools.RLoc;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.Objects;

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class CantripRegistry {
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
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                3,
                CantripRegistry::lightning,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/square"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
        ).setRequiredAdvancement(fromNamespaceAndPath("forgotten_cantrips", "uc1/part_3"));
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                CantripRegistry::placeBed,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setRequiredAdvancement(fromNamespaceAndPath("forgotten_cantrips", "uc2/part_3"));
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                CantripRegistry::summonBoat,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        ).setRequiredAdvancement(fromNamespaceAndPath("forgotten_cantrips", "uc3/part_3"));
    }

    public static boolean advancementCheck(Player player, ICantrip cantrip) {
        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null) return false;

            Advancement advancement = server.getAdvancements().getAdvancement(Objects.requireNonNull(cantrip.getRequiredAdvancement()));
            if (advancement == null) return false;

            AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
            return progress.isDone();
        }
        return false;
    }

    public static boolean allowedToCast(Player player, ICantrip cantrip) {
        if (!advancementCheck(player, cantrip)) {
            player.sendSystemMessage(
                    Component.translatable("cantrip.forgotten_cantrips.locked.pre")
                            .append(Component.translatable("cantrip.forgotten_cantrips." + cantrip.getId().getPath()))
                            .append(Component.translatable("cantrip.forgotten_cantrips.locked.post")));
            return false;
        }
        return true;
    }

    public static void lightning(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!allowedToCast(player, cantrip)) return;

        double range;
        try {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } catch (Throwable var14) {
            range = 4.5F;
        }
        Vec3 target = player.pick(range, 0.0F, true).getLocation();
        Level level = player.level();
        if (!level.canSeeSky(new BlockPos((int) target.x, (int) target.y, (int) target.z))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.lightning.obstructed"));
            return;
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(target);
                lightning.setCause((ServerPlayer) player);
                serverLevel.addFreshEntity(lightning);
            }
        }
    }

    public static void placeBed(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!allowedToCast(player, cantrip)) return;

        player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.desc"));
    }
    public static void summonBoat(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!allowedToCast(player, cantrip)) return;

        player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_boat.desc"));
    }
}
