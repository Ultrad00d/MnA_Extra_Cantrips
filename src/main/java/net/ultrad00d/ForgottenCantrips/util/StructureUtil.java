package net.ultrad00d.ForgottenCantrips.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class StructureUtil {
    public static boolean isInsideProtectedStructure(LevelAccessor level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            Structure structure = getStructure(serverLevel, "old_wizard/house"); // TODO: also make "old_wizard/garden" protected
            if (structure != null) {
                // Get the top-level structure start at this position
                StructureStart start = serverLevel.structureManager()
                        .getStructureAt(pos, structure);

                if (start.isValid()) {
                    for (StructurePiece piece : start.getPieces()) {
                        // Check if the coordinate is inside the precise piece bounding box
                        if (piece.getBoundingBox().isInside(pos)) return true;
                    }
                }
            }
        }
        return false;
    }

    public static Structure getStructure(ServerLevel level, String path) {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceKey<Structure> gardenKey = ResourceKey.create(Registries.STRUCTURE,
                ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, path));

        Structure structure = structureRegistry.get(gardenKey);
        if (structure == null) ForgottenCantrips.LOGGER.error("{} structure not found in registry!", path);
        return structure;
    }

    public static Tuple<BlockPos, Rotation> spawnGarden(ServerLevel level, BlockPos homePos) {
        Structure structure = StructureUtil.getStructure(level, "old_wizard/garden");
        if (structure == null) return null;

        int[] distances = { 25 + level.random.nextInt(6), 35 + level.random.nextInt(6) };
        int startDirectionIndex = level.random.nextInt(8);
        BlockPos houseCenter = new BlockPos(-1, 0, 4);

        for (int distance : distances) {
            for (int i = 0; i < 8; i++) {
                int directionIndex = (startDirectionIndex + i) % 8;

                // 1. Calculate Target Coordinates & Rotation
                double angle = directionIndex * (Math.PI / 4.0);
                int distanceX = (int) Math.round(Math.cos(angle) * distance);
                int distanceZ = (int) Math.round(Math.sin(angle) * distance);
                Rotation gardenRotation = StructureUtil.calculateGardenRotation(distanceX, distanceZ);

                int targetX = homePos.getX() + distanceX;
                int targetZ = homePos.getZ() + distanceZ;

                // 2. Validate Terrain (Cliffs & Water)
                BlockPos validSurfacePos = StructureUtil.getValidSurfacePos(level, homePos, targetX, targetZ);
                if (validSurfacePos == null) continue; // Skip to next direction if terrain is bad

                // 3. Offset for house center and generate
                BlockPos finalSpawnPos = validSurfacePos
                        .offset(houseCenter.rotate(gardenRotation))
                        .below(4);
                ForgottenCantrips.LOGGER.info("Final garden position was set to {}", finalSpawnPos);
                Tuple<BlockPos, Rotation> result = StructureUtil.generateGardenStructure(level, structure, finalSpawnPos, gardenRotation);

                if (result != null) return result;
            }
        }
        ForgottenCantrips.LOGGER.error("Failed to find a valid spot to place native garden structure near {}", homePos);
        return null;
    }

    public static Rotation calculateGardenRotation(int distanceX, int distanceZ) {
        if (Math.abs(distanceX) > Math.abs(distanceZ)) {
            return distanceX > 0 ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
        } else {
            return distanceZ > 0 ? Rotation.NONE : Rotation.CLOCKWISE_180;
        }
    }

    public static BlockPos getValidSurfacePos(ServerLevel level, BlockPos homePos, int targetX, int targetZ) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, targetX, targetZ);

        // Reject if height difference is greater than 10 blocks (steep cliff)
        if (Math.abs(surfaceY - homePos.getY()) > 10) return null;

        // Reject if the surface block is a fluid (water/lava)
        BlockPos surfaceBlockPos = new BlockPos(targetX, surfaceY - 1, targetZ);
        if (!level.getFluidState(surfaceBlockPos).isEmpty()) return null;

        return new BlockPos(targetX, surfaceY, targetZ);
    }

    public static Tuple<BlockPos, Rotation> generateGardenStructure(ServerLevel level, Structure structure, BlockPos spawnPos, Rotation rotation) {
        ChunkPos chunkPos = new ChunkPos(spawnPos);
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
        if (!structureStart.isValid()) return null;

        structureStart.placeInChunk(
                level,
                level.structureManager(),
                level.getChunkSource().getGenerator(),
                level.getRandom(),
                new BoundingBox(
                        spawnPos.getX() - 30, level.getMinBuildHeight(), spawnPos.getZ() - 30,
                        spawnPos.getX() + 30, level.getMaxBuildHeight(), spawnPos.getZ() + 30
                ),
                chunkPos
        );

        return new Tuple<>(structureStart.getBoundingBox().getCenter(), rotation);
    }
}
