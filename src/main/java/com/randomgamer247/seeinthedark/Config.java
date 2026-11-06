package com.randomgamer247.seeinthedark;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Config for See in the Dark
 */
public class Config {
    public enum ENERGY_UNITS {
        FE ("text.seeinthedark.energy_unit_FE"),
        RF ("text.seeinthedark.energy_unit_RF");

        public final String unit_key;

        ENERGY_UNITS(String unit_key) {
            this.unit_key = unit_key;
        }
    }

    private static final ModConfigSpec.Builder BUILDER_SERVER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder BUILDER_CLIENT = new ModConfigSpec.Builder();

    // SERVER CONFIG OPTIONS
    public static final ModConfigSpec.IntValue NIGHT_VISION_GOGGLES_MAX_ENERGY = BUILDER_SERVER
            .worldRestart()
            .comment("Night vision goggles energy capacity (requires world restart)")
            .defineInRange("nightVisionMaxEnergy", 800000, 200, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue NIGHT_VISION_GOGGLES_ENERGY_RECEIVE = BUILDER_SERVER
            .worldRestart()
            .comment("Night vision goggles energy receive per tick, affects max charging speed (requires world restart)")
            .defineInRange("nightVisionEnergyTransfer", 10000, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue NIGHT_VISION_GOGGLES_ENERGY_USAGE = BUILDER_SERVER
            .worldRestart()
            .comment("Night vision goggles energy usage per tick, set to 0 to allow night vision to be used indefinitely (requires world restart)")
            .defineInRange("nightVisionEnergyUse", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLUX_BOLT_MAX_ENERGY = BUILDER_SERVER
            .worldRestart()
            .comment("Flux bolt energy capacity (requires world restart)")
            .defineInRange("fluxBoltMaxEnergy", 1600000, 200, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLUX_BOLT_ENERGY_RECEIVE = BUILDER_SERVER
            .worldRestart()
            .comment("Flux bolt energy receive per tick, affects max charging speed (requires world restart)")
            .defineInRange("fluxBoltEnergyTransfer", 10000, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue FLUX_BOLT_ENERGY_USAGE = BUILDER_SERVER
            .worldRestart()
            .comment("Flux bolt energy usage per shot, set to 0 to fire arrows for completely free (requires world restart)")
            .defineInRange("fluxBoltEnergyUse", 8000, 0, Integer.MAX_VALUE);

    // CLIENT CONFIG OPTIONS
    public static final ModConfigSpec.ConfigValue<ENERGY_UNITS> ENERGY_UNIT_VISUAL = BUILDER_CLIENT
            .comment("Energy unit setting for nostalgists, purely visual")
            .defineEnum("energyUnitConfig", ENERGY_UNITS.FE, ENERGY_UNITS.values());

    static final ModConfigSpec SPEC_SERVER = BUILDER_SERVER.build();
    static final ModConfigSpec SPEC_CLIENT = BUILDER_CLIENT.build();
}
