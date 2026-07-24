package net.ultrad00d.ForgottenCantrips.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
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
            @NotNull LevelReader level, @NotNull BlockPos structureOrigin, @NotNull BlockPos pieceOrigin,
            @NotNull StructureTemplate.StructureBlockInfo originalBlock, StructureTemplate.StructureBlockInfo currentBlock,
            @NotNull StructurePlaceSettings settings, @Nullable StructureTemplate template) {

        BlockState currentState = currentBlock.state();

        // Replacing marker block with spawner block
        if (currentState.is(Blocks.JACK_O_LANTERN)) {
            if (level instanceof ServerLevelAccessor) {
                BlockPos spawnPos = currentBlock.pos();

                Direction facing = currentState.getValue(HorizontalDirectionalBlock.FACING);
                BlockState spawnerState = BlockRegistry.OLD_WIZARD_SPAWNER.get().defaultBlockState()
                        .setValue(HorizontalDirectionalBlock.FACING, facing);
                return new StructureTemplate.StructureBlockInfo(spawnPos, spawnerState, null);
            }
        }

        // Rotating Mana and Artifice blocks
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(currentState.getBlock());
        if (blockId != null && blockId.getNamespace().equals("mna") && currentState.hasProperty(HorizontalDirectionalBlock.FACING)) {
            Direction originalFacing = originalBlock.state().getValue(HorizontalDirectionalBlock.FACING);
            Rotation structureRotation = settings.getRotation();
            Direction newFacing = structureRotation.rotate(originalFacing);
            BlockState fixedState = currentState.setValue(HorizontalDirectionalBlock.FACING, newFacing);
            return new StructureTemplate.StructureBlockInfo(currentBlock.pos(), fixedState, currentBlock.nbt());
        }

        return currentBlock;
    }

    @Override @NotNull protected StructureProcessorType<?> getType() { return StructureProcessorRegistry.OLD_WIZARD_SPAWNER.get(); }
}
