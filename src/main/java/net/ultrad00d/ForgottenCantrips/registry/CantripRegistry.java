package net.ultrad00d.ForgottenCantrips.registry;

import com.mna.api.cantrips.ICantrip;
import com.mna.api.tools.RLoc;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

        // Lightning Cantrip
        registry.registerCantrip(
                fromNamespaceAndPath("forgotten_cantrips", "lightning"),
                fromNamespaceAndPath("forgotten_cantrips", "textures/gui/cantrips/lightning.png"),
                1,
                CantripRegistry::lightning,
                ItemStack.EMPTY,
                RLoc.create("manaweave_patterns/knot2"), RLoc.create("manaweave_patterns/diamond")
        );
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
        double range;
        try {
            range = player.getAttributeValue(ForgeMod.BLOCK_REACH.get());
        } catch (Throwable var15) {
            range = 4.5F;
        }

        if (!player.level().dimensionType().bedWorks()) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.baddimension"));
            return;
        }

        HitResult rayHit = player.pick(range, 0.0F, false);

        if (rayHit.distanceTo(player) > range) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.toofar"));
            return;
        }

        BlockPos targetBlock = ((BlockHitResult) rayHit).getBlockPos();

        BlockPos aboveTarget = targetBlock.above();
        BlockPos aboveTargetAndInPlayerFacingDir = aboveTarget.relative(player.getDirection(),1);

        if (!(player.level().getBlockState(targetBlock).isSolid())) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtarget"));
            return;
        }

        if (!((player.level().getBlockState(aboveTarget).isAir()) && (player.level().getBlockState(aboveTargetAndInPlayerFacingDir).isAir()))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.nospace"));
            return;
        }


        BedBlock b1 = new SpectralBedBlock();
        BlockState bedState = b1
                .defaultBlockState()
                .setValue(BedBlock.FACING, player.getDirection()) // choose direction
                .setValue(BedBlock.PART, HEAD);

        BlockState footState = bedState.setValue(BedBlock.PART, FOOT);

        //head
        player.level().setBlock(aboveTargetAndInPlayerFacingDir, bedState, 3);

        // foot
        player.level().setBlock(aboveTarget, footState, 3);


        /*
        //block entity is in head
        BlockEntity bedBlockEntity = new SpectralBedBlockEntity(aboveTargetAndInPlayerFacingDir,bedState);

        player.level().setBlockEntity(bedBlockEntity);

        return;
        */
    }
    
    public static void summonBoat(Player player, ICantrip cantrip, InteractionHand hand) {
        player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_boat.desc"));
    }
}
