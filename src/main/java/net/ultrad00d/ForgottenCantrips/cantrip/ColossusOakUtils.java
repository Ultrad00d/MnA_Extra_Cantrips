package net.ultrad00d.ForgottenCantrips.cantrip;

import  net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static net.ultrad00d.ForgottenCantrips.cantrip.ColossusOakGrower.MAX_GROWTH_HEIGHT;

final class ColossusOakUtils {
    // 'ColossusOakUtils' provides only static methods
    private ColossusOakUtils() {}

    static final int BLOCK_UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS;

    static boolean isSapling(BlockState state) {
        return state.is(BlockTags.SAPLINGS);
    }

    static boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    static boolean isHorizontalLog(BlockState state) {
        return isLog(state) &&
            state.hasProperty(RotatedPillarBlock.AXIS) &&
            state.getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y;
    }

    static boolean isTrunkLog(ServerLevel level, BlockPos pos, String treeType) {
        BlockState state = level.getBlockState(pos);

        return isLog(state) &&
            !isHorizontalLog(state) &&
            getTreeType(state).equals(treeType);
    }

    static boolean sameLog(ServerLevel level, BlockPos pos, Block log) {
        return level.getBlockState(pos).is(log);
    }

    static boolean isValidGround(BlockState state) {
        return state.is(BlockTags.DIRT) ||
            state.is(Blocks.FARMLAND) ||
            state.is(Blocks.MUD);
    }

    static boolean canOccupy(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        return state.isAir() ||
            state.canBeReplaced() ||
            state.is(BlockTags.LEAVES);
    }

    static void placeLogState(ServerLevel level, BlockPos pos, BlockState logState) {
        if (canOccupy(level, pos)) {
            level.setBlock(pos, logState, BLOCK_UPDATE_FLAGS);
        }
    }

    static boolean isMoreNorthWest(BlockPos a, BlockPos b) {
        // 'North-West' means smaller Z (North) then smaller X (West)
        if (a.getZ() != b.getZ()) {
            return a.getZ() < b.getZ();
        }

        return a.getX() < b.getX();
    }

    static String getTreeType(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();

        path = path
            .replace("stripped_", "")
            .replace("_log", "")
            .replace("_wood", "")
            .replace("_stem", "")
            .replace("_hyphae", "");

        return path.isEmpty() ? "oak" : path;
    }

    static boolean hasCanopy(ServerLevel level, List<BlockPos> treeBlocks) {
        int leaves = 0;
        for (BlockPos pos : treeBlocks) {
            if (level.getBlockState(pos).is(BlockTags.LEAVES)) ++leaves;
        }
        return leaves >= 40;
    }

    /** Returns true if tree has at least 40 leaf blocks, at least 3 blocks tall and its top layer has no more than 4 logs, false otherwise */
    static boolean isValidTreeStructure(ServerLevel level, int pivotY, List<BlockPos> treeBlocks, int trunkHeight) {
        // minimum height check (a <3-block tall flat layer is not a tree)
        if (trunkHeight < 3 || trunkHeight > MAX_GROWTH_HEIGHT) return false;

        int topLayerLogCount = 0;

        for (BlockPos pos : treeBlocks) {
            BlockState state = level.getBlockState(pos);

            if (pos.getY() == pivotY && isLog(state)) {
                topLayerLogCount++;
            }
        }

        // A valid tree top layer shouldn't have more than 4 logs on its top layer
        boolean validTopFootprint = topLayerLogCount <= 4;

        return hasCanopy(level, treeBlocks) && validTopFootprint;
    }
}
