package dev.tazer.clutternomore.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

import static dev.tazer.clutternomore.common.event.ShapeMapHandler.INVERSE_SHAPES_DATAMAP;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void place(ItemStack instance, int amount, LivingEntity entity, @Local(argsOnly = true) BlockPlaceContext context, @Local(ordinal = 0) BlockState blockstate) {
        boolean consume = true;
        if (INVERSE_SHAPES_DATAMAP.containsKey(context.getItemInHand().getItem())) {
            Optional<SlabType> slabType = blockstate.getOptionalValue(SlabBlock.TYPE);
            Optional<Boolean> isDouble = blockstate.getOptionalValue(VerticalSlabBlock.DOUBLE);

            if (slabType.isPresent()) consume = slabType.get() != SlabType.DOUBLE;
            if (isDouble.isPresent()) consume = !isDouble.get();
        }

        if (consume) instance.consume(amount, entity);
    }
}
