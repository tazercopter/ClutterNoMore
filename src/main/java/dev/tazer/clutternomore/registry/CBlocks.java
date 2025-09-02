package dev.tazer.clutternomore.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ClutterNoMore.MODID);

//    public static final DeferredBlock<VerticalSlabBlock> STONE_TILE_VERTICAL_SLAB = register(
//            "stone_tile_vertical_slab",
//            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB))
//    );
//
//    public static final DeferredBlock<VerticalSlabBlock> ACACIA_VERTICAL_SLAB = register(
//            "acacia_vertical_slab",
//            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_SLAB))
//    );

    public static <T extends Block> DeferredBlock<T> register(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void registerBlockItem(String name, DeferredBlock<? extends Block> block) {
        CItems.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
