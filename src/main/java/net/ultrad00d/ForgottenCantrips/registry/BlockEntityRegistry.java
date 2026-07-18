package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.blockentity.OldWizardSpawnerBlockEntity;
import net.ultrad00d.ForgottenCantrips.blockentity.SpectralBedBlockEntity;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<BlockEntityType<SpectralBedBlockEntity>> SPECTRAL_BED_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("spectral_bed_block_entity", () ->
                    BlockEntityType.Builder.of(SpectralBedBlockEntity::new, BlockRegistry.SPECTRAL_BED.get())
                            .build(null)
            );

    public static final RegistryObject<BlockEntityType<OldWizardSpawnerBlockEntity>> OLD_WIZARD_SPAWNER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("old_wizard_spawner_block_entity", () ->
                    BlockEntityType.Builder.of(OldWizardSpawnerBlockEntity::new, BlockRegistry.OLD_WIZARD_SPAWNER.get())
                            .build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
