package net.ultrad00d.ForgottenCantrips.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class IlluminationConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> RADIUS_EXT;
    public static final ForgeConfigSpec.ConfigValue<Integer> TICK_PERIOD;

    static {
        BUILDER.push("Illumination");

        RADIUS_EXT = BUILDER
            .comment("Radius extension of the illumination light cylinder (0-1)")
            .defineInRange("radius_ext", 0, 0, 1);

        TICK_PERIOD = BUILDER
            .comment("How many ticks between light refreshes (1-10, 10 = 0.5 seconds)")
            .defineInRange("tick_period", 5, 1, 10);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}