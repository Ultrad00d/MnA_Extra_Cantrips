package net.ultrad00d.ForgottenCantrips.block;

import com.mna.tools.RotationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

public class OldWizardSpawnerBlock extends HorizontalDirectionalBlock {
    public OldWizardSpawnerBlock() {
        super(Properties.copy(Blocks.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        level.scheduleTick(pos, this, 2);
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        OldWizard entity = EntityRegistry.OLD_WIZARD.get().create(level);
        if (entity != null) {
            entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            entity.setHomePos(pos);

            Direction facing = state.getValue(FACING);
            Rotation rotation = RotationUtils.rotationFromFacing(facing);
            entity.setStructureRotation(rotation);

            level.addFreshEntity(entity);
        }
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }
}
