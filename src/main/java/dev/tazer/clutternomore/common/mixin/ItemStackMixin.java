package dev.tazer.clutternomore.common.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.DatamapHandler.SHAPES_DATAMAP;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "isSameItemSameComponents", at = @At("RETURN"), cancellable = true)
    private static void isSameItemSameComponents(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            Item item = stack.getItem();
            Item otherItem = other.getItem();
            if (SHAPES_DATAMAP.containsKey(otherItem)) {
                if (SHAPES_DATAMAP.get(otherItem).contains(item)) cir.setReturnValue(true);
                return;
            }

            if (SHAPES_DATAMAP.containsKey(item)) {
                if (SHAPES_DATAMAP.get(item).contains(otherItem)) cir.setReturnValue(true);
                return;
            }

            Item originalItem = INVERSE_SHAPES_DATAMAP.get(item);
            if (originalItem != null) {
                if (other.is(originalItem)) {
                    cir.setReturnValue(true);
                    return;
                }

                Item otherOriginalItem = INVERSE_SHAPES_DATAMAP.get(otherItem);
                if (otherOriginalItem.equals(originalItem)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
