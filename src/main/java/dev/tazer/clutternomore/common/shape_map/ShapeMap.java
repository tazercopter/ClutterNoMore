package dev.tazer.clutternomore.common.shape_map;

import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.*;

public class ShapeMap {
    public static final Map<Item, List<Item>> SHAPES_DATAMAP = new HashMap<>();
    public static final Map<Item, Item> INVERSE_SHAPES_DATAMAP = new HashMap<>();

    public static boolean hasShapes(Item item) {
        return SHAPES_DATAMAP.containsKey(item);
    }

    public static boolean isShape(Item item) {
        return INVERSE_SHAPES_DATAMAP.containsKey(item);
    }

    public static boolean contains(Item item) {
        return hasShapes(item) || isShape(item);
    }

    public static Item getParent(Item item) {
        return INVERSE_SHAPES_DATAMAP.getOrDefault(item, item);
    }

    public static boolean isParentOfShape(Item parent, Item shape) {
        return getParent(shape) == parent;
    }

    public static boolean inSameShapes(Item item, Item other) {
        return getParent(item) == getParent(other);
    }

    public static List<Item> getShapes(Item item) {
        return SHAPES_DATAMAP.getOrDefault(getParent(item), List.of());
    }

    public static void set(Map<ResourceLocation, List<ResourceLocation>> idMap) {
        SHAPES_DATAMAP.clear();
        INVERSE_SHAPES_DATAMAP.clear();

        for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : idMap.entrySet()) {
            ResourceLocation key = entry.getKey();
            Item newKey = BuiltInRegistries.ITEM.getOptional(key).orElse(null);
            if (newKey != null) {
                List<ResourceLocation> value = entry.getValue();
                List<Item> newValue = new ArrayList<>();

                for (ResourceLocation location : value) {
                    BuiltInRegistries.ITEM.getOptional(location).map(newValue::add);
                }

                if (!newValue.isEmpty()) SHAPES_DATAMAP.put(newKey, newValue);
            }
        }

        for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
            List<Item> shapes = new ArrayList<>(getShapes(item));

            BlockSetRegistry.ShapeSet shapeSet = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);

            if (shapeSet != null) {
                Item mainChild = shapeSet.mainChild().asItem();
                if (item == mainChild) {
                    shapeSet.getChildren().forEach(child -> {
                        if (child != mainChild && child.getValue() instanceof Item shape) {
                            shapes.add(shape);
                        }
                    });
                }
            }


            if (!shapes.isEmpty()) SHAPES_DATAMAP.put(item, shapes);
        }

        for (Map.Entry<Item, List<Item>> entry : new HashSet<>(SHAPES_DATAMAP.entrySet())) {
            Item item = entry.getKey();
            List<Item> shapes = entry.getValue();

            for (Item shape : shapes) {
                INVERSE_SHAPES_DATAMAP.put(shape, item);
            }
        }

        for (Map.Entry<Item, Item> entry : new HashSet<>(INVERSE_SHAPES_DATAMAP.entrySet())) {
            if (!hasShapes(entry.getValue())) {
                INVERSE_SHAPES_DATAMAP.remove(entry.getKey());
            }
        }
    }
}
