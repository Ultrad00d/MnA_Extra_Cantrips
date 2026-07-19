package net.ultrad00d.ForgottenCantrips.cantrip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

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
}
