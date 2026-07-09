package net.ultrad00d.ForgottenCantrips.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;

import java.util.ArrayList;
import java.util.List;

public class ColossusTreeRootsBlockEntity extends BlockEntity {
    private final List<BlockPos> trackedBlocks = new ArrayList<>();
    private int expectedBlocks = 0;

    public ColossusTreeRootsBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.COLOSSUS_TREE_ROOTS_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    // Calculate tree's integrity
    public double calculateHealth(Level level) {
        if (expectedBlocks == 0) return 0.0;

        long intactBlocks = trackedBlocks.stream()
                .filter(pos -> {
                    BlockState s = level.getBlockState(pos);
                    // TODO insert tree specific leaf & log check
                    return s.is(BlockTags.LOGS) || s.is(BlockTags.LEAVES);
                })
                .count();

        return ((double) intactBlocks / expectedBlocks) * 100.0;
    }

    public List<BlockPos> getTrackedBlocks() {
        return this.trackedBlocks;
    }

    public void setExpectedBlocks(int expectedBlocks) {
        this.expectedBlocks = expectedBlocks;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ExpectedBlocks", expectedBlocks);

        ListTag posList = new ListTag();
        for (BlockPos pos : trackedBlocks) {
            posList.add(LongTag.valueOf(pos.asLong()));
        }
        tag.put("TrackedBlocks", posList);
    }

    // Load data from world files
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.expectedBlocks = tag.getInt("ExpectedBlocks");
        this.trackedBlocks.clear();

        ListTag posList = tag.getList("TrackedBlocks", 4); // 4 is the ID for LongTag
        for (int i = 0; i < posList.size(); i++) {
            this.trackedBlocks.add(BlockPos.of(((LongTag) posList.get(i)).getAsLong()));
        }
    }
}
