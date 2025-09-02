package dev.tazer.clutternomore.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
                List<String> prefixes = List.of(
                        "",
                        "spiked"
                );

                List<String> postfixes = List.of(
                        "",
                        "stairs",
                        "slab",
                        "wall"
                );

                for (String prefix : prefixes) {
                    for (String postfix : postfixes) {
                        if (hasBlock(blockId, prefix, postfix)) {
                            return Optional.of(new ShapeSet(blockId, block));
                        }
                    }
                }
            }

            return Optional.empty();
        }

        public static boolean hasBlock(ResourceLocation key, String postfix) {
            return hasBlock(key, "", postfix);
        }

        public static boolean hasBlock(ResourceLocation key, String prefix, String postfix) {
            String newPrefix = prefix + (prefix.isEmpty() ? "" : "_");
            String newPostfix = (postfix.isEmpty() ? "" : "_") + postfix;
            boolean optional = BuiltInRegistries.BLOCK.containsKey(key.withPrefix(newPrefix).withSuffix(newPostfix));
            if (!optional) {
                if (key.getPath().endsWith("stripped_")) {
                    return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> "stripped_" + newPrefix + path.substring(9) + newPostfix));
                }

                if (key.getPath().endsWith("_block")) {
                    return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> newPrefix + path.substring(0, path.length() - 6) + newPostfix));
                } else if (key.getPath().endsWith("_planks")) {
                    return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> newPrefix + path.substring(0, path.length() - 7) + newPostfix));
                } else if (key.getPath().endsWith("s")) {
                    return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> newPrefix + path.substring(0, path.length() - 1) + newPostfix));
                }
            }

            return optional;
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

        protected @Nullable Item findRelatedEntry(String postfix) {
            return findRelatedEntry("", postfix);
        }

        protected @Nullable Item findRelatedEntry(String prefix, String postfix) {
            String newPrefix = prefix + (prefix.isEmpty() ? "" : "_");
            String newPostfix = (postfix.isEmpty() ? "" : "_") + postfix;
            Optional<Item> optional = BuiltInRegistries.ITEM.getOptional(id.withPrefix(newPrefix).withSuffix(newPostfix));
            if (optional.isEmpty()) {
                if (id.getPath().startsWith("stripped_")) {
                    return BuiltInRegistries.ITEM.getOptional(id.withPath(path -> "stripped_" + newPrefix + path.substring(9) + newPostfix)).orElse(null);
                }

                if (id.getPath().endsWith("_block")) {
                    return BuiltInRegistries.ITEM.getOptional(id.withPath(path -> newPrefix + path.substring(0, path.length() - 6) + newPostfix)).orElse(null);
                } else if (id.getPath().endsWith("_planks")) {
                    return BuiltInRegistries.ITEM.getOptional(id.withPath(path -> newPrefix + path.substring(0, path.length() - 7) + newPostfix)).orElse(null);
                } else if (id.getPath().endsWith("s")) {
                    return BuiltInRegistries.ITEM.getOptional(id.withPath(path -> newPrefix + path.substring(0, path.length() - 1) + newPostfix)).orElse(null);
                }
            }

            return optional.orElse(null);
        }

        @Override
        protected void initializeChildrenItems() {
            Item stairs = findRelatedEntry("stairs");
            if (stairs != null) addChild("stairs", stairs);
            Item slab = findRelatedEntry("slab");
            if (slab != null) addChild("slab", slab);
            Item wall = findRelatedEntry("wall");
            if (wall != null) addChild("wall", wall);
            Item spiked = findRelatedEntry("spiked", "");
            if (spiked != null) addChild("spiked", spiked);
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
