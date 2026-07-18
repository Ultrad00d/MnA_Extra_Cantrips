package net.ultrad00d.ForgottenCantrips.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.block.OldWizardSpawnerBlock;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import net.ultrad00d.ForgottenCantrips.registry.BlockEntityRegistry;
import net.ultrad00d.ForgottenCantrips.registry.EntityRegistry;

public class OldWizardSpawnerBlockEntity extends BlockEntity {
    public OldWizardSpawnerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.OLD_WIZARD_SPAWNER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static boolean serverTick(Level level, BlockPos pos, BlockState state, OldWizardSpawnerBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        OldWizard entity = EntityRegistry.OLD_WIZARD.get().create(serverLevel);
        if (entity != null) {
            entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            entity.setHomePos(pos);

            Direction facing = state.getValue(OldWizardSpawnerBlock.FACING);
            Rotation rotation = com.mna.tools.RotationUtils.rotationFromFacing(facing);
            entity.setHouseRotation(rotation);

            if (state.getBlock() instanceof OldWizardSpawnerBlock spawnerBlock) {
                entity.setGarden(spawnGarden(serverLevel, pos));
            }

            serverLevel.addFreshEntity(entity);
        }
        return true;
    }

    public static Tuple<BlockPos, Rotation> spawnGarden(ServerLevel level, BlockPos homePos) {
        // Pick one of 8 directions (0 to 7), representing 45-degree intervals
        int directionIndex = level.random.nextInt(8);
        double angle = directionIndex * (Math.PI / 4.0);

        // Random distance offset from 10 to 15 blocks
        double distance = 10 + level.random.nextInt(6);

        // Convert polar coordinates (distance, angle) to X and Z offsets
        int distanceX = (int) Math.round(Math.cos(angle) * distance);
        int distanceZ = (int) Math.round(Math.sin(angle) * distance);

        // Calculate which direction the garden needs to look to face the house
        Rotation gardenRotation;
        if (Math.abs(distanceX) > Math.abs(distanceZ)) {
            gardenRotation = distanceX > 0 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
        } else {
            gardenRotation = distanceZ > 0 ? Rotation.NONE : Rotation.CLOCKWISE_180;
        }

        BlockPos houseCenter = new BlockPos(-1, 0, 4);
        BlockPos gardenSpawnPos = homePos
                .offset(houseCenter.rotate(gardenRotation)) // centering the house coords
                .offset(distanceX, -1, distanceZ);

        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceKey<Structure> gardenKey = ResourceKey.create(Registries.STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "old_wizard/garden"));
        Structure structure = structureRegistry.get(gardenKey);

        if (structure != null) {
            StructureTemplateManager templateManager = level.getStructureManager();
            ChunkPos chunkPos = new ChunkPos(gardenSpawnPos);

            StructureStart structureStart = structure.generate(
                    level.registryAccess(),
                    level.getChunkSource().getGenerator(),
                    level.getChunkSource().getGenerator().getBiomeSource(),
                    level.getChunkSource().randomState(),
                    level.getStructureManager(),
                    level.getSeed(),
                    chunkPos,
                    0,
                    level,
                    biome -> biome.is(Biomes.DARK_FOREST)
            );

            if (structureStart.isValid()) {
                structureStart.placeInChunk(
                        level,
                        level.structureManager(),
                        level.getChunkSource().getGenerator(),
                        level.getRandom(),
                        new BoundingBox(
                                gardenSpawnPos.getX() - 30, level.getMinBuildHeight(), gardenSpawnPos.getZ() - 30,
                                gardenSpawnPos.getX() + 30, level.getMaxBuildHeight(), gardenSpawnPos.getZ() + 30
                        ),
                        chunkPos
                );

                // Ensure that after worldgen places the garden we still have a valid reference coordinate
                BoundingBox actualBox = structureStart.getBoundingBox();
                BlockPos actualCenterpiece = actualBox.getCenter();
                return new Tuple<>(actualCenterpiece, gardenRotation);
            }
        }

        ForgottenCantrips.LOGGER.error("Failed to place native garden structure at {}", gardenSpawnPos);
        return null;
    }
}
