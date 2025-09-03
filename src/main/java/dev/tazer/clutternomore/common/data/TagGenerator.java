package dev.tazer.clutternomore.common.data;

import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagGenerator implements DataGenerator {
    public SimpleTagBuilder verticalSlabItems;
    public SimpleTagBuilder stepItems;
    public SimpleTagBuilder verticalSlabBlocks;
    public SimpleTagBuilder stepBlocks;
    public SimpleTagBuilder woodenVerticalSlabs;
    public SimpleTagBuilder woodenSteps;
    public SimpleTagBuilder pickaxeMineable;
    public SimpleTagBuilder axeMineable;

    @Override
    public void initialize(ResourceManager manager, ResourceSink sink) {
        DataGenerator.super.initialize(manager, sink);
        verticalSlabItems = SimpleTagBuilder.of(ClutterNoMore.location("vertical_slabs"));
        stepItems = SimpleTagBuilder.of(ClutterNoMore.location("steps"));
        verticalSlabBlocks = SimpleTagBuilder.of(ClutterNoMore.location("vertical_slabs"));
        stepBlocks = SimpleTagBuilder.of(ClutterNoMore.location("steps"));
        woodenVerticalSlabs = SimpleTagBuilder.of(ClutterNoMore.location(("wooden_vertical_slabs")));
        woodenSteps = SimpleTagBuilder.of(ClutterNoMore.location("wooden_steps"));
        pickaxeMineable = SimpleTagBuilder.of(BlockTags.MINEABLE_WITH_PICKAXE);
        axeMineable = SimpleTagBuilder.of(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public void generate(Item item, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
        if (set == null || item != set.mainChild().asItem()) return;

        if (set.hasChild("vertical_slab")) {
            verticalSlabItems.addEntry(set.getChild("vertical_slab"));
        }

        if (set.hasChild("step")) {
            stepItems.addEntry(set.getChild("step"));
        }

        if (set.hasChild("vertical_slab_block")) {
            Block block = (Block) set.getChild("vertical_slab_block");
            verticalSlabBlocks.addEntry(block);

            BlockState state = Block.byItem((Item) set.getChild("slab")).defaultBlockState();
            if (state.is(BlockTags.WOODEN_SLABS)) {
                woodenVerticalSlabs.addEntry(block);
            }

            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                pickaxeMineable.addEntry(block);
            }

            if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
                axeMineable.addEntry(block);
            }
        }

        if (set.hasChild("step_block")) {
            Block block = (Block) set.getChild("step_block");
            stepBlocks.addEntry(block);

            BlockState state = Block.byItem((Item) set.getChild("stairs")).defaultBlockState();
            if (state.is(BlockTags.WOODEN_STAIRS)) {
                woodenSteps.addEntry(block);
            }

            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                pickaxeMineable.addEntry(block);
            }

            if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
                axeMineable.addEntry(block);
            }
        }
    }

    @Override
    public void finish(ResourceManager manager, ResourceSink sink) {
        sink.addTag(verticalSlabItems, Registries.ITEM);
        sink.addTag(stepItems, Registries.ITEM);

        sink.addTag(verticalSlabBlocks, Registries.BLOCK);
        sink.addTag(stepBlocks, Registries.BLOCK);

        sink.addTag(woodenVerticalSlabs, Registries.BLOCK);
        sink.addTag(woodenSteps, Registries.BLOCK);

        sink.addTag(pickaxeMineable, Registries.BLOCK);
        sink.addTag(axeMineable, Registries.BLOCK);

        StaticResource tag = StaticResource.getOrThrow(manager, ResourceLocation.withDefaultNamespace("tags/block/slabs.json"));
        SimpleTagBuilder builder = SimpleTagBuilder.of(BlockTags.SLABS);
        builder.addFromJson(tag.toJson());
        builder.addTag(verticalSlabBlocks);
        sink.addTag(builder, Registries.BLOCK);

        builder = SimpleTagBuilder.of(ItemTags.SLABS);
        builder.addTag(verticalSlabItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.STAIRS);
        builder.addTag(stepBlocks);
        sink.addTag(builder, Registries.BLOCK);

        builder = SimpleTagBuilder.of(ItemTags.STAIRS);
        builder.addTag(stepItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.WOODEN_SLABS);
        builder.addTag(woodenVerticalSlabs);
        sink.addTag(builder, Registries.BLOCK);

        builder = SimpleTagBuilder.of(BlockTags.WOODEN_STAIRS);
        builder.addTag(woodenSteps);
        sink.addTag(builder, Registries.BLOCK);
    }
}
