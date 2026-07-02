package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.ultrad00d.ForgottenCantrips.blockentity.SpectralBedBlockEntity;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;

public class SpectralBedCantripLogic  extends CantripLogic {
    @Override
    public boolean precond(Player player, ICantrip cantrip, InteractionHand hand) {
        long dayTime = player.level().getDayTime() % 24000L;
        boolean isDaytime = dayTime < 13000L;

        if (player.level().dimensionType().bedWorks()) {

            if (isDaytime && !(player.level().isThundering())) {
                player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtime"));
                return false;
            }
        }
        return true;
    }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {     
        HitResult rayHit = player.pick(player.getBlockReach(), 0.0F, false);

        if (rayHit.getType() != BlockHitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.toofar"));
            return;
        }

        long dayTime = player.level().getDayTime() % 24000L;
        boolean isDaytime = dayTime < 13000L;

        if (player.level().dimensionType().bedWorks()) {

            if (isDaytime && !(player.level().isThundering())) {
                player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtime"));
                return;
            }
        }


        BlockPos targetBlock = ((BlockHitResult) rayHit).getBlockPos();

        BlockPos footBlockPos, headBlockPos;
        // if player is looking at the top of the targeted block, simply try placing the bed on top of that block
        if (((BlockHitResult) rayHit).getDirection() == Direction.UP) {
            footBlockPos = targetBlock.above();
        }
        // if the player is looking at the bottom of the targeted block, try placing the bed below the block
        else if (((BlockHitResult) rayHit).getDirection() == Direction.DOWN) {
            footBlockPos = targetBlock.below();
        } else {
            //otherwise, player is looking at the side of the block, so try placing the bed one block in that direction
            footBlockPos = targetBlock.relative(((BlockHitResult) rayHit).getDirection(), 1);
        }

        headBlockPos = footBlockPos.relative(player.getDirection(), 1);

        if (!(player.level().getBlockState(footBlockPos.below()).isSolid())) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.badtarget"));
            return;
        }

        if (!((player.level().getBlockState(footBlockPos).isAir()) && (player.level().getBlockState(headBlockPos).isAir()))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.spectral_bed.nospace"));
            return;
        }



        BedBlock b1 = (BedBlock) BlockRegistry.SPECTRAL_BED.get();
        BlockState bedState = b1
                .defaultBlockState()
                .setValue(BedBlock.FACING, player.getDirection())
                .setValue(BedBlock.PART, BedPart.HEAD);

        BlockState footState = bedState.setValue(BedBlock.PART, BedPart.FOOT);

        player.level().setBlock(headBlockPos, bedState, 3);
        player.level().setBlock(footBlockPos, footState, 3);

        BlockEntity bedBlockEntity = new SpectralBedBlockEntity(headBlockPos, bedState);
        player.level().setBlockEntity(bedBlockEntity);
    }
}
