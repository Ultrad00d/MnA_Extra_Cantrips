package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpectralBedBlockEntity extends BedBlockEntity {
    private boolean used = false;
    private static final String TAG_USED = "Used";

    public void markUsed() {
        this.used = true;
        this.setChanged();
    }

    public boolean isUsed() {
        return this.used;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean(TAG_USED, this.used);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.used = tag.getBoolean(TAG_USED);
    }

    public SpectralBedBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(pPos, pBlockState, DyeColor.CYAN);
    }
}
