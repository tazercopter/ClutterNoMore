package dev.tazer.clutternomore.common.registry;

import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockSetRegistry {

    public static void init() {
//        BlockSetAPI.registerBlockSetDefinition(new ShapeSetRegistry());
//        BlockSetAPI.addDynamicBlockRegistration(BlockSetRegistry::registerShapeBlocks, ShapeSet.class);
//        BlockSetAPI.addDynamicItemRegistration(BlockSetRegistry::registerShapeItems, ShapeSet.class);

    }

    public static ShapeSet getBlockTypeOf(Item item, Class<ShapeSet> shapeSetClass) {
        return ShapeSetRegistry.items.get(item);
    }

    public static class ShapeSetRegistry {

        public static final Map<Item, ShapeSet> items = new LinkedHashMap<>();
        protected ShapeSetRegistry() {
        }

        public static void register(Item block, ShapeSet shapeSet) {
            items.put(block, shapeSet);
        }

        public static Optional<ShapeSet> detectTypeFromBlock(Block block, ResourceLocation blockId) {
            if (block.asItem() != Items.AIR) {
                if (isParentBlock(blockId)) {
                    return Optional.of(new ShapeSet(blockId, block));
                }
            }

            return Optional.empty();
        }

        private static boolean has(ResourceLocation block) {
            return BuiltInRegistries.BLOCK.containsKey(block);
        }

        private static boolean isParentBlock(ResourceLocation block) {
            List<String> namespaces = List.of(block.getNamespace(), "minecraft");
            String path = block.getPath();

            List<String> parentSuffixes = List.of("log", "planks", "block");
            List<String> ignoredSuffixes = List.of("block");
            Map<String, String> replacements = Map.of("log", "wood");
            List<String> prefixes = List.of("spiked");
            List<String> ignoredPrefixes = List.of("stripped");
            List<String> suffixes = List.of("stairs", "slab", "wall");

            for (String namespace : namespaces) {
                ResourceLocation base = ResourceLocation.fromNamespaceAndPath(namespace, path);

                String suffixBase = path;
                for (String ignored : ignoredSuffixes) {
                    suffixBase = stripSuffix(suffixBase, ignored);
                }

                for (String parent : parentSuffixes) {
                    String suffixed = suffixBase + "_" + parent;
                    if (!suffixed.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> suffixed);
                        if (has(candidate)) return false;
                    }
                }

                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    String replaced = path.replace(replacement.getKey(), replacement.getValue());
                    if (!replaced.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> replaced);
                        if (has(candidate)) return true;
                    }
                }

                String prefixBase = path;
                for (String ignored : ignoredPrefixes) {
                    prefixBase = stripPrefix(prefixBase, ignored);
                }

                for (String prefix : prefixes) {
                    String prefixed = prefix + "_" + prefixBase;
                    if (!prefixed.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> prefixed);
                        if (has(candidate)) return true;
                    }
                }

                for (String parent : parentSuffixes) {
                    suffixBase = stripSuffix(suffixBase, parent);
                }

                for (String suffix : suffixes) {
                    if (suffixBase.endsWith("s")) {
                        String trimmed  = suffixBase.substring(0, suffixBase.length() - 1);
                        String suffixed = trimmed + "_" + suffix;
                        if (!suffixed.equals(path)) {
                            ResourceLocation candidate = base.withPath(p -> suffixed);
                            if (has(candidate)) return true;
                        }
                    }

                    String suffixed = suffixBase + "_" + suffix;
                    if (!suffixed.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> suffixed);
                        if (has(candidate)) return true;
                    }
                }
            }

            return false;
        }

