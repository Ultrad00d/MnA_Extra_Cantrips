package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.entity.SpectralBoat;

public final class EntityRegistry
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<EntityType<SpectralBoat>> SPECTRAL_BOAT =
            ENTITY_TYPES.register("spectral_boat", () ->
                    EntityType.Builder.<SpectralBoat>of(SpectralBoat::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .build("spectral_boat")
            );

    private EntityRegistry()
    {
    }
}
