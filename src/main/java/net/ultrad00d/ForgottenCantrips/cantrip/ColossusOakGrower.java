package net.ultrad00d.ForgottenCantrips.cantrip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class ColossusOakGrower {
    enum Outcome { GREW, BRANCHED, NOT_GROWABLE, ROOT_DESTROYED }

    private static final int MIN_BRANCH_HEIGHT = 3;
    private static final int MAX_GROWTH_HEIGHT = 32;
    private static final int MAX_TRUNK_HEIGHT = 256;

    private static final int SAPLING_GROWTH_ATTEMPTS = 8;
    private static final int BRANCH_LENGTH_MIN = 3;
    private static final int BRANCH_LENGTH_MAX = 6;
    private static final int LEAN_CHANCE_DENOMINATOR = 3;
    private static final int BRANCH_CHANCE_DENOMINATOR = 3;
    private static final int MAX_LOG_SEARCH_NODES = MAX_TRUNK_HEIGHT * 8;
    private static final int MAX_LEAF_SEARCH_NODES = MAX_TRUNK_HEIGHT * 16;

    // Random direction branching felt really bad lol
    private static final Direction[] BRANCH_DIRECTIONS = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    private final Long2IntOpenHashMap branchDirectionIndexByTree = new Long2IntOpenHashMap();
    // TODO: 'INVERSE_SMOOTH' and 'THICK' are unused for now (im tired)
    private enum BranchStyle { SMOOTH, SHARP, INVERSE_SMOOTH, THICK }
    private record Tree(BlockPos base, String type, int width) {}

    Outcome grow(ServerLevel level, BlockPos hitPos) {
        BlockState hitState = level.getBlockState(hitPos);

        if (ColossusOakUtils.isSapling(hitState)) {
            return growSapling(level, hitPos, hitState);
        }

        // If not sapling then must be a log block
        if (!ColossusOakUtils.isLog(hitState)) {
            return Outcome.NOT_GROWABLE;
        }

        BlockPos lowestLog = findTreeBase(hitPos, level);

        if (!ColossusOakUtils.isValidGround(level.getBlockState(lowestLog.below()))) {
            // The tree isn't on grass/any type of dirt
            return Outcome.ROOT_DESTROYED;
        }

        Tree tree = new Tree(
            lowestLog,
            ColossusOakUtils.getTreeType(hitState),
            detectTrunkWidth(level, lowestLog, hitState.getBlock())
        );


        boolean canGrow = trunkLogCount(level, tree) < MAX_GROWTH_HEIGHT;
        boolean grow = canGrow && (
            !canBranch(level, tree) ||
            level.random.nextInt(BRANCH_CHANCE_DENOMINATOR) != 0
        );

        if (grow) {
            growOnce(level, tree);

            return Outcome.GREW;
        }

        if (walkAndBranch(level, tree)) {
            return Outcome.BRANCHED;
        }

        if (canGrow) {
            growOnce(level, tree);

            return Outcome.GREW;
        }

        return Outcome.NOT_GROWABLE;
    }

    private Outcome growSapling(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BonemealableBlock)) {
            return Outcome.NOT_GROWABLE;
        }

        // Sometimes saplings just don't want to grow...
        for (int attempt = 0; attempt < SAPLING_GROWTH_ATTEMPTS; attempt++) {
            if (!(level.getBlockState(pos).getBlock() instanceof BonemealableBlock b)) {
                break;
            }

            if (!b.isValidBonemealTarget(level, pos, level.getBlockState(pos), false)) {
                continue;
            }

            b.performBonemeal(level, level.random, pos, level.getBlockState(pos));
        }

        return Outcome.GREW;
    }

    private boolean walkAndBranch(ServerLevel level, Tree tree) {
        List<BlockPos> spots = branchOrigins(level, tree);

        if (spots.isEmpty()) {
            return false;
        }

        BlockPos origin = spots.get(level.random.nextInt(spots.size()));
        Direction dir = nextBranchDirection(tree.base());

        placeBranch(level, origin, dir, tree);

        return true;
    }

    private boolean canBranch(ServerLevel level, Tree tree) {
        return !branchOrigins(level, tree).isEmpty();
    }

    private List<BlockPos> branchOrigins(ServerLevel level, Tree tree) {
        List<BlockPos> spots = new ArrayList<>();
        int lowestLeafY = lowestCanopyLeafY(level, tree);
        int maxBranchY = (lowestLeafY == Integer.MAX_VALUE)
            ? Integer.MAX_VALUE
            : lowestLeafY - 2;
        int minY = tree.base().getY() + MIN_BRANCH_HEIGHT;

        for (BlockPos pos : trunkPath(level, tree)) {
            if (pos.getY() < minY) continue;
            if (pos.getY() > maxBranchY) break;

            spots.add(pos);
        }

        return spots;
    }

    private Direction nextBranchDirection(BlockPos base) {
        long key = base.asLong();
        int index = branchDirectionIndexByTree.getOrDefault(key, 0);
        Direction dir = BRANCH_DIRECTIONS[index];

        branchDirectionIndexByTree.put(key, (index + 1) % BRANCH_DIRECTIONS.length);

        return dir;
    }

    private int lowestCanopyLeafY(ServerLevel level, Tree tree) {
        for (BlockPos at : trunkPath(level, tree)) {
            if (
                isLeaf(level, at) ||
                isLeaf(level, at.north()) ||
                isLeaf(level, at.south()) ||
                isLeaf(level, at.east()) ||
                isLeaf(level, at.west())
            ) {
                return at.getY();
            }
        }

        return Integer.MAX_VALUE;
    }

    private boolean isLeaf(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.LEAVES);
    }

    private void growOnce(ServerLevel level, Tree tree) {
        Direction lean = (level.random.nextInt(LEAN_CHANCE_DENOMINATOR) == 0)
            ? BRANCH_DIRECTIONS[level.random.nextInt(BRANCH_DIRECTIONS.length)]
            : null;

        growTree(level, tree, lean);
    }

    private void growTree(ServerLevel level, Tree tree, Direction lean) {
        List<BlockPos> treeBlocks = collectTreeBlocks(level, tree.base());

        if (treeBlocks.isEmpty()) {
            return;
        }

        treeBlocks.removeIf(pos -> ColossusOakUtils.isHorizontalLog(level.getBlockState(pos)));

        int dx = (lean == null) ? 0 : lean.getStepX();
        int dz = (lean == null) ? 0 : lean.getStepZ();

        // Growth always happens at the trunk's tip
        List<BlockPos> path = trunkPath(level, tree);
        int pivotY = path.get(path.size() - 1).getY();

        Map<BlockPos, Block> tipLogTypes = new HashMap<>();

        for (BlockPos pos : treeBlocks) {
            if (pos.getY() == pivotY && ColossusOakUtils.isLog(level.getBlockState(pos))) {
                tipLogTypes.put(pos.immutable(), level.getBlockState(pos).getBlock());
            }
        }

        // The whole top part shifts up and over by the lean
        List<BlockPos> moving = new ArrayList<>();

        for (BlockPos pos : treeBlocks) {
            BlockState s = level.getBlockState(pos);
            boolean tipOrAboveLog = ColossusOakUtils.isLog(s) && pos.getY() >= pivotY;
            boolean canopyLeaf = s.is(BlockTags.LEAVES);

            if (tipOrAboveLog || canopyLeaf) {
                moving.add(pos);
            }
        }

        Map<BlockPos, BlockState> sources = new HashMap<>();

        for (BlockPos from : moving) {
            sources.put(from.immutable(), level.getBlockState(from));
        }

        moving.sort((a, b) -> Integer.compare(b.getY(), a.getY()));

        Set<BlockPos> destinations = new HashSet<>();

        for (BlockPos from : moving) {
            BlockPos to = from.offset(dx, 1, dz);

            level.setBlock(
                to,
                sources.get(from.immutable()),
                ColossusOakUtils.BLOCK_UPDATE_FLAGS
            );
            destinations.add(to.immutable());
        }

        for (BlockPos from : moving) {
            if (!destinations.contains(from.immutable())) {
                level.setBlock(
                    from,
                    Blocks.AIR.defaultBlockState(),
                    ColossusOakUtils.BLOCK_UPDATE_FLAGS
                );
            }
        }

        for (Map.Entry<BlockPos, Block> tip : tipLogTypes.entrySet()) {
            if (destinations.contains(tip.getKey())) {
                continue;
            }

            level.setBlock(
                tip.getKey(),
                tip.getValue().defaultBlockState(),
                ColossusOakUtils.BLOCK_UPDATE_FLAGS
            );
        }
    }

    private List<BlockPos> collectTreeBlocks(ServerLevel level, BlockPos base) {
        List<BlockPos> result = new ArrayList<>();

        Set<BlockPos> logs = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(base);
        visited.add(base);

        while (!queue.isEmpty() && logs.size() < MAX_LOG_SEARCH_NODES) {
            BlockPos current = queue.poll();

            if (!ColossusOakUtils.isLog(level.getBlockState(current))) {
                continue;
            }

            logs.add(current.immutable());
            result.add(current.immutable());

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }

                        BlockPos neighbor = current.offset(dx, dy, dz);

                        if (neighbor.getY() < base.getY()) {
                            continue;
                        }

                        if (visited.add(neighbor) && ColossusOakUtils.isLog(level.getBlockState(neighbor))) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        Set<BlockPos> claimedLeaves = new HashSet<>();
        Queue<BlockPos> leafQueue = new ArrayDeque<>();

        for (BlockPos log : logs) {
            for (Direction dir : Direction.values()) {
                BlockPos side = log.relative(dir);

                if (side.getY() < base.getY()) {
                    continue;
                }

                if (isDistanceLeaf(level, side, 1) && claimedLeaves.add(side.immutable())) {
                    result.add(side.immutable());
                    leafQueue.add(side.immutable());
                }
            }
        }

        while (!leafQueue.isEmpty() && result.size() < MAX_LEAF_SEARCH_NODES) {
            BlockPos current = leafQueue.poll();
            int currentDist = level.getBlockState(current).getValue(LeavesBlock.DISTANCE);

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);

                if (neighbor.getY() < base.getY()) {
                    continue;
                }

                if (isDistanceLeaf(level, neighbor, currentDist + 1) && claimedLeaves.add(neighbor.immutable())) {
                    result.add(neighbor.immutable());
                    leafQueue.add(neighbor.immutable());
                }
            }
        }

        return result;
    }

    private boolean isDistanceLeaf(ServerLevel level, BlockPos pos, int expected) {
        BlockState state = level.getBlockState(pos);

        return state.is(BlockTags.LEAVES) &&
            state.hasProperty(LeavesBlock.DISTANCE) &&
            state.getValue(LeavesBlock.DISTANCE) == expected;
    }

    private void placeBranch(ServerLevel level, BlockPos origin, Direction dir, Tree tree) {
        BranchStyle style = branchStyleFor(tree.type());
        Block log = level.getBlockState(origin).getBlock();
        BlockState logState = log.defaultBlockState().setValue(RotatedPillarBlock.AXIS, dir.getAxis());

        int length = BRANCH_LENGTH_MIN + level.random.nextInt(BRANCH_LENGTH_MAX - BRANCH_LENGTH_MIN + 1);
        int outPerUp = (style == BranchStyle.SHARP) ? 1 : 2;

        BlockPos cursor = origin;
        int outSinceUp = 0;

        for (int step = 1; step <= length; step++) {
            cursor = cursor.relative(dir);
            ColossusOakUtils.placeLogState(level, cursor, logState);

            if (tree.width() == 2 && step == 1) {
                ColossusOakUtils.placeLogState(level, cursor.relative(dir.getClockWise()), logState);
            }

            outSinceUp++;

            if (outSinceUp >= outPerUp && step < length) {
                cursor = cursor.above();
                outSinceUp = 0;
            }
        }
    }

    private BranchStyle branchStyleFor(String treeType) {
        return switch (treeType) {
            case "cherry" -> BranchStyle.SHARP;
            case "jungle" -> BranchStyle.SHARP;
            default -> BranchStyle.SMOOTH;
        };
    }

    private BlockPos findTreeBase(BlockPos start, ServerLevel level) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        BlockPos lowest = start;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            if (
                current.getY() < lowest.getY() ||
                (
                    current.getY() == lowest.getY() &&
                    ColossusOakUtils.isMoreNorthWest(current, lowest)
                )
            ) {
                lowest = current;
            }

            BlockPos below = current.below();

            if (visited.add(below) && ColossusOakUtils.isLog(level.getBlockState(below))) {
                queue.add(below);
            }

            for (int yOff = -1; yOff <= 0; yOff++) {
                for (int xOff = -1; xOff <= 1; xOff++) {
                    for (int zOff = -1; zOff <= 1; zOff++) {
                        if (xOff == 0 && zOff == 0) {
                            continue;
                        }

                        BlockPos neighbor = current.offset(xOff, yOff, zOff);

                        if (visited.add(neighbor) && ColossusOakUtils.isLog(level.getBlockState(neighbor))) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return lowest;
    }

    private int detectTrunkWidth(ServerLevel level, BlockPos base, Block log) {
        boolean twoByTwo = ColossusOakUtils.sameLog(level, base.east(), log) &&
            ColossusOakUtils.sameLog(level, base.south(), log) &&
            ColossusOakUtils.sameLog(level, base.east().south(), log);

        return twoByTwo ? 2 : 1;
    }

    private List<BlockPos> trunkPath(ServerLevel level, Tree tree) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = tree.base();

        path.add(current);

        for (int i = 0; i < MAX_TRUNK_HEIGHT; i++) {
            BlockPos above = current.above();
            BlockPos next = null;

            if (ColossusOakUtils.isTrunkLog(level, above, tree.type())) {
                next = above;
            } else {
                for (Direction dir : BRANCH_DIRECTIONS) {
                    BlockPos diag = above.relative(dir);

                    if (ColossusOakUtils.isTrunkLog(level, diag, tree.type())) {
                        next = diag;

                        break;
                    }
                }
            }

            if (next == null) {
                break;
            }

            path.add(next);
            current = next;
        }

        return path;
    }

    private int trunkLogCount(ServerLevel level, Tree tree) {
        return trunkPath(level, tree).size();
    }
}