//        @Override
        public ShapeSet getDefaultType() {
            return STONE;
        }

        public static final ShapeSet STONE = new ShapeSet(ResourceLocation.withDefaultNamespace("stone"), Blocks.STONE);
    }

    public static class ShapeSet {
        private final ResourceLocation id;
        private final String type;
        private final Block block;
        private final Map<String, ItemLike> items = new LinkedHashMap<>();

        ShapeSet(ResourceLocation id, Block block) {
            this.id = id;
            this.block = block;
            this.type = id.getPath().replace("_block", "").replace("_planks", "");
            initializeChildrenBlocks();
            ShapeSetRegistry.register(block.asItem(), this);

        }

        protected void initializeChildrenBlocks() {
            addChild("block", block);
            if (id.getPath().contains("log")) {
                items.put("wood", getWood());
                if (id.getPath().contains("stripped")) {
                    items.put("hollow_log", BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("wilderwild", id.getPath().replace("stripped_", "stripped_hollowed_"))).orElse(null));
                } else {
                    items.put("hollow_log", BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("wilderwild", "hollowed_"+ id.getPath())).orElse(null));
                }
            }
            items.put("slab", findRelatedEntry("slab"));
            items.put("stairs", findRelatedEntry("stairs"));
            items.put("wall", findRelatedEntry("wall"));
        }

        private void addChild(String block, ItemLike block1) {
            items.put(block, block1);
        }

        private void addChild(ItemLike block1) {
            var b = switch (block) {
                case SlabBlock slabBlock -> "slab";
                case StairBlock stairBlock ->  "stairs";
                case WallBlock wallBlock -> "wall";
                case FenceBlock wallBlock -> "fence";
                case VerticalSlabBlock wallBlock -> "vertical_slab";
                case StepBlock wallBlock -> "step";
                case null, default -> "block";
            };
            items.put(b, block1);
        }

        public String getVariantId(String prefix, String postfix) {
            String name = prefix + (prefix.isEmpty() ? "" : "_") + getTypeName();

            List<String> suffixes = List.of("_block", "_planks", "s");

            for (String suffix : suffixes) {
                if (name.endsWith(suffix)) {
                    name = name.substring(0, name.length() - suffix.length());
                    break;
                }
            }

            return name + (postfix.isEmpty() ? "" : "_") + postfix;
        }

        private String getTypeName() {
            return type;
        }


        protected @Nullable Item findRelatedEntry(String prefix, String postfix) {
            String basePath = id.getPath();

            List<String> parentSuffixes = List.of("block", "planks");
            for (String parent : parentSuffixes) {
                basePath = stripSuffix(basePath, parent);
            }

            List<String> ignoredPrefixes = List.of("stripped");
            String reapplyPrefix = "";
            for (String ignored : ignoredPrefixes) {
                if (id.getPath().startsWith(ignored + "_")) {
                    reapplyPrefix = ignored + "_";
                    basePath = stripPrefix(basePath, ignored);
                }
            }

            String prefixPart = prefix.isEmpty() ? "" : prefix + "_";
            String postfixPart = postfix.isEmpty() ? "" : "_" + postfix;

            List<String> candidates = new ArrayList<>();
            if (basePath.endsWith("s")) {
                candidates.add(basePath.substring(0, basePath.length() - 1));
            }
            candidates.add(basePath);

            for (String stem : candidates) {
                String candidatePath = reapplyPrefix + prefixPart + stem + postfixPart;
                ResourceLocation candidateId = id.withPath(p -> candidatePath);
                Optional<Item> found = BuiltInRegistries.ITEM.getOptional(candidateId);
                if (found.isPresent()) return found.get();
            }

            return null;
        }

        protected @Nullable Item findRelatedEntry(String postfix) {
            return findRelatedEntry("", postfix);
        }

        protected @Nullable Item getWood() {
            String path = id.getPath();
            if (path.endsWith("log")) {
                String stem = path.substring(0, path.length() - 3);
                ResourceLocation woodId = id.withPath(p -> stem + "wood");
                return BuiltInRegistries.ITEM.getOptional(woodId).orElse(null);
            }

            return null;
        }

        protected void initializeChildrenItems() {
            List<String> postfixes = List.of("stairs", "slab", "wall");

            for (String postfix : postfixes) {
                Item found = findRelatedEntry(postfix);
                if (found != null) addChild(postfix, found);
            }

            Item spiked = findRelatedEntry("spiked", "");
            if (spiked != null) addChild("spiked", spiked);

            Item wood = getWood();
            if (wood != null) addChild("wood", wood);
        }


        public String getTranslationKey() {
            return "shape_set." + this.getNamespace() + "." + this.getTypeName();
        }

        private String getNamespace() {
            return id.getNamespace();
        }

        public Block mainChild() {
            return block;
        }

        public boolean hasChild(String slab) {
            return items.containsKey(slab);
        }

        public ItemLike getChild(String slab) {
            return items.get(slab);
        }

        public Collection<ItemLike> getChildren() {
            return items.values();
        }
    }

//    private static void registerShapeBlocks(Registrator<Block> event, Collection<ShapeSet> shapeSets) {
//        for (ShapeSet set : shapeSets) {
//            if (CNMConfig.VERTICAL_SLABS.get() && set.hasChild("slab")) {
//                ResourceLocation id = ClutterNoMore.location(set.getVariantId("vertical", "slab"));
//                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
//                    Block slab = Block.byItem((Item) set.getChild("slab"));
//
//                    if (slab.defaultBlockState().getValues().size() == 2) {
//                        Block block = new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(Block.byItem((Item) set.getChild("slab"))));
//                        event.register(id, block);
//                        set.addChild("vertical_slab_block", block);
//                    }
//                }
//            }
//
//            if (CNMConfig.STEPS.get() && set.hasChild("stairs")) {
//                ResourceLocation id = ClutterNoMore.location(set.getVariantId("", "step"));
//                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
//                    Block stairs = Block.byItem((Item) set.getChild("stairs"));
//
//                    if (stairs.defaultBlockState().getValues().size() == 4) {
//                        Block block = new StepBlock(BlockBehaviour.Properties.ofFullCopy(stairs));
//                        event.register(id, block);
//                        set.addChild("step_block", block);
//                    }
//                }
//            }
//        }
//    }

//    private static void registerShapeItems(Registrator<Item> event, Collection<ShapeSet> shapeSets) {
//        for (ShapeSet type : shapeSets) {
//            Block block;
//
//            block = (Block) type.getChild("vertical_slab_block");
//            if (block != null) {
//                ResourceLocation id = ClutterNoMore.location(type.getVariantId("vertical", "slab"));
//                if (!BuiltInRegistries.ITEM.containsKey(id)) {
//                    Item item = new BlockItem(block, new Item.Properties());
//                    event.register(id, item);
//                    type.addChild("vertical_slab", item);
//                }
//            }
//
//            block = (Block) type.getChild("step_block");
//            if (block != null) {
//                ResourceLocation id = ClutterNoMore.location(type.getVariantId("", "step"));
//                if (!BuiltInRegistries.ITEM.containsKey(id)) {
//                    Item item = new BlockItem(block, new Item.Properties());
//                    event.register(id, item);
//                    type.addChild("step", item);
//                }
//            }
//        }
//    }

    private static String stripSuffix(String path, String suffix) {
        if (path.endsWith("_" + suffix)) return path.substring(0, path.length() - suffix.length() - 1);
        if (path.endsWith(suffix)) return path.substring(0, path.length() - suffix.length());
        return path;
    }

    private static String stripPrefix(String path, String prefix) {
        if (path.startsWith(prefix + "_")) return path.substring(prefix.length() + 1);
        if (path.startsWith(prefix)) return path.substring(prefix.length());
        return path;
    }
}
