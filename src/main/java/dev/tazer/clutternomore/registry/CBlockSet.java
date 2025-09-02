package dev.tazer.clutternomore.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.event.CommonEvents;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class CBlockSet {

    public static void init() {
        BlockSetAPI.registerBlockSetDefinition(new ShapeSetRegistry());
        BlockSetAPI.addDynamicBlockRegistration(CBlockSet::registerShapeBlocks, ShapeSet.class);
        BlockSetAPI.addDynamicItemRegistration(CBlockSet::registerShapeItems, ShapeSet.class);
    }

    public static class ShapeSetRegistry extends BlockTypeRegistry<ShapeSet> {
        protected ShapeSetRegistry() {
            super(ShapeSet.class, "slab_type");
        }

        @Override
        public Optional<ShapeSet> detectTypeFromBlock(Block block, ResourceLocation blockId) {
            if (block.asItem() != Items.AIR) {
                List<String> postfixes = List.of(
                        "stairs",
                        "slab",
                        "wall"
                );

                for (String postfix : postfixes) {
                    if (CommonEvents.hasBlock(blockId, postfix)) {
                        return Optional.of(new ShapeSet(blockId, block));
                    }
                }
            }

            return Optional.empty();
        }

        @Override
        public ShapeSet getDefaultType() {
            return STONE;
        }

        public static final ShapeSet STONE = new ShapeSet(ResourceLocation.withDefaultNamespace("stone"), Blocks.STONE);
    }

    public static class ShapeSet extends BlockType {
        private final ResourceLocation id;
        private final Block block;

        ShapeSet(ResourceLocation id, Block block) {
            super(id);
            this.id = id;
            this.block = block;
        }

        public String getVariantId(String prefix, String postfix) {
            String name = prefix + "_" + getTypeName();

            if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
            if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
            if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
            return name + "_" + postfix;
        }

        @Override
        protected void initializeChildrenBlocks() {
            addChild("block", block);
        }

        protected @Nullable Item findRelatedEntry(String suffix) {
            Item item = CommonEvents.getOptional(id, suffix).orElse(null);
            return item == Items.AIR ? null : item;
        }

        @Override
        protected void initializeChildrenItems() {
            Item stairs = findRelatedEntry("stairs");
            if (stairs != null) addChild("stairs", stairs);
            Item slab = findRelatedEntry("slab");
            if (slab != null) addChild("slab", slab);
            Item wall = findRelatedEntry("wall");
            if (wall != null) addChild("wall", wall);
        }

        @Override
        public String getTranslationKey() {
            return "slab_type." + this.getNamespace() + "." + this.getTypeName();
        }

        @Override
        public Block mainChild() {
            return block;
        }
    }

    private static void registerShapeBlocks(Registrator<Block> event, Collection<ShapeSet> shapeSets) {
        for (ShapeSet type : shapeSets) {
            if (type.hasChild("slab")) {
                ResourceLocation id = ClutterNoMore.location(type.getVariantId("vertical", "slab"));
                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                    Block block = new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(type.mainChild()));
                    event.register(id, block);
                    type.addChild("vertical_slab_block", block);
                }
            }
        }
    }

    private static void registerShapeItems(Registrator<Item> event, Collection<ShapeSet> shapeSets) {
        for (ShapeSet type : shapeSets) {
            Block block = (Block) type.getChild("vertical_slab_block");
            if (block != null) {
                ResourceLocation id = ClutterNoMore.location(type.getVariantId("vertical", "slab"));
                if (!BuiltInRegistries.ITEM.containsKey(id)) {
                    Item item = new BlockItem(block, new Item.Properties());
                    event.register(id, item);
                    type.addChild("vertical_slab", item);
                }
            }
        }
    }
}
