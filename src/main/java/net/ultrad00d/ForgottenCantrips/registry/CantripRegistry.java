package net.ultrad00d.ForgottenCantrips.registry;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.tools.RLoc;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

import static net.minecraft.resources.ResourceLocation.fromNamespaceAndPath;

public class CantripRegistry {
    public static void register() {
        com.mna.cantrips.CantripRegistry registry = com.mna.cantrips.CantripRegistry.INSTANCE;

        // Allowed shapes:
        // mna:manaweave_patterns/square                       mna:manaweave_patterns/knot
        // mna:manaweave_patterns/triangle                     mna:manaweave_patterns/circle
        // mna:manaweave_patterns/split_triangle               mna:manaweave_patterns/slash
        // mna:manaweave_patterns/bolt                         mna:manaweave_patterns/knot2
        // mna:manaweave_patterns/hourglass                    mna:manaweave_patterns/knot3
        // mna:manaweave_patterns/inverted_split_triangle      mna:manaweave_patterns/star
        // mna:manaweave_patterns/backslash                    mna:manaweave_patterns/diamond
        // mna:manaweave_patterns/infinity                     mna:manaweave_patterns/knot4
        // mna:manaweave_patterns/inverted_triangle

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                CantripRegistry::lightning,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/hourglass"), RLoc.create("manaweave_patterns/circle"), RLoc.create("manaweave_patterns/bolt")
        ).setRequiredAdvancement(fromNamespaceAndPath("forgotten_cantrips", "lightning_spell"));
        // Spectral Bed Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_bed"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_bed.png"),
                1,
                CantripRegistry::placeBed,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
        // Spectral Boat Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "spectral_boat"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/spectral_boat.png"),
                1,
                CantripRegistry::summonBoat,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
    }

    public static void lightning(Player player, ICantrip cantrip, InteractionHand hand) {
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
        player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.desc"));
    }
    public static void summonBoat(Player player, ICantrip cantrip, InteractionHand hand) {
        player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_boat.desc"));
    }
}
