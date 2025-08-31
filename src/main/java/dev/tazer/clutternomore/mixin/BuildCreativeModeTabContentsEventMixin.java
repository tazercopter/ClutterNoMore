package dev.tazer.clutternomore.mixin;

import dev.tazer.clutternomore.registry.CDataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public abstract class BuildCreativeModeTabContentsEventMixin {

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (newEntry.has(CDataComponents.BLOCK)) ci.cancel();
    }

    @Inject(method = "insertAfter", at = @At("HEAD"), cancellable = true)
    private void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (newEntry.has(CDataComponents.BLOCK)) ci.cancel();
        if (existingEntry.has(CDataComponents.BLOCK)) existingEntry = existingEntry.get(CDataComponents.BLOCK).getDefaultInstance();
    }

    @Inject(method = "insertBefore", at = @At("HEAD"), cancellable = true)
    private void insertBefore(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (newEntry.has(CDataComponents.BLOCK)) ci.cancel();
        if (existingEntry.has(CDataComponents.BLOCK)) existingEntry = existingEntry.get(CDataComponents.BLOCK).getDefaultInstance();
    }
}
