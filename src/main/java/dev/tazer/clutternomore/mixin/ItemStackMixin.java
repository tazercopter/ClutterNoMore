package dev.tazer.clutternomore.mixin;

import dev.tazer.clutternomore.registry.CDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void isSameItemSameComponents(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        List<Item> shapes = stack.getOrDefault(CDataComponents.SHAPES, List.of());
        if (shapes.contains(other.getItem())) {
            cir.setReturnValue(true);
            return;
        }

        if (stack.has(CDataComponents.BLOCK)) {
            Item originalItem = Objects.requireNonNull(stack.get(CDataComponents.BLOCK));
            if (other.is(originalItem)) {
                cir.setReturnValue(true);
                return;
            }

            if (other.has(CDataComponents.BLOCK)) {
                Item otherOriginalItem = Objects.requireNonNull(other.get(CDataComponents.BLOCK));
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
