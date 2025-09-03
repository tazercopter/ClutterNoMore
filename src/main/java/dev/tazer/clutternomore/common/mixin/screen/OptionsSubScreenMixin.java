package dev.tazer.clutternomore.common.mixin.screen;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(OptionsSubScreen.class)
public abstract class OptionsSubScreenMixin extends ScreenMixin {
    @Shadow
    @Nullable
    protected OptionsList list;
    @Shadow
    @Final
    protected Options options;
}
