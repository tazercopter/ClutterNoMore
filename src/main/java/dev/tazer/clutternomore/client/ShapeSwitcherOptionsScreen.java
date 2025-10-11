package dev.tazer.clutternomore.client;

import com.google.common.collect.ImmutableList;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
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
                ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING.value(),
                value -> {
                    ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING.setValue(value);
                });

        OptionInstance<?> toggleButton = new OptionInstance<>(
                "key.clutternomore.open_menu",
                OptionInstance.noTooltip(),
                (component, value) -> Component.translatable("key.clutternomore.menu_" + value),
                INPUT_TYPE_VALUES,
                ClutterNoMoreClient.CLIENT_CONFIG.HOLD.value(),
                value -> {
                    ClutterNoMoreClient.CLIENT_CONFIG.HOLD.setValue(value);
                });

        if (list != null) list.addSmall(moving, toggleButton);
    }
}
