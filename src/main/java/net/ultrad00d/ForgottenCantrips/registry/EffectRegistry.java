package net.ultrad00d.ForgottenCantrips.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ultrad00d.ForgottenCantrips.ForgottenCantrips;
import net.ultrad00d.ForgottenCantrips.effect.AetherStrideEffect;
import net.ultrad00d.ForgottenCantrips.effect.BubbleUpEffect;
import net.ultrad00d.ForgottenCantrips.effect.EmpowerCantripBuffEffect;
import net.ultrad00d.ForgottenCantrips.effect.EmpowerDamageBuffEffect;
import net.ultrad00d.ForgottenCantrips.effect.EmpowerManaCostBuffEffect;
import net.ultrad00d.ForgottenCantrips.effect.IlluminationEffect;
import net.ultrad00d.ForgottenCantrips.effect.SpectralArmorEffect;
import net.ultrad00d.ForgottenCantrips.effect.UndyingEffect;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ForgottenCantrips.MOD_ID);

    public static final RegistryObject<MobEffect> UNDYING = MOB_EFFECTS.register("undying",
            UndyingEffect::new);

    public static final RegistryObject<MobEffect> ILLUMINATION = MOB_EFFECTS.register("illumination",
            IlluminationEffect::new);

    public static final RegistryObject<MobEffect> AETHER_STRIDE = MOB_EFFECTS.register("aether_stride",
            AetherStrideEffect::new);

    public static final RegistryObject<MobEffect> EMPOWER_MANA_COST_BUFF = MOB_EFFECTS.register("empower_mana_cost_buff",
            EmpowerManaCostBuffEffect::new);

    public static final RegistryObject<MobEffect> EMPOWER_DAMAGE_BUFF = MOB_EFFECTS.register("empower_damage_buff",
            EmpowerDamageBuffEffect::new);

    public static final RegistryObject<MobEffect> EMPOWER_CANTRIP_BUFF = MOB_EFFECTS.register("empower_cantrip_buff",
            EmpowerCantripBuffEffect::new);

    public static final RegistryObject<MobEffect> SPECTRAL_ARMOR = MOB_EFFECTS.register("spectral_armor",
            SpectralArmorEffect::new);


    public static final RegistryObject<MobEffect> BUBBLE_UP = MOB_EFFECTS.register("bubble_up",
            BubbleUpEffect::new);


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
