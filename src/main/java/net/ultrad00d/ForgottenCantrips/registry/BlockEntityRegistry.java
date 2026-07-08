package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.blockentity.ColossusTreeRootsBlockEntity;
import net.ultrad00d.ForgottenCantrips.blockentity.SpectralBedBlockEntity;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<BlockEntityType<SpectralBedBlockEntity>> SPECTRAL_BED_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("spectral_bed_block_entity", () ->
                    BlockEntityType.Builder.of(SpectralBedBlockEntity::new, BlockRegistry.SPECTRAL_BED.get())
                            .build(null)
            );

    public static final RegistryObject<BlockEntityType<ColossusTreeRootsBlockEntity>> COLOSSUS_TREE_ROOTS_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("colossus_tree_roots", () ->
                    BlockEntityType.Builder.of(
                            ColossusTreeRootsBlockEntity::new,
                            BlockRegistry.COLOSSUS_OAK_ROOTS.get(),
                            BlockRegistry.COLOSSUS_BIRCH_ROOTS.get(),
                            BlockRegistry.COLOSSUS_JUNGLE_ROOTS.get()
                        ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
