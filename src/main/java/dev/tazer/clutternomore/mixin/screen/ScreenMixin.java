package dev.tazer.clutternomore.mixin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow
    @Nullable
    protected Minecraft minecraft;
}
