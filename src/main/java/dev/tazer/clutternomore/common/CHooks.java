package dev.tazer.clutternomore.common;

import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
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

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;

public class CHooks {
    public static List<ItemStack> getDrops(List<ItemStack> old, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        if (state.getOptionalValue(SlabBlock.TYPE).isPresent() && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE && INVERSE_SHAPES_DATAMAP.containsKey(state.getBlock().asItem())) {
            LootParams.Builder lootparams$builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            BlockState newState = state.setValue(SlabBlock.TYPE, SlabType.BOTTOM);
            return newState.getDrops(lootparams$builder);
        }

        return old;
    }

    public static boolean acceptItem(Item item) {
        BlockSetRegistry.ShapeSet shapeSet = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
        if (shapeSet != null && shapeSet.mainChild().asItem() != item) return false;
        return !INVERSE_SHAPES_DATAMAP.containsKey(item);
    }
}
