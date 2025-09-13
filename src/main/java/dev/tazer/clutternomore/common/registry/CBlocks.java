package dev.tazer.clutternomore.common.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class CBlocks {
//    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ClutterNoMore.MODID);

//    public static final Supplier<VerticalSlabBlock> STONE_TILE_VERTICAL_SLAB = register(
//            "stone_tile_vertical_slab",
//            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB))
//    );
//
//    public static final Supplier<VerticalSlabBlock> ACACIA_VERTICAL_SLAB = register(
//            "acacia_vertical_slab",
//            () -> new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_SLAB))
//    );

    public static <T extends Block> Supplier<T> register(String name, Supplier<T> block) {
        Supplier<T> toReturn = CommonRegistry.registerBlock(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> block) {
        return CommonRegistry.registerBlock(name, block);
    }

    public static void registerBlockItem(String name, Supplier<? extends Block> block) {
        CItems.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register() {

    }
}