// Config.java
package net.emilyonaire.worldlimiter;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue WORLD_LIMIT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        WORLD_LIMIT = builder
                .comment("Maximum number of worlds allowed")
                .defineInRange("worldLimit", 3, 1, 100);

        SPEC = builder.build();
    }
}