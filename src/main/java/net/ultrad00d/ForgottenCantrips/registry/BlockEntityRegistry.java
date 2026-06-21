package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.block.SpectralBedBlock;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBedBlockEntity;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<BlockEntityType<SpectralBedBlockEntity>> SPECTRAL_BED_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("spectral_bed_block_entity", () ->
                    BlockEntityType.Builder.of(SpectralBedBlockEntity::new, BlockRegistry.SPECTRAL_BED.get())
                            .build(null)
            );
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
