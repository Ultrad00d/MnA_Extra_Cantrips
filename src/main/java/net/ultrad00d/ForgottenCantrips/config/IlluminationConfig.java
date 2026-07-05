package net.ultrad00d.ForgottenCantrips.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class IlluminationConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> RADIUS_EXT;
    public static final ForgeConfigSpec.ConfigValue<Integer> MAX_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Integer> TICK_PERIOD;

    static {
        BUILDER.push("Illumination");

        RADIUS_EXT = BUILDER
            .comment("Radius extension of the illumination light cylinder (0-2)")
            .defineInRange("radius_ext", 0, 0, 1);

        MAX_RADIUS = BUILDER
            .comment("Maximal radius of the illumination light cylinder (1-15)")
            .defineInRange("max_radius", 15, 1, 15);

        TICK_PERIOD = BUILDER
            .comment("How many ticks between light refreshes (1-10, 10 = 0.5 seconds)")
            .defineInRange("tick_period", 5, 1, 10);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}