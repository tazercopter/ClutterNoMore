package dev.tazer.clutternomore.common.registry;

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
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

public class BlockSetRegistry {

    public static void init() {
        BlockSetAPI.registerBlockSetDefinition(new ShapeSetRegistry());
        BlockSetAPI.addDynamicBlockRegistration(BlockSetRegistry::registerShapeBlocks, ShapeSet.class);
        BlockSetAPI.addDynamicItemRegistration(BlockSetRegistry::registerShapeItems, ShapeSet.class);
    }

    public static class ShapeSetRegistry extends BlockTypeRegistry<ShapeSet> {
        protected ShapeSetRegistry() {
            super(ShapeSet.class, "shape_set");
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
                        if (!defHasBlock(blockId, "", "planks") && (hasBlock(blockId, prefix, postfix) || (blockId.getPath().endsWith("_log") && hasWood(blockId)))) {
                            return Optional.of(new ShapeSet(blockId, block));
                        }
                    }
                }
            }

            return Optional.empty();
        }

        public static boolean hasWood(ResourceLocation key) {
            return BuiltInRegistries.ITEM.containsKey(key.withPath(path -> path.substring(0, path.length() - 3) + "wood"));
        }

        public static boolean defHasBlock(ResourceLocation key, String prefix, String postfix) {
            String newPrefix = prefix + (prefix.isEmpty() ? "" : "_");
            String newPostfix = (postfix.isEmpty() ? "" : "_") + postfix;
            boolean hasBlock = BuiltInRegistries.BLOCK.containsKey(key.withPrefix(newPrefix).withSuffix(newPostfix));
            if (!hasBlock) {
                if (key.getPath().endsWith("_block")) {
                    return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> newPrefix + path.substring(0, path.length() - 6) + newPostfix));
                }
            }
            return hasBlock;
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
            String name = prefix + (prefix.isEmpty() ? "" : "_") + getTypeName();

            if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
            if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
            if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
            return name + (postfix.isEmpty() ? "" : "_") + postfix;
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

        protected @Nullable Item getWood() {
            return id.getPath().endsWith("_log") ? BuiltInRegistries.ITEM.getOptional(id.withPath(path -> path.substring(0, path.length() - 3) + "wood")).orElse(null) : null;
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
            Item wood = getWood();
            if (wood != null) addChild("wood", wood);
        }

        @Override
        public String getTranslationKey() {
            return "shape_set." + this.getNamespace() + "." + this.getTypeName();
        }

        @Override
        public Block mainChild() {
            return block;
        }
    }

    private static void registerShapeBlocks(Registrator<Block> event, Collection<ShapeSet> shapeSets) {
        for (ShapeSet set : shapeSets) {
            if (CNMConfig.VERTICAL_SLABS.get() && set.hasChild("slab")) {
                ResourceLocation id = ClutterNoMore.location(set.getVariantId("vertical", "slab"));
                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                    Block block = new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(Block.byItem((Item) set.getChild("slab"))));
                    event.register(id, block);
                    set.addChild("vertical_slab_block", block);
                }
            }

            if (CNMConfig.STEPS.get() && set.hasChild("stairs")) {
                ResourceLocation id = ClutterNoMore.location(set.getVariantId("", "step"));
                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                    Block block = new StepBlock(BlockBehaviour.Properties.ofFullCopy(Block.byItem((Item) set.getChild("stairs"))));
                    event.register(id, block);
                    set.addChild("step_block", block);
                }
            }
        }
    }

    private static void registerShapeItems(Registrator<Item> event, Collection<ShapeSet> shapeSets) {
        for (ShapeSet type : shapeSets) {
            Block block;

            block = (Block) type.getChild("vertical_slab_block");
            if (block != null) {
                ResourceLocation id = ClutterNoMore.location(type.getVariantId("vertical", "slab"));
                if (!BuiltInRegistries.ITEM.containsKey(id)) {
                    Item item = new BlockItem(block, new Item.Properties());
                    event.register(id, item);
                    type.addChild("vertical_slab", item);
                }
            }

            block = (Block) type.getChild("step_block");
            if (block != null) {
                ResourceLocation id = ClutterNoMore.location(type.getVariantId("", "step"));
                if (!BuiltInRegistries.ITEM.containsKey(id)) {
                    Item item = new BlockItem(block, new Item.Properties());
                    event.register(id, item);
                    type.addChild("step", item);
                }
            }
        }
    }
}
