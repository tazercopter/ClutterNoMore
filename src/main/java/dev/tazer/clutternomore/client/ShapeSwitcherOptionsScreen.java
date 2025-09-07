package dev.tazer.clutternomore.client;

import com.google.common.collect.ImmutableList;
import dev.tazer.clutternomore.CNMConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ShapeSwitcherOptionsScreen extends OptionsSubScreen {
    public static final Component TITLE = Component.translatable("options.clutternomore.shape_switcher.title");
    public static final OptionInstance.Enum<CNMConfig.InputType> INPUT_TYPE_VALUES = new OptionInstance.Enum<>(
            ImmutableList.of(
                    CNMConfig.InputType.HOLD,
                    CNMConfig.InputType.TOGGLE,
                    CNMConfig.InputType.PRESS
            ),
            CNMConfig.InputType.CODEC
    );

    public ShapeSwitcherOptionsScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TITLE);
    }

    @Override
    protected void addOptions() {
        OptionInstance<?> moving = new OptionInstance<>(
                "key.clutternomore.menu_type",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.menu_scrolling") : Component.translatable("key.clutternomore.menu_static"),
                OptionInstance.BOOLEAN_VALUES,
                CNMConfig.SCROLLING.get(),
                value -> {
                    CNMConfig.SCROLLING.set(value);
                    CNMConfig.SCROLLING.save();
                });

        OptionInstance<?> toggleButton = new OptionInstance<>(
                "key.clutternomore.open_menu",
                OptionInstance.noTooltip(),
                (component, value) -> Component.translatable("key.clutternomore.menu_" + value),
                INPUT_TYPE_VALUES,
                CNMConfig.HOLD.get(),
                value -> {
                    CNMConfig.HOLD.set(value);
                    CNMConfig.HOLD.save();
                });

        if (list != null) list.addSmall(moving, toggleButton);
    }
}
