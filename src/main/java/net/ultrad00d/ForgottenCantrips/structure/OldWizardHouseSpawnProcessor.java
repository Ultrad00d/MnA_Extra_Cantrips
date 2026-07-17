package net.ultrad00d.ForgottenCantrips.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;
import net.ultrad00d.ForgottenCantrips.registry.StructureProcessorRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OldWizardHouseSpawnProcessor extends StructureProcessor {
    public static final Codec<OldWizardHouseSpawnProcessor> CODEC = Codec.unit(() -> OldWizardHouseSpawnProcessor.INSTANCE);
    public static final OldWizardHouseSpawnProcessor INSTANCE = new OldWizardHouseSpawnProcessor();

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader level, BlockPos structureOrigin, BlockPos pieceOrigin,
            StructureTemplate.StructureBlockInfo originalBlock, StructureTemplate.StructureBlockInfo currentBlock,
            StructurePlaceSettings settings, @Nullable StructureTemplate template) {

        BlockState currentState = currentBlock.state();

        if (currentState.is(Blocks.JACK_O_LANTERN)) {
            if (level instanceof ServerLevelAccessor serverLevel) {
                BlockPos spawnPos = currentBlock.pos();

                Direction facing = currentState.getValue(HorizontalDirectionalBlock.FACING);
                BlockState spawnerState = BlockRegistry.OLD_WIZARD_SPAWNER.get().defaultBlockState()
                        .setValue(HorizontalDirectionalBlock.FACING, facing);

                return new StructureTemplate.StructureBlockInfo(currentBlock.pos(), spawnerState, null);
            }
        }
        return currentBlock;
    }

    @Override @NotNull protected StructureProcessorType<?> getType() { return StructureProcessorRegistry.OLD_WIZARD_SPAWNER.get(); }
}
