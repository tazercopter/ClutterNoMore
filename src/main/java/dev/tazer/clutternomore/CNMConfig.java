package dev.tazer.clutternomore;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CNMConfig {

    public static ModConfigSpec COMMON_CONFIG;
    public static ModConfigSpec CLIENT_CONFIG;
    public static ModConfigSpec.BooleanValue SCROLLING;
    public static ModConfigSpec.BooleanValue HOLD;

    static {

        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

        COMMON_CONFIG = COMMON_BUILDER.build();

        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        SCROLLING = CLIENT_BUILDER
                .comment("If the shape switcher menu should be scrolling or static")
                .define("scrolling", true);
        HOLD = CLIENT_BUILDER
                .comment("If the change block shape key should be held or toggled to open the menu")
                .define("hold", true);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}