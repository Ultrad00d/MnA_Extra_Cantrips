package net.ultrad00d.ForgottenCantrips.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RootedDirtBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.ultrad00d.ForgottenCantrips.blockentity.ColossusTreeRootsBlockEntity;
import org.jetbrains.annotations.NotNull;

public class ColossusTreeRootsBlock extends RootedDirtBlock implements EntityBlock {
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 1, 3);
    public static final DirectionProperty ROTATION = DirectionProperty.create("rotation", Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST);

    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ColossusTreeRootsBlockEntity(pos, state); }
    public ColossusTreeRootsBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.ROOTED_DIRT));
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(TIER, 1)
                .setValue(ROTATION, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TIER, ROTATION);
    }
}
