package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.CHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public abstract class BuildCreativeModeTabContentsEventMixin {

    @Shadow
    public abstract void insertBefore(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);

    @Shadow
    public abstract void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
    }

    @Inject(method = "insertAfter", at = @At("HEAD"), cancellable = true)
    private void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
        if (INVERSE_SHAPES_DATAMAP.containsKey(existingEntry.getItem())) {
            insertAfter(INVERSE_SHAPES_DATAMAP.get(existingEntry.getItem()).getDefaultInstance(), newEntry, visibility);
            ci.cancel();
        }
    }

    @Inject(method = "insertBefore", at = @At("HEAD"), cancellable = true)
    private void insertBefore(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
        if (INVERSE_SHAPES_DATAMAP.containsKey(existingEntry.getItem())) {
            insertBefore(INVERSE_SHAPES_DATAMAP.get(existingEntry.getItem()).getDefaultInstance(), newEntry, visibility);
            ci.cancel();
        }
    }
}
