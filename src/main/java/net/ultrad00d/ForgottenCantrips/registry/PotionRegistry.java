package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;

public class PotionRegistry {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<Potion> UNDYING = POTIONS.register("undying",
        () -> new Potion(new MobEffectInstance(EffectRegistry.UNDYING.get(), 1800)));

    public static final RegistryObject<Potion> UNDYING_LONG = POTIONS.register("undying_long",
        () -> new Potion("undying", new MobEffectInstance(EffectRegistry.UNDYING.get(), 4800)));

    public static final RegistryObject<Potion> ILLUMINATION = POTIONS.register("illumination",
        () -> new Potion(new MobEffectInstance(EffectRegistry.ILLUMINATION.get(), 1800)));

    public static final RegistryObject<Potion> ILLUMINATION_LONG = POTIONS.register("illumination_long",
        () -> new Potion("illumination", new MobEffectInstance(EffectRegistry.ILLUMINATION.get(), 4800)));

    public static final RegistryObject<Potion> ILLUMINATION_STRONG = POTIONS.register("illumination_strong",
        () -> new Potion("illumination", new MobEffectInstance(EffectRegistry.ILLUMINATION.get(), 900, 1)));

    public static final RegistryObject<Potion> AETHER_STRIDE = POTIONS.register("aether_stride",
        () -> new Potion(new MobEffectInstance(EffectRegistry.AETHER_STRIDE.get(), 1800)));

    public static final RegistryObject<Potion> AETHER_STRIDE_LONG = POTIONS.register("aether_stride_long",
        () -> new Potion("aether_stride", new MobEffectInstance(EffectRegistry.AETHER_STRIDE.get(), 4800)));

    public static final RegistryObject<Potion> AETHER_STRIDE_STRONG = POTIONS.register("aether_stride_strong",
        () -> new Potion("aether_stride", new MobEffectInstance(EffectRegistry.AETHER_STRIDE.get(), 600, 1)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}