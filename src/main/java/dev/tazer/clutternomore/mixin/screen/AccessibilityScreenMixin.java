package dev.tazer.clutternomore.mixin.screen;

import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityScreenMixin extends OptionsSubScreenMixin {
    @Inject(method = "addOptions", at = @At(value = "RETURN"))
    public void addOptions(CallbackInfo ci) {
        Button shapeSwitcherButton = Button
                .builder(
                        Component.translatable("key.clutternomore.shape_switcher"),
                        button -> minecraft.setScreen(new ShapeSwitcherOptionsScreen(((AccessibilityOptionsScreen) (Object) this), options))
                ).bounds(0, 0, 150, 20).build();
        list.addSmall(List.of(shapeSwitcherButton));
    }
}
