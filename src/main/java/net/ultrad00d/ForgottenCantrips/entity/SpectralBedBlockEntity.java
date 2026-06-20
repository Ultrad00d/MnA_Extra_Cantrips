package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpectralBedBlockEntity extends BedBlockEntity {
    public SpectralBedBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, DyeColor.CYAN);
    }
}
