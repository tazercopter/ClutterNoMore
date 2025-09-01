package dev.tazer.clutternomore.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.event.CommonEvents;
import dev.tazer.clutternomore.registry.CDataComponents;
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

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void place(ItemStack instance, int amount, LivingEntity entity, @Local(argsOnly = true) BlockPlaceContext context, @Local(ordinal = 0) BlockState blockstate) {
        if (blockstate.getOptionalValue(SlabBlock.TYPE).isEmpty() ||  blockstate.getValue(SlabBlock.TYPE) != SlabType.DOUBLE || !CommonEvents.INVERSE_SHAPES_DATAMAP.containsKey(context.getItemInHand().getItem())) {
            instance.consume(amount, entity);
        }
    }
}
