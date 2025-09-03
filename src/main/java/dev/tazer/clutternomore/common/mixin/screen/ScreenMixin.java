package dev.tazer.clutternomore.common.mixin.screen;

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
