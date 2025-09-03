package dev.tazer.clutternomore.common.data;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public class TagGenerator implements DataGenerator {
    public SimpleTagBuilder verticalSlabItems;
    public SimpleTagBuilder stepItems;
    public SimpleTagBuilder verticalSlabBlocks;
    public SimpleTagBuilder stepBlocks;
    public SimpleTagBuilder woodenVerticalSlabItems;
    public SimpleTagBuilder woodenStepItems;
    public SimpleTagBuilder woodenVerticalSlabBlocks;
    public SimpleTagBuilder woodenStepBlocks;
    public SimpleTagBuilder pickaxeMineable;

    @Override
    public void initialize(ResourceManager manager, ResourceSink sink) {
        verticalSlabItems = SimpleTagBuilder.of(ClutterNoMore.location("vertical_slabs"));
        stepItems = SimpleTagBuilder.of(ClutterNoMore.location("steps"));
        verticalSlabBlocks = SimpleTagBuilder.of(ClutterNoMore.location("vertical_slabs"));
        stepBlocks = SimpleTagBuilder.of(ClutterNoMore.location("steps"));
        woodenVerticalSlabItems = SimpleTagBuilder.of(ClutterNoMore.location(("wooden_vertical_slabs")));
        woodenStepItems = SimpleTagBuilder.of(ClutterNoMore.location("wooden_steps"));
        woodenVerticalSlabBlocks = SimpleTagBuilder.of(ClutterNoMore.location(("wooden_vertical_slabs")));
        woodenStepBlocks = SimpleTagBuilder.of(ClutterNoMore.location("wooden_steps"));
        pickaxeMineable = SimpleTagBuilder.of(ClutterNoMore.location("mineable/pickaxe"));
    }

    @Override
    public void generate(Item item, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
        if (set == null || item != set.mainChild().asItem()) return;

        if (set.hasChild("vertical_slab")) {
            Item verticalSlab = (Item) set.getChild("vertical_slab");
            verticalSlabItems.addEntry(verticalSlab);

            ItemStack stack = ((Item) set.getChild("slab")).getDefaultInstance();
            if (stack.is(ItemTags.WOODEN_SLABS)) woodenVerticalSlabItems.addEntry(verticalSlab);
            else pickaxeMineable.addEntry(verticalSlab);
        }

        if (set.hasChild("step")) {
            Item step = (Item) set.getChild("step");
            stepItems.addEntry(step);

            ItemStack stack = ((Item) set.getChild("stairs")).getDefaultInstance();
            if (stack.is(ItemTags.WOODEN_STAIRS)) woodenStepItems.addEntry(step);
            else pickaxeMineable.addEntry(step);
        }
    }

    @Override
    public void generate(Block block, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = null;
        if (block.asItem() != Items.AIR) set = BlockSetAPI.getBlockTypeOf(block, BlockSetRegistry.ShapeSet.class);
        if (set == null || block != set.mainChild()) return;

        if (set.hasChild("vertical_slab_block")) {
            Block verticalSlab = (Block) set.getChild("vertical_slab_block");
            verticalSlabBlocks.addEntry(verticalSlab);

            BlockState state = Block.byItem((Item) set.getChild("slab")).defaultBlockState();
            if (state.is(BlockTags.WOODEN_SLABS)) {
                woodenVerticalSlabBlocks.addEntry(verticalSlab);
            }

            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                pickaxeMineable.addEntry(verticalSlab);
            }
        }

        if (set.hasChild("step_block")) {
            Block step = (Block) set.getChild("step_block");
            stepBlocks.addEntry(step);

            BlockState state = Block.byItem((Item) set.getChild("stairs")).defaultBlockState();
            if (state.is(BlockTags.WOODEN_STAIRS)) {
                woodenStepBlocks.addEntry(step);
            }

            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                pickaxeMineable.addEntry(step);
            }
        }
    }

    @Override
    public void finish(ResourceManager manager, ResourceSink sink) {
        sink.addTag(verticalSlabItems, Registries.ITEM);
        sink.addTag(verticalSlabBlocks, Registries.BLOCK);

        sink.addTag(woodenVerticalSlabItems, Registries.ITEM);
        sink.addTag(woodenVerticalSlabBlocks, Registries.BLOCK);

        SimpleTagBuilder builder = SimpleTagBuilder.of(BlockTags.SLABS);
        builder.addTag(verticalSlabBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.SLABS);
        builder.addTag(verticalSlabItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.WOODEN_SLABS);
        builder.addTag(woodenVerticalSlabBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.WOODEN_SLABS);
        builder.addTag(woodenVerticalSlabItems);
        sink.addTag(builder, Registries.ITEM);

        sink.addTag(stepItems, Registries.ITEM);
        sink.addTag(stepBlocks, Registries.BLOCK);

        sink.addTag(woodenStepItems, Registries.ITEM);
        sink.addTag(woodenStepBlocks, Registries.BLOCK);

        builder = SimpleTagBuilder.of(BlockTags.STAIRS);
        builder.addTag(stepBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.STAIRS);
        builder.addTag(stepItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.WOODEN_STAIRS);
        builder.addTag(woodenStepBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.WOODEN_STAIRS);
        builder.addTag(woodenStepItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.MINEABLE_WITH_PICKAXE);
        builder.merge(pickaxeMineable);
        sink.addTag(builder, Registries.BLOCK);
    }
}
