package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.CHooks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? neoforge {
/*@Mixin(BuildCreativeModeTabContentsEvent.class)
*///?} else {
@Mixin(FabricItemGroupEntries.class)
//?}
public abstract class BuildCreativeModeTabContentsEventMixin {

    //? neoforge {
    /*@Shadow
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
    *///?} else {
    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
    }
    //?}
}
