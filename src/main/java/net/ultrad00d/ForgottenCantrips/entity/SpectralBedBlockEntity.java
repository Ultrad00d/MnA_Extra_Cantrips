package net.ultrad00d.ForgottenCantrips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;

public class SpectralBedBlockEntity extends BlockEntity {
    public SpectralBedBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.SPECTRAL_BED_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
