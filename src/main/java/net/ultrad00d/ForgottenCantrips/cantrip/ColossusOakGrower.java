package net.ultrad00d.ForgottenCantrips.cantrip;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

class ColossusOakGrower {
    enum Outcome { GREW, BRANCHED, NOT_GROWABLE, ROOT_DESTROYED, CAST_ON_LEAF}

    static final int MAX_GROWTH_HEIGHT = 32;

    private static final int MIN_BRANCH_HEIGHT = 3;
    private static final int MAX_TRUNK_HEIGHT = 256;
    private static final int SAPLING_GROWTH_ATTEMPTS = 8;
    private static final int BRANCH_LENGTH_MIN = 3;
    private static final int BRANCH_LENGTH_MAX = 6;
    private static final int LEAN_CHANCE_DENOMINATOR = 3;
    private static final int BRANCH_CHANCE_DENOMINATOR = 3;
    private static final int MAX_LOG_SEARCH_NODES = MAX_TRUNK_HEIGHT * 8;
    private static final int MAX_LEAF_SEARCH_NODES = MAX_TRUNK_HEIGHT * 16;

    private static final int THICKEN_TRUNK_NEEDED_HEIGHT = 18;
    private static final int THICKEN_TRUNK_UP_TO_HEIGHT = 12;

    static final Direction[] BRANCH_DIRECTIONS = {
        Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    private enum BranchStyle { SMOOTH, SHARP}
    private record Tree(BlockPos base, String type, int width) {}
    private record BranchOrigin(BlockPos pos, Direction direction) {}

    Outcome grow(ServerLevel level, BlockPos hitPos) {
        BlockState hitState = level.getBlockState(hitPos);

        if (ColossusOakUtils.isSapling(hitState)) {
            return growSapling(level, hitPos, hitState);
        }

        if (ColossusOakUtils.isLeaf(hitState)) {
            return Outcome.CAST_ON_LEAF;
        }

        // If not sapling then must be a log block
        if (!ColossusOakUtils.isLog(hitState)) {
            return Outcome.NOT_GROWABLE;
        }

        BlockPos lowestLog = findTreeBase(hitPos, level);

        // The tree isn't on grass/any type of dirt
        if (!ColossusOakUtils.isValidGround(level.getBlockState(lowestLog.below()))) {
            return Outcome.ROOT_DESTROYED;
        }

        String treeType = ColossusOakUtils.getTreeType(hitState);
        Tree tree = new Tree(
            lowestLog,
                treeType,
            detectTrunkWidth(level, lowestLog, hitState.getBlock())
        );

        List<BlockPos> trunk = trunkPath(level, tree);
        if (trunk.isEmpty()) return Outcome.NOT_GROWABLE;

        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        List<BlockPos> treeBlocks = collectTreeBlocks(level, tree.base(), blockMap);
        int topY = trunk.get(trunk.size() - 1).getY();

        if (!ColossusOakUtils.isValidTreeStructure(topY, treeBlocks, trunk.size(), blockMap)) {
            return Outcome.NOT_GROWABLE;
        }

        if (trunk.size() == THICKEN_TRUNK_NEEDED_HEIGHT) {
            thickenTrunk(level, tree.base(), trunk);
            expandCrown(level, tree, trunk);
        }

        int lowestLeafY = lowestCanopyLeafY(trunk, blockMap);

        List<BranchOrigin> branchOrigins = branchOrigins(level, tree, lowestLeafY);
        boolean grow = branchOrigins.isEmpty() || level.random.nextInt(BRANCH_CHANCE_DENOMINATOR) != 0;

        if (grow) {
            growOnce(level, treeBlocks, blockMap, lowestLeafY);
            return Outcome.GREW;
        }

        if (walkAndBranch(level, tree, branchOrigins)) {
            return Outcome.BRANCHED;
        }

        return Outcome.NOT_GROWABLE;
    }

    private Outcome growSapling(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BonemealableBlock)) {
            return Outcome.NOT_GROWABLE;
        }

        // Sometimes saplings just don't want to grow...
        for (int attempt = 0; attempt < SAPLING_GROWTH_ATTEMPTS; attempt++) {
            BlockState currentState = level.getBlockState(pos);
            if (!(currentState.getBlock() instanceof BonemealableBlock b)) break;
            if (!b.isValidBonemealTarget(level, pos, currentState, false)) continue;

            b.performBonemeal(level, level.random, pos, currentState);
        }

