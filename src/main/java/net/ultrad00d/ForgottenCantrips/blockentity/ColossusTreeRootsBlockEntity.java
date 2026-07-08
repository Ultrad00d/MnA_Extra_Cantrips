package net.ultrad00d.ForgottenCantrips.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;

public class ColossusTreeRootsBlockEntity extends BlockEntity {
    public ColossusTreeRootsBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.COLOSSUS_TREE_ROOTS_BLOCK_ENTITY.get(), pPos, pBlockState);
    }
}
