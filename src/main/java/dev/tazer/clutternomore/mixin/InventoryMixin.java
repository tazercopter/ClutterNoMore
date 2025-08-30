package dev.tazer.clutternomore.mixin;

import dev.tazer.clutternomore.CHooks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Redirect(method = "hasRemainingSpaceForItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean hasRemainingSpaceForItem(ItemStack stack, ItemStack other) {
        return CHooks.isSameItemSameComponents(stack, other);
    }
}
