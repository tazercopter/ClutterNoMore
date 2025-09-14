package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.tazer.clutternomore.common.event.ShapeMapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.ShapeMapHandler.SHAPES_DATAMAP;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        Item item = stack.getItem();

        boolean hasShapes = SHAPES_DATAMAP.containsKey(item);
        boolean isShape = INVERSE_SHAPES_DATAMAP.containsKey(item);

        if (hasShapes || isShape) {
            Item originalItem = INVERSE_SHAPES_DATAMAP.getOrDefault(item, item);
            List<Item> shapes = new ArrayList<>(SHAPES_DATAMAP.get(originalItem));
            shapes.addFirst(originalItem);
            cir.setReturnValue(Optional.of(new ShapeTooltip(shapes, shapes.indexOf(item))));
        }
    }
}
