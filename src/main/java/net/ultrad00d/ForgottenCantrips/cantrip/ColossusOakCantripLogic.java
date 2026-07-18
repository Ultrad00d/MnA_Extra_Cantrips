package net.ultrad00d.ForgottenCantrips.cantrip;

import com.mna.api.cantrips.ICantrip;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.block.ColossusTreeRootsBlock;
import net.ultrad00d.ForgottenCantrips.blockentity.ColossusTreeRootsBlockEntity;
import net.ultrad00d.ForgottenCantrips.registry.BlockRegistry;

import java.util.*;

public class ColossusOakCantripLogic implements ICantripLogic {
    @Override
    public String getCantripId() { return "colossus_oak"; }

    private static final Set<Block> TARGET_BLOCKS = Set.of(
            Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING,
            Blocks.OAK_WOOD,    Blocks.BIRCH_WOOD,    Blocks.JUNGLE_WOOD,
            Blocks.OAK_LOG,     Blocks.BIRCH_LOG,     Blocks.JUNGLE_LOG
    );

    @Override
    public void run(Player player, ICantrip cantrip, InteractionHand hand) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        // Hit detect
        HitResult rayHit = player.pick(player.getBlockReach(), 0.0F, false);

        if (rayHit.getType() != BlockHitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.toofar"));
            return;
        }
        BlockPos bPos = ((BlockHitResult) rayHit).getBlockPos();
        if (!isTreeBlock(serverLevel.getBlockState(bPos))) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.not_growable"));
            return;
        }

        // Look for root
        BlockPos rootPos = findTreeSource(bPos, serverLevel);
        BlockState rootBlockState = serverLevel.getBlockState(rootPos);
        int treeTier;

        // get current tier
        boolean isRooted = rootBlockState.getBlock() instanceof ColossusTreeRootsBlock;
        if (isRooted) {
            treeTier = rootBlockState.getValue(ColossusTreeRootsBlock.TIER);
        } else if (rootBlockState.is(Blocks.DIRT)) {
            treeTier = 1;
        } else {
            // if root block doesn't exist
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.root_destroyed"));
            return;
        }

        // to grow or to stop?
        if (treeTier == 3) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.max_tier"));
            return;
        }

        treeTier++;
        ColossusTreeRootsBlockEntity rootsBlockEntity = null;
        Direction growthDirection;

        // Reading root data
        if (isRooted) {
            growthDirection = rootBlockState.getValue(ColossusTreeRootsBlock.ROTATION);

            rootsBlockEntity = (ColossusTreeRootsBlockEntity) serverLevel.getBlockEntity(rootPos);
            if (rootsBlockEntity != null) {
                // Check integrity threshold before allowing upgrade
                double health = rootsBlockEntity.calculateHealth(serverLevel);
                if (health < 75.0) {
                    player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.too_damaged"));
                    return;
                }
            }
        } else { // Vanilla tree
            growthDirection = player.getDirection();

            BlockState initialRootState = BlockRegistry.COLOSSUS_OAK_ROOTS.get().defaultBlockState()
                    .setValue(ColossusTreeRootsBlock.TIER, 1)
                    .setValue(ColossusTreeRootsBlock.ROTATION, growthDirection);
            serverLevel.setBlock(rootPos, initialRootState, 3);
            rootsBlockEntity = (ColossusTreeRootsBlockEntity) serverLevel.getBlockEntity(rootPos);
        }

        if (rootsBlockEntity == null) {
            player.sendSystemMessage(Component.translatable("cantrip.forgotten_cantrips.colossus_oak.root_failed_to_create"));
            return;
        }

        String treeType = getTreeType(serverLevel.getBlockState(bPos));

        // Execute structure growth
        // rotation is root rotation, or player rotation when cast, if unset
        boolean success = growTreeStructure(player, serverLevel, rootPos, rootsBlockEntity, treeType, treeTier, growthDirection.getSerializedName());

        if (success) {
            // updating root logic, but it crashes MC right now

            // Finally, update the blockstate property to the newly achieved tier
//            BlockState updatedRootState = serverLevel.getBlockState(rootPos)
//                    .setValue(ColossusTreeRootsBlock.TIER, treeTier)
//                    .setValue(ColossusTreeRootsBlock.ROTATION, growthDirection);
//            serverLevel.setBlock(rootPos, updatedRootState, 3);

//            rootsBlockEntity.setChanged(); // Notify Minecraft data has changed
        }
    }

    private String getTreeType(BlockState state) {
        String blockId = state.getBlock().getDescriptionId();
        if (blockId.contains("birch")) return "birch";
        if (blockId.contains("jungle")) return "jungle";
        return "oak";
    }

    /**
     Lazy implementation (using structure from {@link PlaceCommand#placeTemplate(CommandSourceStack, ResourceLocation, BlockPos, Rotation, Mirror, float, int)})
     */
    private boolean growTreeStructure(Player player, ServerLevel level, BlockPos rootPos, ColossusTreeRootsBlockEntity blockEntity, String type, int targetTier, String rotation) {
        ResourceLocation structureLoc = ResourceLocation.fromNamespaceAndPath(
                ForgottenCantrips.MOD_ID, "tree/" + type + "/t" + targetTier + "/" + rotation
        );

        StructureTemplateManager structuretemplatemanager = level.getStructureManager();
        Optional<StructureTemplate> optional;
        optional = structuretemplatemanager.get(structureLoc);

        if (optional.isEmpty()) return false;

        StructureTemplate structuretemplate = optional.get();

        StructurePlaceSettings structureplacesettings = new StructurePlaceSettings();

        structuretemplate.placeInWorld(level, rootPos.offset(-4, 1, -4), rootPos.offset(-4, 1, -4), structureplacesettings, StructureBlockEntity.createRandom(0), 3);
        // Offsets differ per view direction:
        // north -5 1 -4
        // west  -4 1 -4
        // south -4 1 -4
        // east  -4 1 -5
        return true;



        // Some Gemini spaghetti code, which is what we need, but not what actually works

//
//        var templateOpt = level.getServer().getStructureManager().get(structureLoc);
//        if (templateOpt.isEmpty()) return false;
//
//        StructureTemplate template = templateOpt.get();
//
//        // Calculate structure origin (The template's center base should anchor right above the root pos)
//        // Adjust the offset vector if your NBT file pivots are designed differently!
//        // TODO change this shi
////        BlockPos structureOrigin = rootPos.above();
//
//        Vec3i size = template.getSize();
//        int groundOffsetX = size.getX() / 2;
//        int groundOffsetZ = size.getZ() / 2;
//        BlockPos structureOrigin = rootPos.above().offset(-groundOffsetX, 0, -groundOffsetZ);
//        player.sendSystemMessage(Component.literal(String.valueOf(structureOrigin)));
//        List<StructureTemplate.StructureBlockInfo> blockInfos = template.filterBlocks(
//                structureOrigin,
//                new StructurePlaceSettings().setIgnoreEntities(true),
//                Blocks.AIR
//        );
//
//        // 2. Sort blocks: Layer-by-layer (Y), then inside-out (Distance from center axis)
//        blockInfos.sort((b1, b2) -> {
//            int yCompare = Integer.compare(b1.pos().getY(), b2.pos().getY());
//            if (yCompare != 0) return yCompare;
//
//            double dx1 = b1.pos().getX() - structureOrigin.getX();
//            double dz1 = b1.pos().getZ() - structureOrigin.getZ();
//            double dist1 = (dx1 * dx1) + (dz1 * dz1);
//
//            double dx2 = b2.pos().getX() - structureOrigin.getX();
//            double dz2 = b2.pos().getZ() - structureOrigin.getZ();
//            double dist2 = (dx2 * dx2) + (dz2 * dz2);
//            return Double.compare(dist1, dist2);
//        });
//
//        // 3. Prepare BlockEntity trackers for rewriting
//        blockEntity.getTrackedBlocks().clear();
//        int placedCount = 0;
//
//        // 4. Place blocks sequentially (TODO: Or delegate to a scheduled ticker loop if you want it animated)
//        for (var info : blockInfos) {
//            BlockState targetWorldState = level.getBlockState(info.pos());
//
//            // Obstruction Check: Is the spot empty, or can it be replaced safely?
//            if (targetWorldState.isAir() || targetWorldState.canBeReplaced() || targetWorldState.is(BlockTags.LEAVES)) {
//                level.setBlock(info.pos(), info.state(), 3);
//
//                // Record this absolute position in our block entity tracking registry
//                blockEntity.getTrackedBlocks().add(info.pos());
//                placedCount++;
//            }
//        }
//        player.sendSystemMessage(Component.literal(String.valueOf(placedCount)));
//
//        // Set how many blocks actually survived the obstruction pruning process
//        blockEntity.setExpectedBlocks(placedCount);
//        return true;
    }

    private boolean isTreeBlock(BlockState blockState) {
        return !blockState.isAir() && TARGET_BLOCKS.contains(blockState.getBlock());
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

        return lowestRoot.below();
    }
}
