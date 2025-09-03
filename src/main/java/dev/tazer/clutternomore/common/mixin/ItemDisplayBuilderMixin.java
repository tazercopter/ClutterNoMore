package dev.tazer.clutternomore.common.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;

@Mixin(CreativeModeTab.ItemDisplayBuilder.class)
public class ItemDisplayBuilderMixin {
    @Inject(method = "accept", at = @At(value = "HEAD"), cancellable = true)
    private void accept(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility, CallbackInfo ci) {
        if (INVERSE_SHAPES_DATAMAP.containsKey(stack.getItem())) ci.cancel();
    }
}
