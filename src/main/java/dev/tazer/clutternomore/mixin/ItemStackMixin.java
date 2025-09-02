package dev.tazer.clutternomore.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static dev.tazer.clutternomore.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.event.DatamapHandler.SHAPES_DATAMAP;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void isSameItemSameComponents(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (SHAPES_DATAMAP.containsKey(other.getItem())) {
            if (SHAPES_DATAMAP.get(other.getItem()).contains(stack.getItem())) cir.setReturnValue(true);
            return;
        }

        if (INVERSE_SHAPES_DATAMAP.containsKey(stack.getItem())) {
            Item originalItem = INVERSE_SHAPES_DATAMAP.get(stack.getItem());
            if (other.is(originalItem)) {
                cir.setReturnValue(true);
                return;
            }

            if (INVERSE_SHAPES_DATAMAP.containsKey(other.getItem())) {
                Item otherOriginalItem = INVERSE_SHAPES_DATAMAP.get(other.getItem());
                if (otherOriginalItem == originalItem) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }

        if (!stack.is(other.getItem())) {
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(stack.isEmpty() && other.isEmpty() || Objects.equals(stack.getComponents(), other.getComponents()));
        }
    }
}
