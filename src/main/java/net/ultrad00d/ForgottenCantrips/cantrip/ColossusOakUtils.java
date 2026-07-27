package net.ultrad00d.ForgottenCantrips.cantrip;

import  net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.ultrad00d.ForgottenCantrips.cantrip.ColossusOakGrower.MAX_GROWTH_HEIGHT;

final class ColossusOakUtils {
    // 'ColossusOakUtils' provides only static methods
    private ColossusOakUtils() {}

    static final int BLOCK_UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS;

    private static final Map<Block, String> TREE_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<BlockState, BlockState> WOOD_STATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, BlockState> LEAF_STATE_CACHE = new ConcurrentHashMap<>();

    static boolean isSapling(BlockState state) {
        return state.is(BlockTags.SAPLINGS) ||
               state.is(Blocks.CRIMSON_FUNGUS) ||
               state.is(Blocks.WARPED_FUNGUS);
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

    static boolean isLeaf(BlockPos pos, Map<BlockPos, BlockState> blockMap) {
        BlockState state = blockMap.get(pos);
        return isLeaf(state);
    }

    static boolean isLeaf(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.WART_BLOCKS);
    }

    static boolean sameLog(ServerLevel level, BlockPos pos, Block log) {
        return level.getBlockState(pos).is(log);
    }

    static boolean isValidGround(BlockState state) {
        return state.is(BlockTags.DIRT) ||
               state.is(Blocks.FARMLAND) ||
               state.is(Blocks.MUD) ||
               state.is(Blocks.CRIMSON_NYLIUM) ||
               state.is(Blocks.WARPED_NYLIUM) ||
               state.is(Blocks.NETHERRACK);
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
        return TREE_TYPE_CACHE.computeIfAbsent(state.getBlock(), block -> {
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            path = path
                    .replace("stripped_", "")
                    .replace("_log", "")
                    .replace("_wood", "")
                    .replace("_stem", "")
                    .replace("_hyphae", "");
            return path.isEmpty() ? "oak" : path;
        });
    }

    static boolean hasCanopy(List<BlockPos> treeBlocks, Map<BlockPos, BlockState> blockMap) {
        int leaves = 0;
        int warts = 0;
        for (BlockPos pos : treeBlocks) {
            BlockState state = blockMap.get(pos);
            if (state == null) continue;
            if (state.is(BlockTags.LEAVES)) ++leaves;
            if (state.is(BlockTags.WART_BLOCKS) || state.is(Blocks.SHROOMLIGHT)) ++warts;
            if (leaves >= 40 || warts >= 20) return true;
        }
        return false;
    }

    /** Returns true if tree has at least 40 leaf blocks, at least 3 blocks tall and its top layer has no more than 4 logs, false otherwise */
    static boolean isValidTreeStructure(int pivotY, List<BlockPos> treeBlocks, int trunkHeight, Map<BlockPos, BlockState> blockMap) {
        // minimum height check (a <3-block tall flat layer is not a tree)
        if (trunkHeight < 3 || trunkHeight > MAX_GROWTH_HEIGHT) return false;

        int topLayerLogCount = 0;
        for (BlockPos pos : treeBlocks) {
            if (pos.getY() == pivotY) {
                BlockState state = blockMap.get(pos);
                if (state != null && isLog(state)) {
                    topLayerLogCount++;
                    // A valid tree top layer shouldn't have more than 4 logs on its top layer
                    if (topLayerLogCount > 4) return false;
                }
            }
        }

        return hasCanopy(treeBlocks, blockMap);
    }

    static boolean canBranchThisDirection(BlockPos pos, Direction dir, ServerLevel level, BlockPos.MutableBlockPos mutPos) {
        int targetX = pos.getX() + dir.getStepX();
        int targetZ = pos.getZ() + dir.getStepZ();
        int posY = pos.getY();

        for (int y = -2; y <= 2; y++) {
            mutPos.set(targetX, posY + y, targetZ);
            if (isLog(level.getBlockState(mutPos))) return false;
        }
        return true;
    }

    static BlockState toWoodState(BlockState state) {
        return WOOD_STATE_CACHE.computeIfAbsent(state, s -> {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(s.getBlock());
            String path = id.getPath();

            if (path.endsWith("_stem")) {
                path = path.substring(0, path.length() - 5) + "_hyphae";
            } else if (path.endsWith("_log")) {
                path = path.substring(0, path.length() - 4) + "_wood";
            }

            ResourceLocation newId = ResourceLocation.fromNamespaceAndPath("minecraft", path);
            if (BuiltInRegistries.BLOCK.containsKey(newId)) {
                Block woodBlock = BuiltInRegistries.BLOCK.get(newId);
                BlockState newState = woodBlock.defaultBlockState();
                if (s.hasProperty(RotatedPillarBlock.AXIS) && newState.hasProperty(RotatedPillarBlock.AXIS)) {
                    newState = newState.setValue(RotatedPillarBlock.AXIS, s.getValue(RotatedPillarBlock.AXIS));
                }
                return newState;
            }
            return s;
        });
    }

    static BlockState getLeafState(String treeType) {
        return LEAF_STATE_CACHE.computeIfAbsent(treeType, type -> {
            String leafPath = type + "_leaves";
            if (type.equals("crimson")) leafPath = "nether_wart_block";
            if (type.equals("warped")) leafPath = "warped_wart_block";

            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("minecraft", leafPath);
            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                return BuiltInRegistries.BLOCK.get(id).defaultBlockState();
            }
            return Blocks.OAK_LEAVES.defaultBlockState();
        });
    }
}
