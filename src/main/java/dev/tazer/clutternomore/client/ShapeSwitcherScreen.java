package dev.tazer.clutternomore.client;

import dev.tazer.clutternomore.CNMConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ShapeSwitcherScreen extends OptionsSubScreen {
    public static final Component TITLE = Component.translatable("options.clutternomore.shape_switcher.title");

    public ShapeSwitcherScreen(Screen lastScreen, Options options) {
        super(lastScreen, options, TITLE);
    }

    @Override
    protected void addOptions() {
        OptionInstance<?> moving = new OptionInstance<>(
                "key.clutternomore.menu_type",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.menu_scrolling") : Component.translatable("key.clutternomore.menu_static"),
                OptionInstance.BOOLEAN_VALUES,
                true,
                value -> {
                    CNMConfig.SCROLLING.set(value);
                    CNMConfig.SCROLLING.save();
                });

        OptionInstance<?> toggleButton = new OptionInstance<>(
                "key.clutternomore.open_menu",
                OptionInstance.noTooltip(),
                (component, value) -> value ? Component.translatable("key.clutternomore.menu_hold") : Component.translatable("key.clutternomore.menu_toggle"),
                OptionInstance.BOOLEAN_VALUES,
                true,
                value -> {
                    CNMConfig.HOLD.set(value);
                    CNMConfig.HOLD.save();
                });

        if (list != null) list.addSmall(moving, toggleButton);
    }
}
