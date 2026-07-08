package net.ultrad00d.ForgottenCantrips.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RootedDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ColossusTreeRootsBlock extends RootedDirtBlock {
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 1, 3);

    public ColossusTreeRootsBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.ROOTED_DIRT));
        this.registerDefaultState(this.getStateDefinition().any().setValue(TIER, 1));
    }
}
