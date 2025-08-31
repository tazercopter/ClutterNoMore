package dev.tazer.clutternomore.event;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.datamap.ListMerger;
import dev.tazer.clutternomore.datamap.ListRemover;
import dev.tazer.clutternomore.datamap.Shapes;
import dev.tazer.clutternomore.registry.CDataComponents;
import dev.tazer.clutternomore.networking.ChangeStackPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        List<String> suffixes = List.of(
                "stairs",
                "slab",
                "vertical_slab",
                "wall"
        );

        for (Item item : event.getAllItems().toList()) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            List<Item> shapes = new ArrayList<>();

            Shapes removedShapes = item.builtInRegistryHolder().getData(REMOVE_SHAPE_DATA);

            for (String suffix : suffixes) {
                if (key.getPath().endsWith(suffix)) continue;

                Optional<Item> optional = getOptional(key, suffix);

                optional.ifPresent(shape -> {
                    if (removedShapes == null || !removedShapes.items().contains(shape)) {
                        shapes.add(shape);
                        event.modify(shape, builder -> builder.set(CDataComponents.BLOCK.get(), item));
                    }
                });
            }

            Shapes assignedShapes = item.builtInRegistryHolder().getData(ADD_SHAPE_DATA);

            if (item == Items.STONE_BRICKS) {
                if (assignedShapes != null) {
                    for (Item shape : assignedShapes.items()) {
                        shapes.add(shape);
                        event.modify(shape, builder -> builder.set(CDataComponents.BLOCK.get(), item));
                    }
                }
            }

            if (!shapes.isEmpty()) event.modify(item, builder -> builder.set(CDataComponents.SHAPES.get(), shapes));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event) {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item.getDefaultInstance().has(CDataComponents.BLOCK))
                .forEach(item -> event.remove(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
    }

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ADD_SHAPE_DATA);
        event.register(REMOVE_SHAPE_DATA);
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

    public static Optional<Item> getOptional(ResourceLocation key, String suffix) {
        String newSuffix = "_" + suffix;
        Optional<Item> optional = BuiltInRegistries.ITEM.getOptional(key.withSuffix(newSuffix));
        if (optional.isEmpty()) {
            if (key.getPath().endsWith("_planks")) {
                return BuiltInRegistries.ITEM.getOptional(key.withPath(path -> path.substring(0, path.length() - 7) + newSuffix));
            } else if (key.getPath().endsWith("s")) {
                return BuiltInRegistries.ITEM.getOptional(key.withPath(path -> path.substring(0, path.length() - 1) + newSuffix));
            }
        }

        return optional;
    }
}
