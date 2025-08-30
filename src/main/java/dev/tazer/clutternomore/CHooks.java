package dev.tazer.clutternomore;

import dev.tazer.clutternomore.registry.CDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class CHooks {
    public static boolean isSameItemSameComponents(ItemStack stack, ItemStack other) {
        List<Item> shapes = stack.getOrDefault(CDataComponents.SHAPES, List.of());
        if (shapes.contains(other.getItem())) return true;

        if (stack.has(CDataComponents.BLOCK)) {
            Item originalItem = Objects.requireNonNull(stack.get(CDataComponents.BLOCK));
            if (other.is(originalItem)) return true;

            if (other.has(CDataComponents.BLOCK)) {
                Item otherOriginalItem = Objects.requireNonNull(other.get(CDataComponents.BLOCK));
                if (otherOriginalItem == originalItem) return true;
            }
        }

        return ItemStack.isSameItemSameComponents(stack, other);
    }

    public static List<ItemStack> getDrops(List<ItemStack> old, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        if (state.getOptionalValue(SlabBlock.TYPE).isPresent() && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE && state.getBlock().asItem().getDefaultInstance().has(CDataComponents.BLOCK)) {
            LootParams.Builder lootparams$builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            BlockState newState = state.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
            return newState.getDrops(lootparams$builder);
        }

        return old;
    }
}
