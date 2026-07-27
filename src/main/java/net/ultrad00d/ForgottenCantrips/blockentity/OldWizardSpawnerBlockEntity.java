package net.ultrad00d.ForgottenCantrips.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ultrad00d.ForgottenCantrips.block.OldWizardSpawnerBlock;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;
import net.ultrad00d.ForgottenCantrips.util.StructureUtil;

public class OldWizardSpawnerBlockEntity extends BlockEntity {
    public OldWizardSpawnerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.OLD_WIZARD_SPAWNER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static boolean serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        OldWizard entity = EntityRegistry.OLD_WIZARD.get().create(level);
        if (entity != null) {
            entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            entity.setHomePos(pos);

            Direction facing = state.getValue(OldWizardSpawnerBlock.FACING);
            Rotation rotation = com.mna.tools.RotationUtils.rotationFromFacing(facing);
            entity.setHouseRotation(rotation);

            entity.setGarden(StructureUtil.spawnGarden(level, pos));

            level.addFreshEntity(entity);
            return true;
        }
        return false;
    }
}
