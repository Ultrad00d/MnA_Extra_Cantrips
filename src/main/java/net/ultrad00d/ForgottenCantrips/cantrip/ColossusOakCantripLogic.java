package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ColossusOakCantripLogic extends CantripLogic {
    private static final Set<Block> TARGET_BLOCKS = Set.of(
            Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING,
            Blocks.OAK_WOOD,    Blocks.BIRCH_WOOD,    Blocks.JUNGLE_WOOD,
            Blocks.OAK_LOG,     Blocks.BIRCH_LOG,     Blocks.JUNGLE_LOG
    );

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        HitResult rayHit = player.pick(player.getBlockReach(), 0.0F, false);

        if (rayHit.getType() != BlockHitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.toofar"));
            return;
        }
        BlockPos bPos = ((BlockHitResult) rayHit).getBlockPos();
        ServerLevel serverLevel = (ServerLevel) player.level();
        if (!isTreeBlock(serverLevel.getBlockState(bPos))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.not_growable"));
            return;
        }

        BlockPos source = findTreeSource(bPos, serverLevel);
        serverLevel.setBlock(source, Blocks.OBSIDIAN.defaultBlockState(), 3);
    }

    private boolean isTreeBlock(BlockState blockState) {
//        return !blockState.isAir() && TARGET_BLOCKS.contains(blockState.getBlock());
        return blockState.getBlock().equals(Blocks.GLASS);
    }

    private BlockPos findTreeSource(BlockPos pos, ServerLevel level) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(pos);
        visited.add(pos);

        // Keep track of the absolute lowest tree block we find
        BlockPos lowestRoot = pos;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            // Track the lowest position found so far
            if (current.getY() < lowestRoot.getY()) {
                lowestRoot = current;
            }

            // Search neighbor offsets: Y from -1 (down) to 0 (same level/sideways)
            for (int yOffset = -1; yOffset <= 0; yOffset++) {
                BlockPos below = current.below();
                visited.add(below);
                if (isTreeBlock(level.getBlockState(below))) {
                    queue.add(below);
                    continue;
                }

                for (int xOffset = -1; xOffset <= 1; xOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {

                        // Skip the current block itself
                        if (xOffset == 0 && zOffset == 0) continue;

                        BlockPos neighbor = current.offset(xOffset, yOffset, zOffset);

                        // If we haven't checked this block yet
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);

                            BlockState neighborState = level.getBlockState(neighbor);

                            if (isTreeBlock(neighborState)) queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return lowestRoot;
    }
}
