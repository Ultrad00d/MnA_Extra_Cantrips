package net.ultrad00d.ForgottenCantrips.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class StructureUtil {
    public static boolean isInsideProtectedStructure(LevelAccessor level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            ResourceKey<Structure> oldWizardHouseStructureKey = ResourceKey.create(
                    Registries.STRUCTURE,
                    ResourceLocation.fromNamespaceAndPath(ForgottenCantrips.MOD_ID, "old_wizard_house")
            );

            Structure structure = serverLevel.registryAccess()
                    .registryOrThrow(Registries.STRUCTURE)
                    .get(oldWizardHouseStructureKey);

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
}
