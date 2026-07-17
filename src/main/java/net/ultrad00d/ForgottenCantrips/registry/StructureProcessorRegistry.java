package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.structure.OldWizardHouseSpawnProcessor;

public class StructureProcessorRegistry {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<StructureProcessorType<OldWizardHouseSpawnProcessor>> OLD_WIZARD_SPAWNER =
            PROCESSORS.register("old_wizard_spawner", () -> () -> OldWizardHouseSpawnProcessor.CODEC);

    public static void register(IEventBus eventBus) {
        PROCESSORS.register(eventBus);
    }
}
