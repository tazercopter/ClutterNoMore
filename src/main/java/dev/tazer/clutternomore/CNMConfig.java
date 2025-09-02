package dev.tazer.clutternomore;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CNMConfig {

    public static ModConfigSpec STARTUP_CONFIG;
    public static ModConfigSpec.BooleanValue VERTICAL_SLABS;
    public static ModConfigSpec.BooleanValue STEPS;
    public static ModConfigSpec CLIENT_CONFIG;
    public static ModConfigSpec.BooleanValue SCROLLING;
    public static ModConfigSpec.EnumValue<InputType> HOLD;

    public enum InputType implements StringRepresentable {
        HOLD,
        TOGGLE,
        PRESS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        @Override
        public String getSerializedName() {
            return toString();
        }

        public static final Codec<InputType> CODEC = StringRepresentable.fromEnum(InputType::values);
    }

    static {

        ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
        COMMON_BUILDER.push("shapes");
        COMMON_BUILDER.comment("Ensure these values are the same as the servers you're joining or issues may arise");
        VERTICAL_SLABS = COMMON_BUILDER
                .comment("If vertical slabs should be added to all existing slabs")
                .define("verticalSlabs", true);
        STEPS = COMMON_BUILDER
                .comment("If steps should be added to all existing stairs")
                .define("steps", true);
        COMMON_BUILDER.pop();
        STARTUP_CONFIG = COMMON_BUILDER.build();

        ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

        SCROLLING = CLIENT_BUILDER
                .comment("If the shape switcher menu should be scrolling or static")
                .define("scrolling", true);
        HOLD = CLIENT_BUILDER
                .comment("If the change block shape key should be held or toggled to open the menu")
                .defineEnum("hold", InputType.HOLD);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

}