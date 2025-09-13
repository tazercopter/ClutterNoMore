package dev.tazer.clutternomore.common.event;


import dev.tazer.clutternomore.ClutterNoMore;

import dev.tazer.clutternomore.common.datamap.Shapes;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
//? if neoforge {
/*
import dev.tazer.clutternomore.common.datamap.ListMerger;
import dev.tazer.clutternomore.common.datamap.ListRemover;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
*///?}
import java.util.*;

// FIXME
//? if neoforge
/*@EventBusSubscriber(modid = ClutterNoMore.MODID)*/
public class DatamapHandler {

    //? if neoforge {
    /*public static final AdvancedDataMapType<Item, Shapes, ListRemover> ADD_SHAPE_DATA = AdvancedDataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "add_shapes"),
            Registries.ITEM,
            Shapes.CODEC
    ).synced(Shapes.SHAPES_CODEC, true).merger(new ListMerger()).remover(ListRemover.CODEC).build();

    public static final AdvancedDataMapType<Item, Shapes, ListRemover> REMOVE_SHAPE_DATA = AdvancedDataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "remove_shapes"),
            Registries.ITEM,
            Shapes.CODEC
    ).synced(Shapes.SHAPES_CODEC, true).merger(new ListMerger()).remover(ListRemover.CODEC).build();
    *///?}

    private static final Map<Item, List<Item>> SHAPES_DATAMAP_INTERNAL = new HashMap<>();
    private static final Map<Item, Item> INVERSE_SHAPES_DATAMAP_INTERNAL = new HashMap<>();
    private static final Map<Item, List<Item>> REMOVE_SHAPES_DATAMAP_INTERNAL = new HashMap<>();

    public static final Map<Item, List<Item>> SHAPES_DATAMAP = Collections.unmodifiableMap(SHAPES_DATAMAP_INTERNAL);
    public static final Map<Item, Item> INVERSE_SHAPES_DATAMAP = Collections.unmodifiableMap(INVERSE_SHAPES_DATAMAP_INTERNAL);

    //? if neoforge {
    /*@SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ADD_SHAPE_DATA);
        event.register(REMOVE_SHAPE_DATA);
    }

    @SubscribeEvent
    public static void onDataMapsUpdated(DataMapsUpdatedEvent event) {

        event.ifRegistry(Registries.ITEM, registry -> {
    *///?} else {
    public static void onDataMapsUpdated() {
        var registry = BuiltInRegistries.ITEM;
        //?}
            SHAPES_DATAMAP_INTERNAL.clear();
            INVERSE_SHAPES_DATAMAP_INTERNAL.clear();
            REMOVE_SHAPES_DATAMAP_INTERNAL.clear();

            for (Item item : registry.stream().toList()) {
                List<Item> shapes = new ArrayList<>();

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


                if (!shapes.isEmpty()) SHAPES_DATAMAP_INTERNAL.put(item, shapes);
            }

            //? if neoforge {
            /*registry.getDataMap(ADD_SHAPE_DATA).forEach((resourceKey, shapes) -> {
                Item item = BuiltInRegistries.ITEM.get(resourceKey);
                List<Item> items = new ArrayList<>();
                shapes.items().forEach(itemHolder -> items.add(itemHolder.value()));
                SHAPES_DATAMAP_INTERNAL.put(item, items);
            });

            registry.getDataMap(REMOVE_SHAPE_DATA).forEach((resourceKey, shapes) -> {
                Item item = BuiltInRegistries.ITEM.get(resourceKey);

                List<Item> items = new ArrayList<>();
                shapes.items().forEach(itemHolder -> items.add(itemHolder.value()));
                REMOVE_SHAPES_DATAMAP_INTERNAL.put(item, items);
            });
            *///?}

            for (Map.Entry<Item, List<Item>> entry : new HashSet<>(SHAPES_DATAMAP_INTERNAL.entrySet())) {
                Item item = entry.getKey();
                List<Item> shapes = entry.getValue();

                INVERSE_SHAPES_DATAMAP_INTERNAL.remove(item);

                if (new HashSet<>(REMOVE_SHAPES_DATAMAP_INTERNAL.getOrDefault(item, List.of())).containsAll(shapes)) {
                    SHAPES_DATAMAP_INTERNAL.remove(item);
                } else {
                    for (Item shape : shapes) {
                        INVERSE_SHAPES_DATAMAP_INTERNAL.put(shape, item);
                    }
                }
            }

            for (Map.Entry<Item, Item> entry : new HashSet<>(INVERSE_SHAPES_DATAMAP_INTERNAL.entrySet())) {
                if (!SHAPES_DATAMAP_INTERNAL.containsKey(entry.getValue())) {
                    INVERSE_SHAPES_DATAMAP_INTERNAL.remove(entry.getKey());
                }
            }
        //? if neoforge
        /*});*/
    }

    //? if neoforge {
    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event) {
        BuiltInRegistries.ITEM.stream()
                .filter(INVERSE_SHAPES_DATAMAP::containsKey)
                .forEach(item -> event.remove(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
    }
    *///?}
}
