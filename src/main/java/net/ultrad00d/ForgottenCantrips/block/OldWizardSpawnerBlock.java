package net.ultrad00d.ForgottenCantrips.block;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.ultrad00d.ForgottenCantrips.blockentity.OldWizardSpawnerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class OldWizardSpawnerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public OldWizardSpawnerBlock() {
        super(Properties.copy(Blocks.STONE).randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new OldWizardSpawnerBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pType) {
        if (!(pLevel instanceof ServerLevel serverLevel)) return null;
        return (level, pos, state, blockEntity) -> {
            if (OldWizardSpawnerBlockEntity.serverTick(level, pos, state)) {
                serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
