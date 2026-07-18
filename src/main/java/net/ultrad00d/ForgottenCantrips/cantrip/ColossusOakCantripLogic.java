package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Colossus Oak cantrip.
 * <p>
 * Procedural growth (see backlog US-010 and its technical PBI):
 * <ul>
 *     <li>Cast on a vanilla sapling -> grow it into a normal (T1) vanilla tree.</li>
 *     <li>Cast on a log that forms a tree -> find the root, walk up the trunk, then either branch
 *     out (probabilistically) or grow the tree one block taller.</li>
 *     <li>Cast on an isolated log / broken tree -> feedback.</li>
 * </ul>
 * General case: any block in {@code #minecraft:logs} (and any {@code #minecraft:saplings}) is
 * accepted, so vanilla and modded tree families all work.
 */
public class ColossusOakCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "colossus_oak"; }

    /** Lowest point off the ground a branch may originate from. */
    private static final int MIN_BRANCH_HEIGHT = 3;
    /** Maximum trunk height (logs above the base) the tree may grow to. */
    private static final int MAX_GROWTH_HEIGHT = 32;
    /** Safety cap so a malformed/huge trunk can never spin forever. */
    private static final int MAX_TRUNK_HEIGHT = 256;

    /** Horizontal directions a branch can point, cycled through on each cast. */
    private static final Direction[] BRANCH_DIRECTIONS = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };
    /**
     * Rotates the branch direction per tree, keyed by the tree's base position, so successive
     * branches on the same tree fan out instead of stacking. Different trees rotate independently.
     */
    private final Map<BlockPos, Integer> branchDirectionIndexByTree = new HashMap<>();

    /** Branch drawing styles, keyed by tree family. Only SMOOTH/SHARP are used in v1. */
    private enum BranchStyle { SMOOTH, SHARP, INVERSE_SMOOTH, THICK }

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        // Ray-pick whatever block the player is looking at.
        HitResult rayHit = player.pick(player.getBlockReach(), 0.0F, false);
        if (rayHit.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable(getLangKey("toofar")));
            return;
        }

        BlockPos hitPos = ((BlockHitResult) rayHit).getBlockPos();
        BlockState hitState = serverLevel.getBlockState(hitPos);

        // Sapling path: grow it straight into a vanilla tree and stop.
        if (isSupportedSapling(hitState)) {
            growSapling(player, serverLevel, hitPos, hitState);
            return;
        }

        // Otherwise it must be a supported log/wood that is part of a tree.
        if (!isSupportedLog(hitState)) {
            player.sendSystemMessage(Component.translatable(getLangKey("not_growable")));
            return;
        }

        // Walk down the trunk to the lowest log, then check the ground beneath it.
        BlockPos lowestLog = findTreeBase(hitPos, serverLevel);
        BlockPos groundPos = lowestLog.below();
        if (!isValidGround(serverLevel.getBlockState(groundPos))) {
            // The base isn't sitting on soil -> the tree has been dug out / broken.
            player.sendSystemMessage(Component.translatable(getLangKey("root_destroyed")));
            return;
        }

        String treeType = getTreeType(hitState);
        int trunkWidth = detectTrunkWidth(serverLevel, lowestLog, hitState.getBlock());

        // Each cast either grows the trunk taller or sprouts a branch, chosen at random. Growth is
        // capped; once the trunk is maxed out, only branches remain. If the chosen action can't
        // happen (e.g. no room for a branch), we fall back to the other.
        boolean canGrow = trunkLogCount(serverLevel, lowestLog, treeType, trunkWidth) < MAX_GROWTH_HEIGHT;
        boolean grow = canGrow && (!canBranch(serverLevel, lowestLog, treeType, trunkWidth)
                || serverLevel.random.nextBoolean());

        boolean branched = false;
        if (grow) {
            growOnce(serverLevel, lowestLog, treeType, trunkWidth);
        } else {
            branched = walkAndBranch(serverLevel, lowestLog, treeType, trunkWidth);
            // Nothing branched (no valid spot) and we can still grow -> grow instead of wasting the cast.
            if (!branched && canGrow) {
                growOnce(serverLevel, lowestLog, treeType, trunkWidth);
                grow = true;
            }
        }

        if (branched) {
            player.sendSystemMessage(Component.translatable(getLangKey("branched")));
        } else if (grow) {
            player.sendSystemMessage(Component.translatable(getLangKey("grew")));
        } else {
            // Trunk is maxed and there was no room for a branch either.
            player.sendSystemMessage(Component.translatable(getLangKey("not_growable")));
        }
    }

    // ------------------------------------------------------------------
    // Sapling handling
    // ------------------------------------------------------------------

    private void growSapling(Player player, ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BonemealableBlock)) {
            player.sendSystemMessage(Component.translatable(getLangKey("not_growable")));
            return;
        }
        // Reuse vanilla growth so we get a correct T1 tree. Try a few times in case
        // a single growth step only advances the sapling stage.
        for (int attempt = 0; attempt < 8; attempt++) {
            if (!(level.getBlockState(pos).getBlock() instanceof BonemealableBlock b)) break;
            if (b.isValidBonemealTarget(level, pos, level.getBlockState(pos), false)) {
                b.performBonemeal(level, level.random, pos, level.getBlockState(pos));
            }
        }
        player.sendSystemMessage(Component.translatable(getLangKey("grew")));
    }

    // ------------------------------------------------------------------
    // Trunk walking + branching
    // ------------------------------------------------------------------

    /**
     * Sprouts a single branch at a random eligible trunk height, pointing in the next rotated
     * direction. Does nothing if there's no legal spot (trunk too short or canopy too low).
     *
     * @return true if a branch was placed.
     */
    private boolean walkAndBranch(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        List<BlockPos> spots = branchOrigins(level, base, treeType, trunkWidth);
        if (spots.isEmpty()) {
            return false;
        }

        // Pick a random eligible height so successive branches don't all start at the same spot,
        // and rotate the outward direction every cast so they don't stack on one side.
        BlockPos origin = spots.get(level.random.nextInt(spots.size()));
        Direction dir = nextBranchDirection(base);
        placeBranch(level, origin, dir, treeType, trunkWidth);
        return true;
    }

    /** True if there's at least one trunk position a branch could legally start from. */
    private boolean canBranch(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        return !branchOrigins(level, base, treeType, trunkWidth).isEmpty();
    }

    /**
     * Collects every trunk log a branch may originate from: at least {@link #MIN_BRANCH_HEIGHT}
     * above the base, and low enough to leave one empty block below the canopy (leaf at Y, gap at
     * Y-1, branch origin at Y-2 or lower).
     */
    private List<BlockPos> branchOrigins(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        List<BlockPos> spots = new ArrayList<>();
        int lowestLeafY = lowestCanopyLeafY(level, base, treeType, trunkWidth);
        int maxBranchY = (lowestLeafY == Integer.MAX_VALUE) ? Integer.MAX_VALUE : lowestLeafY - 2;
        int minY = base.getY() + MIN_BRANCH_HEIGHT;

        // Follow the trunk (leans included) and keep the stretch clear of ground and canopy.
        for (BlockPos pos : trunkPath(level, base, treeType, trunkWidth)) {
            if (pos.getY() < minY) continue;
            if (pos.getY() > maxBranchY) break; // reached the canopy zone; stop before branching into leaves
            spots.add(pos);
        }
        return spots;
    }

    /** Returns the next branch direction for the tree at {@code base}, advancing its own rotation. */
    private Direction nextBranchDirection(BlockPos base) {
        BlockPos key = base.immutable();
        int index = branchDirectionIndexByTree.getOrDefault(key, 0);
        Direction dir = BRANCH_DIRECTIONS[index];
        branchDirectionIndexByTree.put(key, (index + 1) % BRANCH_DIRECTIONS.length);
        return dir;
    }

    /**
     * Finds the Y of the lowest leaf block in the tree's canopy by walking the trunk (leans included)
     * and checking each trunk log plus its four horizontal neighbours for leaves. Following the trunk
     * rather than scanning straight up from the base means a leaned tree still reports its real canopy
     * height. Returns {@link Integer#MAX_VALUE} if no leaves exist.
     */
    private int lowestCanopyLeafY(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        for (BlockPos at : trunkPath(level, base, treeType, trunkWidth)) {
            // Check the trunk cell and its four horizontal neighbours for leaves.
            if (isLeaf(level, at)
                    || isLeaf(level, at.north()) || isLeaf(level, at.south())
                    || isLeaf(level, at.east()) || isLeaf(level, at.west())) {
                return at.getY();
            }
        }
        return Integer.MAX_VALUE;
    }

    private boolean isLeaf(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockTags.LEAVES);
    }

    /**
     * Grows the tree taller by one block, either straight up or with a sideways lean, chosen at
     * random. Both cases are pure block displacement so this works for any tree family.
     */
    private void growOnce(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        // Roughly one cast in three leans; the rest grow straight up.
        Direction lean = (level.random.nextInt(3) == 0)
                ? BRANCH_DIRECTIONS[level.random.nextInt(BRANCH_DIRECTIONS.length)]
                : null;
        growTree(level, base, treeType, trunkWidth, lean);
    }

    /**
     * Adds one block of height to the tree. It grows either straight up or with a sideways lean:
     * <ul>
     *     <li>Straight — the pivot sits at the base, so the whole trunk simply rises by one.</li>
     *     <li>Lean — the pivot sits high up, at the trunk log one block below the canopy. The lower
     *     trunk stays vertical and only the crown from the pivot up is nudged sideways, so the tree
     *     kinks near the top instead of toppling from the ground.</li>
     * </ul>
     * The pivot row is read from the trunk's actual position (not the base), so a tree that already
     * leans keeps bending cleanly. Horizontal branch logs are left where they are.
     *
     * @param lean the horizontal lean direction, or {@code null} to grow straight up.
     */
    private void growTree(ServerLevel level, BlockPos base, String treeType, int trunkWidth, Direction lean) {
        List<BlockPos> treeBlocks = collectTreeBlocks(level, base);
        if (treeBlocks.isEmpty()) return;

        // Branches stay at their original height as the tree grows, so exclude them from the shift.
        treeBlocks.removeIf(pos -> isHorizontalLog(level.getBlockState(pos)));

        int dx = (lean == null) ? 0 : lean.getStepX();
        int dz = (lean == null) ? 0 : lean.getStepZ();

        // Growth always happens at the trunk's tip. The pivot is the topmost trunk log, found by
        // *following* the trunk (trunkPath tracks any kinks already frozen into it), so only the tip
        // and the canopy above it move. Everything below the tip stays exactly where it is, which means
        // each sideways lean leaves a permanent kink and repeated leans stack into a zigzag rather than
        // the trunk snapping back to a straight vertical tower.
        List<BlockPos> path = trunkPath(level, base, treeType, trunkWidth);
        int pivotY = Math.max(path.get(path.size() - 1).getY(), base.getY() + 1);

        // The tip logs (trunk logs on the pivot row) and their log type, captured before we move
        // anything. Each grows by extending one block in the lean direction, so we remember where they
        // were to refill that cell afterwards and keep the trunk continuous.
        Map<BlockPos, Block> tipLogTypes = new HashMap<>();
        for (BlockPos pos : treeBlocks) {
            if (pos.getY() == pivotY && isLog(level.getBlockState(pos))) {
                tipLogTypes.put(pos.immutable(), level.getBlockState(pos).getBlock());
            }
        }

        // The whole crown shifts up and over by the lean: the tip log (and any trunk above it) plus the
        // entire leaf canopy, including the leaves that droop a block or two below the tip. Moving all
        // the leaves — not just the ones at/above the tip — keeps the canopy intact and, crucially,
        // lets it actually rise with the trunk so bare trunk is exposed below for branches to sprout on.
        // The trunk logs below the tip are frozen forever, so each lean leaves a permanent kink.
        List<BlockPos> moving = new ArrayList<>();
        for (BlockPos pos : treeBlocks) {
            BlockState s = level.getBlockState(pos);
            boolean tipOrAboveLog = isLog(s) && pos.getY() >= pivotY;
            boolean canopyLeaf = s.is(BlockTags.LEAVES);
            if (tipOrAboveLog || canopyLeaf) moving.add(pos);
        }

        // Snapshot sources first, then move top-down so we never overwrite an unread cell.
        Map<BlockPos, BlockState> sources = new HashMap<>();
        for (BlockPos from : moving) sources.put(from.immutable(), level.getBlockState(from));
        moving.sort((a, b) -> Integer.compare(b.getY(), a.getY()));

        Set<BlockPos> destinations = new HashSet<>();
        for (BlockPos from : moving) {
            BlockPos to = from.offset(dx, 1, dz);
            level.setBlock(to, sources.get(from.immutable()), 3);
            destinations.add(to.immutable());
        }

        // Any original crown cell nothing moved into becomes air.
        for (BlockPos from : moving) {
            if (!destinations.contains(from.immutable())) {
                level.setBlock(from, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        // Refill each vacated tip cell with a log. The old tip has shifted to tip+(dx,1,dz); dropping a
        // log back into the tip's former spot bridges the frozen trunk below to the new leaning tip, so
        // the trunk reads as a continuous diagonal that grows one step further out with every lean.
        for (Map.Entry<BlockPos, Block> tip : tipLogTypes.entrySet()) {
            if (destinations.contains(tip.getKey())) continue; // something already occupies it
            level.setBlock(tip.getKey(), tip.getValue().defaultBlockState(), 3);
        }
    }

    /**
     * Collects only the blocks that belong to <em>this</em> tree, so growth never drags along a
     * neighbouring tree that merely stands close by. Done in two phases:
     * <ol>
     *     <li><b>Logs</b> — flood-fill log→log through the 3x3x3 neighbourhood (to follow diagonal
     *     branch staircases). Disconnected trees share no logs, so this stays inside one trunk.</li>
     *     <li><b>Leaves</b> — BFS outward from those logs using vanilla's {@link LeavesBlock#DISTANCE}
     *     metric: a leaf is claimed only when its distance-to-log is exactly one more than the cell we
     *     arrived from. A neighbouring tree's leaves measure their distance from <em>their own</em>
     *     trunk, so the increasing-distance chain breaks at the canopy seam and never crosses over.</li>
     * </ol>
     * Bounded by {@link #MAX_TRUNK_HEIGHT} for safety.
     */
    private List<BlockPos> collectTreeBlocks(ServerLevel level, BlockPos base) {
        List<BlockPos> result = new ArrayList<>();

        // Phase 1: this tree's logs (and branch logs), reached through log-only adjacency.
        Set<BlockPos> logs = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(base);
        visited.add(base);
        while (!queue.isEmpty() && logs.size() < MAX_TRUNK_HEIGHT * 8) {
            BlockPos current = queue.poll();
            if (!isLog(level.getBlockState(current))) continue;
            logs.add(current.immutable());
            result.add(current.immutable());

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos neighbor = current.offset(dx, dy, dz);
                        // Never wander below the base (that's the ground/roots).
                        if (neighbor.getY() < base.getY()) continue;
                        if (visited.add(neighbor) && isLog(level.getBlockState(neighbor))) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        // Phase 2: leaves belonging to this crown. Walk outward from the logs, only ever claiming a
        // leaf whose recorded distance-to-trunk steps *up* by one from the cell we came from. This is
        // exactly how vanilla propagates LeavesBlock.DISTANCE, so we follow one tree's own gradient and
        // stop where a neighbour's canopy (measuring distance from its own trunk) meets ours.
        Set<BlockPos> claimedLeaves = new HashSet<>();
        Queue<BlockPos> leafQueue = new ArrayDeque<>();
        // Seed the frontier: leaves adjacent to our logs sit at distance 1.
        for (BlockPos log : logs) {
            for (Direction dir : Direction.values()) {
                BlockPos side = log.relative(dir);
                if (side.getY() < base.getY()) continue;
                if (isDistanceLeaf(level, side, 1) && claimedLeaves.add(side.immutable())) {
                    result.add(side.immutable());
                    leafQueue.add(side.immutable());
                }
            }
        }
        while (!leafQueue.isEmpty() && result.size() < MAX_TRUNK_HEIGHT * 16) {
            BlockPos current = leafQueue.poll();
            int currentDist = level.getBlockState(current).getValue(LeavesBlock.DISTANCE);
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (neighbor.getY() < base.getY()) continue;
                // Next leaf must sit exactly one step further from the trunk to stay on our gradient.
                if (isDistanceLeaf(level, neighbor, currentDist + 1) && claimedLeaves.add(neighbor.immutable())) {
                    result.add(neighbor.immutable());
                    leafQueue.add(neighbor.immutable());
                }
            }
        }
        return result;
    }

    /** True if {@code pos} is a leaf whose {@link LeavesBlock#DISTANCE} equals {@code expected}. */
    private boolean isDistanceLeaf(ServerLevel level, BlockPos pos, int expected) {
        BlockState state = level.getBlockState(pos);
        return state.is(BlockTags.LEAVES)
                && state.hasProperty(LeavesBlock.DISTANCE)
                && state.getValue(LeavesBlock.DISTANCE) == expected;
    }

    /** The log type of a trunk column (probes the base cell, falling back to the block above it). */
    private Block trunkLogType(ServerLevel level, BlockPos column) {
        BlockState state = level.getBlockState(column);
        if (isLog(state)) return state.getBlock();
        BlockState above = level.getBlockState(column.above());
        return isLog(above) ? above.getBlock() : null;
    }

    // ------------------------------------------------------------------
    // Branch geometry
    // ------------------------------------------------------------------

    /**
     * Draws a bare-log branch climbing outward and upward from {@code origin} in {@code dir}. Logs
     * are laid horizontally along the branch axis. Branches always angle up; the family's style sets
     * how steep the climb is:
     * <ul>
     *     <li>SMOOTH (oak, birch) — gentle ~30 degrees: step out roughly twice for every step up.</li>
     *     <li>SHARP (jungle) — steeper ~45 degrees: step out and up together.</li>
     * </ul>
     * No vertical connector blocks are placed, so the branch reads as a clean diagonal run.
     */
    private void placeBranch(ServerLevel level, BlockPos origin, Direction dir, String treeType, int trunkWidth) {
        BranchStyle style = branchStyleFor(treeType);
        Block log = level.getBlockState(origin).getBlock();
        // Lay logs horizontally along the branch's axis (the trunk logs stay vertical).
        BlockState logState = log.defaultBlockState().setValue(RotatedPillarBlock.AXIS, dir.getAxis());
        int length = 3 + level.random.nextInt(4); // 3..6

        // Steps taken outward between each upward step. Smooth climbs shallowly, sharp steeply.
        int outPerUp = (style == BranchStyle.SHARP) ? 1 : 2;

        BlockPos cursor = origin;
        int outSinceUp = 0;
        for (int step = 1; step <= length; step++) {
            cursor = cursor.relative(dir);
            placeLogState(level, cursor, logState);

            // 2-wide trunks start with a 2-wide branch for the first block.
            if (trunkWidth == 2 && step == 1) {
                placeLogState(level, cursor.relative(dir.getClockWise()), logState);
            }

            // Climb one block up after enough outward steps, without a vertical connector log.
            outSinceUp++;
            if (outSinceUp >= outPerUp && step < length) {
                cursor = cursor.above();
                outSinceUp = 0;
            }
        }
    }

    private BranchStyle branchStyleFor(String treeType) {
        return switch (treeType) {
            case "jungle" -> BranchStyle.SHARP;
            // oak, birch -> smooth. dark oak / acacia / cherry / spruce are out of scope for v1.
            default -> BranchStyle.SMOOTH;
        };
    }

    // ------------------------------------------------------------------
    // Trunk / root discovery
    // ------------------------------------------------------------------

    /**
     * Flood-fills down and sideways through connected log blocks to find the lowest trunk block.
     * For 2x2 trees this settles on the north-west column at ground level.
     */
    private BlockPos findTreeBase(BlockPos start, ServerLevel level) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        BlockPos lowest = start;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (current.getY() < lowest.getY()
                    || (current.getY() == lowest.getY() && isMoreNorthWest(current, lowest))) {
                lowest = current;
            }

            // Prefer following the trunk straight down first.
            BlockPos below = current.below();
            if (visited.add(below) && isLog(level.getBlockState(below))) {
                queue.add(below);
            }

            // Then spread sideways/down-diagonally to catch 2x2 trunks and slanted logs.
            for (int yOff = -1; yOff <= 0; yOff++) {
                for (int xOff = -1; xOff <= 1; xOff++) {
                    for (int zOff = -1; zOff <= 1; zOff++) {
                        if (xOff == 0 && zOff == 0) continue;
                        BlockPos neighbor = current.offset(xOff, yOff, zOff);
                        if (visited.add(neighbor) && isLog(level.getBlockState(neighbor))) {
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return lowest;
    }

    /**
     * Detects whether the trunk is 1 or 2 blocks wide by probing for a 2x2 log footprint whose
     * north-west corner is {@code base}.
     */
    private int detectTrunkWidth(ServerLevel level, BlockPos base, Block log) {
        boolean twoByTwo =
                sameLog(level, base.east(), log)
                && sameLog(level, base.south(), log)
                && sameLog(level, base.east().south(), log);
        return twoByTwo ? 2 : 1;
    }

    /** The trunk column origins for a tree of the given width, anchored at the NW base block. */
    private Iterable<BlockPos> trunkColumns(BlockPos base, int trunkWidth) {
        if (trunkWidth == 2) {
            return Set.of(base, base.east(), base.south(), base.east().south());
        }
        return Set.of(base);
    }

    // ------------------------------------------------------------------
    // Small helpers
    // ------------------------------------------------------------------

    private boolean isSupportedSapling(BlockState state) {
        // Any sapling in the tag can be coaxed into a tree, vanilla or modded.
        return state.is(BlockTags.SAPLINGS);
    }

    private boolean isSupportedLog(BlockState state) {
        // General case: anything in #minecraft:logs forms a growable trunk, regardless of family.
        return state.is(BlockTags.LOGS);
    }

    private boolean isLog(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    /** A supported log laid on its side (branch log). Trunk logs stand upright on the Y axis. */
    private boolean isHorizontalLog(BlockState state) {
        return isLog(state)
                && state.hasProperty(RotatedPillarBlock.AXIS)
                && state.getValue(RotatedPillarBlock.AXIS) != Direction.Axis.Y;
    }

    /** A same-family log that stands as part of the trunk (upright or axis-less), never a laid branch. */
    private boolean isTrunkLog(ServerLevel level, BlockPos pos, String treeType) {
        BlockState state = level.getBlockState(pos);
        return isLog(state) && !isHorizontalLog(state) && getTreeType(state).equals(treeType);
    }

    /** The trunk's current height in logs (follows the trunk even if it leans). */
    private int trunkLogCount(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        return trunkPath(level, base, treeType, trunkWidth).size();
    }

    /**
     * Walks the trunk upward from the base, following it even when it kinks sideways (a lean). Each
     * step looks for a same-family log directly above; failing that, one diagonally above in any of
     * the four horizontal directions. Returns the ordered trunk column blocks, bottom-first.
     */
    private List<BlockPos> trunkPath(ServerLevel level, BlockPos base, String treeType, int trunkWidth) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = base;
        path.add(current);

        for (int i = 0; i < MAX_TRUNK_HEIGHT; i++) {
            BlockPos above = current.above();
            BlockPos next = null;
            if (isTrunkLog(level, above, treeType)) {
                next = above;
            } else {
                for (Direction dir : BRANCH_DIRECTIONS) {
                    BlockPos diag = above.relative(dir);
                    if (isTrunkLog(level, diag, treeType)) {
                        next = diag;
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

    private boolean sameLog(ServerLevel level, BlockPos pos, Block log) {
        return level.getBlockState(pos).is(log);
    }

    private boolean isValidGround(BlockState state) {
        // Trees may sit on any dirt-like block, not just plain dirt.
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) || state.is(Blocks.MUD);
    }

    /** A branch/trunk block may replace air, foliage and other soft blocks, but never solid logs. */
    private boolean canOccupy(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced() || state.is(BlockTags.LEAVES);
    }

    private void placeLogState(ServerLevel level, BlockPos pos, BlockState logState) {
        if (canOccupy(level, pos)) {
            level.setBlock(pos, logState, 3);
        }
    }

    private boolean isMoreNorthWest(BlockPos a, BlockPos b) {
        // "North-west" = smaller Z (north) then smaller X (west).
        if (a.getZ() != b.getZ()) return a.getZ() < b.getZ();
        return a.getX() < b.getX();
    }

    /**
     * Derives a tree "family" key from the log's registry path, e.g. {@code spruce_log -> "spruce"},
     * {@code stripped_dark_oak_wood -> "dark_oak"}, {@code some_mod:magic_log -> "magic"}. Used to keep
     * the trunk walk on same-family logs and to pick a branch style. Family-agnostic: works for any
     * modded or vanilla log rather than a fixed oak/birch/jungle list.
     */
    private String getTreeType(BlockState state) {
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        // Normalise away the common log/wood decorations so all forms of one family collapse together.
        path = path.replace("stripped_", "")
                .replace("_log", "")
                .replace("_wood", "")
                .replace("_stem", "")
                .replace("_hyphae", "");
        return path.isEmpty() ? "oak" : path;
    }
}