        return Outcome.GREW;
    }

    private boolean walkAndBranch(ServerLevel level, Tree tree, List<BranchOrigin> branchOrigins) {
        BranchOrigin origin = branchOrigins.get(level.random.nextInt(branchOrigins.size()));
        placeBranch(level, origin.pos(), origin.direction(), tree);
        return true;
    }

    private List<BranchOrigin> branchOrigins(ServerLevel level, Tree tree, int lowestLeafY) {
        List<BranchOrigin> spots = new ArrayList<>();
        int maxBranchY = (lowestLeafY == Integer.MAX_VALUE)
            ? Integer.MAX_VALUE
            : lowestLeafY - 2;
        int minY = tree.base().getY() + MIN_BRANCH_HEIGHT;

        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        for (BlockPos pos : trunkPath(level, tree)) {
            int posY = pos.getY();
            if (posY < minY) continue;
            if (posY > maxBranchY) break;

            for (Direction branchDir : BRANCH_DIRECTIONS) {
                if (ColossusOakUtils.canBranchThisDirection(pos, branchDir, level, mPos)) {
                    spots.add(new BranchOrigin(pos, branchDir));
                }
            }
        }

        return spots;
    }

    /** Returns lowest Y value at which the trunk has leaves at the sides, or the first leaf block after the trunk */
    private int lowestCanopyLeafY(List<BlockPos> trunk, Map<BlockPos, BlockState> blockMap) {
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        for (BlockPos at : trunk) {
            if (ColossusOakUtils.isLeaf(at, blockMap)) return at.getY();

            for (Direction dir : BRANCH_DIRECTIONS) {
                mut.setWithOffset(at, dir);
                if (ColossusOakUtils.isLeaf(mut, blockMap)) return at.getY();
            }
        }

        return Integer.MAX_VALUE;
    }

    private void growOnce(ServerLevel level, List<BlockPos> treeBlocks, Map<BlockPos, BlockState> snapshot, int lowestCanopyY) {
        Direction lean = (level.random.nextInt(LEAN_CHANCE_DENOMINATOR) == 0)
                ? BRANCH_DIRECTIONS[level.random.nextInt(BRANCH_DIRECTIONS.length)]
                : null;

        growTree(level, lean, treeBlocks, snapshot, lowestCanopyY);
    }

    private void growTree(ServerLevel level, Direction lean, List<BlockPos> treeBlocks, Map<BlockPos, BlockState> snapshot, int lowestCanopyY) {
        if (treeBlocks.isEmpty()) return;

        treeBlocks.removeIf(pos -> pos.getY() < lowestCanopyY || ColossusOakUtils.isHorizontalLog(snapshot.get(pos)));
        // Sort top-to-bottom
        treeBlocks.sort(Comparator.comparingInt(Vec3i::getY).reversed());

        int dx = (lean == null) ? 0 : lean.getStepX();
        int dz = (lean == null) ? 0 : lean.getStepZ();

        // Clear blocks that will not be at the end of the move
        for (BlockPos pos : treeBlocks) {
            // leave the trunk at `y == lowestCanopyY` alone and clear everything on top
            if (pos.getY() > lowestCanopyY || !ColossusOakUtils.isLog(snapshot.get(pos))) {
                level.setBlock(
                        pos,
                        Blocks.AIR.defaultBlockState(),
                        ColossusOakUtils.BLOCK_UPDATE_FLAGS
                );
            }
        }

        // placing new blocks
        BlockPos.MutableBlockPos destination = new BlockPos.MutableBlockPos();
        for (BlockPos pos : treeBlocks) {
            destination.set(pos.getX() + dx, pos.getY() + 1, pos.getZ() + dz);

            if (!ColossusOakUtils.canOccupy(level, destination)) continue;

            BlockState currentState = snapshot.get(pos);
            if (ColossusOakUtils.isLog(currentState)) {
                currentState = ColossusOakUtils.toWoodState(currentState);
            }
            // Place shifted tree block
            level.setBlock(
                    destination,
                    currentState,
                    ColossusOakUtils.BLOCK_UPDATE_FLAGS
            );
        }
    }

    /** Returns a List of BlockPos of all blocks associated with this tree */
    private List<BlockPos> collectTreeBlocks(ServerLevel level, BlockPos base, Map<BlockPos, BlockState> blockMap) {
        List<BlockPos> result = new ArrayList<>();
        Set<BlockPos> logs = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(base);
        visited.add(base);

        BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();
        while (!queue.isEmpty() && logs.size() < MAX_LOG_SEARCH_NODES) {
            BlockPos current = queue.poll();
            BlockState currentState = level.getBlockState(current);

            if (!ColossusOakUtils.isLog(currentState)) continue;

            BlockPos immutableCurrent = current.immutable();
            logs.add(immutableCurrent);
            result.add(immutableCurrent);
            blockMap.put(immutableCurrent, currentState);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        neighborPos.set(current.getX() + dx, current.getY() + dy, current.getZ() + dz);
                        if (neighborPos.getY() < base.getY()) continue;

                        BlockPos neighbor = neighborPos.immutable();
                        if (visited.add(neighbor)) {
                            BlockState nState = level.getBlockState(neighbor);
                            if (ColossusOakUtils.isLog(nState)) {
                                blockMap.put(neighbor, nState);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }

        Set<BlockPos> claimedLeaves = new HashSet<>();
        Queue<BlockPos> leafQueue = new ArrayDeque<>();

        for (BlockPos log : logs) {
            for (Direction dir : Direction.values()) {
                neighborPos.setWithOffset(log, dir);

                if (neighborPos.getY() < base.getY()) continue;

                BlockPos side = neighborPos.immutable();
                BlockState sideState = level.getBlockState(side);

                if (isDistanceLeaf(sideState, 1) && claimedLeaves.add(side)) {
                    blockMap.put(side, sideState);
                    result.add(side);
                    leafQueue.add(side);
                }
            }
        }

        while (!leafQueue.isEmpty() && result.size() < MAX_LEAF_SEARCH_NODES) {
            BlockPos current = leafQueue.poll();
            int currentDist = blockMap.get(current).getValue(LeavesBlock.DISTANCE);

            for (Direction dir : Direction.values()) {
                neighborPos.setWithOffset(current, dir);

                if (neighborPos.getY() < base.getY()) continue;

                BlockPos neighbor = neighborPos.immutable();
                if (!claimedLeaves.contains(neighbor)) {
                    BlockState nState = level.getBlockState(neighbor);
                    if (isDistanceLeaf(nState, currentDist + 1) && claimedLeaves.add(neighbor)) {
                        blockMap.put(neighbor, nState);
                        result.add(neighbor);
                        leafQueue.add(neighbor);
                    }
                }
            }
        }

        return result;
    }

    private boolean isDistanceLeaf(BlockState state, int expected) {
        if (state.is(BlockTags.WART_BLOCKS)) return expected <= 7;

        return state.is(BlockTags.LEAVES) &&
               state.hasProperty(LeavesBlock.DISTANCE) &&
               state.getValue(LeavesBlock.DISTANCE) == expected;
    }

    private void placeBranch(ServerLevel level, BlockPos origin, Direction dir, Tree tree) {
        BranchStyle style = branchStyleFor(tree.type());
        BlockState originState = level.getBlockState(origin);
        BlockState logState = ColossusOakUtils.toWoodState(originState)
                .setValue(RotatedPillarBlock.AXIS, dir.getAxis());

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

        BlockState leafState = ColossusOakUtils.getLeafState(tree.type());
        placeLeafBlob(level, cursor, leafState);
    }

    private BranchStyle branchStyleFor(String treeType) {
        return switch (treeType) {
            case "cherry", "jungle" -> BranchStyle.SHARP;
            default -> BranchStyle.SMOOTH;
        };
    }

    /** Returns the BlockPos of Northern-West tree root block */
    private BlockPos findTreeBase(BlockPos start, ServerLevel level) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        BlockPos lowest = start;
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            if (current.getY() < lowest.getY() ||
                current.getY() == lowest.getY() && ColossusOakUtils.isMoreNorthWest(current, lowest)) {
                lowest = current;
            }

            mut.setWithOffset(current, Direction.DOWN);
            if (!visited.contains(mut)) {
                BlockState state = level.getBlockState(mut);
                if (ColossusOakUtils.isLog(state)) {
                    BlockPos imm = mut.immutable();
                    visited.add(imm);
                    queue.add(imm);
                }
            }

            for (int y = -1; y <= 0; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        // if same block or block below (below case was checked above)
                        if (x == 0 && z == 0) continue;

                        mut.set(current.getX() + x, current.getY() + y, current.getZ() + z);
                        if (!visited.contains(mut)) {
                            BlockState state = level.getBlockState(mut);
                            if (ColossusOakUtils.isLog(state)) {
                                BlockPos imm = mut.immutable();
                                visited.add(imm);
                                queue.add(imm);
                            }
                        }
                    }
                }
            }
        }

        return lowest;
    }

    /** Returns trunk width - either 1 or 2 */
    private int detectTrunkWidth(ServerLevel level, BlockPos base, Block log) {
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
        boolean twoByTwo =
            ColossusOakUtils.sameLog(level, mut.setWithOffset(base, Direction.EAST), log) &&
            ColossusOakUtils.sameLog(level, mut.setWithOffset(base, Direction.SOUTH), log) &&
            ColossusOakUtils.sameLog(level, mut.set(base.getX() + 1, base.getY(), base.getZ() + 1), log);

        return twoByTwo ? 2 : 1;
    }

    /** Returns List (ordered from below) of BlockPos that represent the trunk of the tree (only logs) */
    private List<BlockPos> trunkPath(ServerLevel level, Tree tree) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = tree.base();
        path.add(current);

        BlockPos.MutableBlockPos mAbove = new BlockPos.MutableBlockPos();
        for (int i = 0; i < MAX_TRUNK_HEIGHT; i++) {
            mAbove.setWithOffset(current, Direction.UP);
            BlockPos next = null;

            if (ColossusOakUtils.isTrunkLog(level, mAbove, tree.type())) {
                next = mAbove.immutable();
            } else {
                for (Direction dir : BRANCH_DIRECTIONS) {
                    BlockPos diagonal = mAbove.relative(dir);
                    if (ColossusOakUtils.isTrunkLog(level, diagonal, tree.type())) {
                        next = diagonal;
                        break;
                    }
                }
            }

            if (next == null) break;

            path.add(next);
            current = next;
        }

        return path;
    }

    private void thickenTrunk(ServerLevel level, BlockPos base, List<BlockPos> trunk) {
        BlockState logState = ColossusOakUtils.toWoodState(level.getBlockState(base));

        if (logState.hasProperty(RotatedPillarBlock.AXIS)) {
            logState = logState.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        }

        int maxThickenY = base.getY() + THICKEN_TRUNK_UP_TO_HEIGHT;
        BlockPos.MutableBlockPos sidePos = new BlockPos.MutableBlockPos();
        for (BlockPos trunkPos : trunk) {
            if (trunkPos.getY() >= maxThickenY) break;
            for (Direction dir : BRANCH_DIRECTIONS) {
                sidePos.setWithOffset(trunkPos, dir);
                ColossusOakUtils.placeLogState(level, sidePos, logState);
            }
        }
    }

    private void expandCrown(ServerLevel level, Tree tree, List<BlockPos> trunk) {
        if (trunk.isEmpty()) return;

        BlockState woodState = ColossusOakUtils.toWoodState(level.getBlockState(tree.base()));
        if (woodState.hasProperty(RotatedPillarBlock.AXIS)) {
            woodState = woodState.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        }
        BlockState leafState = ColossusOakUtils.getLeafState(tree.type());

        BlockPos topTrunk = trunk.get(trunk.size() - 1);

        int[][] directions8 = {
                { 0, -1},
                { 1, -1},
                { 1,  0},
                { 1,  1},
                { 0,  1},
                {-1,  1},
                {-1,  0},
                {-1, -1}
        };

        // 1. Support struts and leaf blobs extending outward on all 8 sides
        for (int[] dir : directions8) {
            int dx = dir[0];
            int dz = dir[1];

            BlockPos supportCursor = topTrunk.below(2);
            for (int step = 1; step <= 3; step++) {
                supportCursor = supportCursor.offset(dx, 1, dz);
                ColossusOakUtils.placeLogState(level, supportCursor, woodState);
                placeLeafBlob(level, supportCursor, leafState);
            }
        }

        // 2. Extra leaf cap over the very top of the trunk
        BlockPos.MutableBlockPos leafPos = new BlockPos.MutableBlockPos();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 0; dy <= 2; dy++) {
                    if (dx * dx + dy * dy + dz * dz <= 5) {
                        leafPos.set(topTrunk.getX() + dx, topTrunk.getY() + dy + 1, topTrunk.getZ() + dz);
                        if (ColossusOakUtils.canOccupy(level, leafPos)) {
                            level.setBlock(leafPos, leafState, ColossusOakUtils.BLOCK_UPDATE_FLAGS);
                        }
                    }
                }
            }
        }
    }

    private void placeLeafBlob(ServerLevel level, BlockPos center, BlockState leafState) {
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

        // 3D "+" shape (center & 8 cardinal directions)
        mut.set(center);
        if (ColossusOakUtils.canOccupy(level, mut)) level.setBlock(mut, leafState, ColossusOakUtils.BLOCK_UPDATE_FLAGS);
        for (Direction dir : Direction.values()) {
            mut.setWithOffset(center, dir);
            if (ColossusOakUtils.canOccupy(level, mut)) {
                level.setBlock(mut, leafState, ColossusOakUtils.BLOCK_UPDATE_FLAGS);
            }
        }

        // Random corners
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) <= 2) continue;

                    if (level.random.nextFloat() < 0.40f) { // 40% chance for corner leaves
                        mut.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                        if (ColossusOakUtils.canOccupy(level, mut)) {
                            level.setBlock(mut, leafState, ColossusOakUtils.BLOCK_UPDATE_FLAGS);
                        }
                    }
                }
            }
        }
    }
}
