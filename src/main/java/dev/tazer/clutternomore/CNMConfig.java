package dev.tazer.clutternomore;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CNMConfig {

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;

    static {

        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        COMMON_CONFIG = COMMON_BUILDER.build();

        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}