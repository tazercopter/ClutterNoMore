package dev.tazer.clutternomore.event;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.datamap.ListMerger;
import dev.tazer.clutternomore.datamap.ListRemover;
import dev.tazer.clutternomore.datamap.Shapes;
import dev.tazer.clutternomore.registry.CBlockSet;
import dev.tazer.clutternomore.networking.ChangeStackPayload;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.*;

@EventBusSubscriber(modid = ClutterNoMore.MODID)
public class CommonEvents {

    public static final AdvancedDataMapType<Item, Shapes, ListRemover> ADD_SHAPE_DATA = AdvancedDataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "add_shapes"),
            Registries.ITEM,
            Shapes.CODEC
    ).merger(new ListMerger()).remover(ListRemover.CODEC).build();

    public static final AdvancedDataMapType<Item, Shapes, ListRemover> REMOVE_SHAPE_DATA = AdvancedDataMapType.builder(
            ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "remove_shapes"),
            Registries.ITEM,
            Shapes.CODEC
    ).merger(new ListMerger()).remover(ListRemover.CODEC).build();

    private static final Map<Item, List<Item>> SHAPES_DATAMAP_INTERNAL = new HashMap<>();

    public static final Map<Item, List<Item>> SHAPES_DATAMAP = Collections.unmodifiableMap(SHAPES_DATAMAP_INTERNAL);

    private static final Map<Item, Item> INVERSE_SHAPES_DATAMAP_INTERNAL = new HashMap<>();

    public static final Map<Item, Item> INVERSE_SHAPES_DATAMAP = Collections.unmodifiableMap(INVERSE_SHAPES_DATAMAP_INTERNAL);

    private static final Map<Item, List<Item>> REMOVE_SHAPES_DATAMAP_INTERNAL = new HashMap<>();

    public static final Map<Item, List<Item>> REMOVE_SHAPES_DATAMAP = Collections.unmodifiableMap(REMOVE_SHAPES_DATAMAP_INTERNAL);

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ADD_SHAPE_DATA);
        event.register(REMOVE_SHAPE_DATA);
    }

    @SubscribeEvent
    public static void onDataMapsUpdated(DataMapsUpdatedEvent event) {
        event.ifRegistry(Registries.ITEM, registry -> {
            SHAPES_DATAMAP_INTERNAL.clear();
            INVERSE_SHAPES_DATAMAP_INTERNAL.clear();
            REMOVE_SHAPES_DATAMAP_INTERNAL.clear();

            for (Item item : registry.stream().toList()) {
                List<Item> shapes = new ArrayList<>();

                CBlockSet.ShapeSet shapeSet = BlockSetAPI.getBlockTypeOf(item, CBlockSet.ShapeSet.class);

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

            registry.getDataMap(ADD_SHAPE_DATA).forEach((resourceKey, shapes) -> {
                Item item = BuiltInRegistries.ITEM.get(resourceKey);
                SHAPES_DATAMAP_INTERNAL.put(item, shapes.items());
            });

            registry.getDataMap(REMOVE_SHAPE_DATA).forEach((resourceKey, shapes) -> {
                Item item = BuiltInRegistries.ITEM.get(resourceKey);

                REMOVE_SHAPES_DATAMAP_INTERNAL.put(item, shapes.items());
            });

            for (Map.Entry<Item, List<Item>> entry : new HashSet<>(SHAPES_DATAMAP_INTERNAL.entrySet())) {
                Item item = entry.getKey();
                List<Item> shapes = entry.getValue();

                if (new HashSet<>(REMOVE_SHAPES_DATAMAP_INTERNAL.getOrDefault(item, List.of())).containsAll(shapes)) {
                    SHAPES_DATAMAP_INTERNAL.remove(item);
                }

                for (Item shape : shapes) {
                    INVERSE_SHAPES_DATAMAP_INTERNAL.put(shape, item);
                }
            }

            for (Map.Entry<Item, Item> entry : new HashSet<>(INVERSE_SHAPES_DATAMAP_INTERNAL.entrySet())) {
                if (!SHAPES_DATAMAP_INTERNAL.containsKey(entry.getValue())) {
                    INVERSE_SHAPES_DATAMAP_INTERNAL.remove(entry.getKey());
                }
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event) {
        BuiltInRegistries.ITEM.stream()
                .filter(INVERSE_SHAPES_DATAMAP::containsKey)
                .forEach(item -> event.remove(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ChangeStackPayload.TYPE,
                ChangeStackPayload.STREAM_CODEC,
                ChangeStackPayload::handleDataOnServer
        );
    }

    public static Optional<Item> getOptional(ResourceLocation key, String postfix) {
        String newSuffix = "_" + postfix;
        Optional<Item> optional = BuiltInRegistries.ITEM.getOptional(key.withSuffix(newSuffix));
        if (optional.isEmpty()) {
            if (key.getPath().endsWith("_block")) {
                return BuiltInRegistries.ITEM.getOptional(key.withPath(path -> path.substring(0, path.length() - 6) + newSuffix));
            } else if (key.getPath().endsWith("_planks")) {
                return BuiltInRegistries.ITEM.getOptional(key.withPath(path -> path.substring(0, path.length() - 7) + newSuffix));
            } else if (key.getPath().endsWith("s")) {
                return BuiltInRegistries.ITEM.getOptional(key.withPath(path -> path.substring(0, path.length() - 1) + newSuffix));
            }
        }

        return optional;
    }

    public static boolean hasBlock(ResourceLocation key, String postfix) {
        String newSuffix = "_" + postfix;
        boolean optional = BuiltInRegistries.BLOCK.containsKey(key.withSuffix(newSuffix));
        if (!optional) {
            if (key.getPath().endsWith("_block")) {
                return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> path.substring(0, path.length() - 6) + newSuffix));
            } else if (key.getPath().endsWith("_planks")) {
                return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> path.substring(0, path.length() - 7) + newSuffix));
            } else if (key.getPath().endsWith("s")) {
                return BuiltInRegistries.BLOCK.containsKey(key.withPath(path -> path.substring(0, path.length() - 1) + newSuffix));
            }
        }

        return optional;
    }
}
