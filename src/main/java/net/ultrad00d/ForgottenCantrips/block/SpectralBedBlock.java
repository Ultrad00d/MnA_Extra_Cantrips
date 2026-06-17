package net.ultrad00d.ForgottenCantrips.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;
import org.jetbrains.annotations.NotNull;

public class SpectralBedBlock extends BedBlock {
    public SpectralBedBlock() {
        super(DyeColor.CYAN, BlockBehaviour.Properties.of().mapColor((p_284863_) -> {
            return p_284863_.getValue(BedBlock.PART) == BedPart.FOOT ? MapColor.COLOR_PURPLE : MapColor.COLOR_CYAN;
        }).sound(SoundType.AMETHYST).strength(0.2F).noOcclusion().ignitedByLava().pushReaction(PushReaction.DESTROY));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SpectralBedBlockEntity(pos, state); }
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
