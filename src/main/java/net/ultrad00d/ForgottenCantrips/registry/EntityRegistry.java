package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.OldWizard;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;
import net.ultrad00d.ForgottenCantrips.entity.SpectralDonkey;
import net.ultrad00d.ForgottenCantrips.entity.SpectralSlime;
import net.ultrad00d.ForgottenCantrips.entity.SpectralSlimeSpit;

public final class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<EntityType<SpectralBoat>> SPECTRAL_BOAT =
            ENTITY_TYPES.register("spectral_boat", () ->
                    EntityType.Builder.of(SpectralBoat::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .build(ForgottenCantrips.MOD_ID + ":spectral_boat")
            );

    public static final RegistryObject<EntityType<SpectralDonkey>> SPECTRAL_DONKEY =
            ENTITY_TYPES.register("spectral_donkey", () ->
                    EntityType.Builder.of(SpectralDonkey::new, MobCategory.CREATURE)
                            .sized(1.3964844F, 1.5F)
                            .build(ForgottenCantrips.MOD_ID + ":spectral_donkey")
            );

    public static final RegistryObject<EntityType<OldWizard>> OLD_WIZARD =
            ENTITY_TYPES.register("old_wizard", () ->
                    EntityType.Builder.of(OldWizard::new, MobCategory.MISC)
                            .sized(0.7F, 1.15F)
                            .build(ForgottenCantrips.MOD_ID + ":old_wizard")
            );

    public static final RegistryObject<EntityType<SpectralSlime>> SPECTRAL_SLIME =
            ENTITY_TYPES.register("spectral_slime", () ->
                    EntityType.Builder.of(SpectralSlime::new, MobCategory.CREATURE)
                            .sized(0.52F, 0.52F)
                            .clientTrackingRange(64)
                            .build("forgotten_cantrips:spectral_slime")
            );

    public static final RegistryObject<EntityType<SpectralSlimeSpit>> SPECTRAL_SLIME_SPIT =
            ENTITY_TYPES.register("spectral_slime_spit", () ->
                    EntityType.Builder.<SpectralSlimeSpit>of(SpectralSlimeSpit::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(10)
                            .build("forgotten_cantrips:spectral_slime_spit")
            );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
