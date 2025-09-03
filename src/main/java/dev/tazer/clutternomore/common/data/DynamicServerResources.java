package dev.tazer.clutternomore.common.data;

import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynServerResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DynamicServerResources extends DynServerResourcesGenerator {

    public static final DynamicServerResources INSTANCE = new DynamicServerResources();

    protected DynamicServerResources() {
        super(new DynamicDataPack(ClutterNoMore.location("generated_pack")));
    }

    @Override
    public Collection<String> additionalNamespaces() {
        List<String> namespaces = new ArrayList<>(List.of("minecraft"));
        ModList.get().getMods().forEach(info -> namespaces.add(info.getNamespace()));
        namespaces.remove(ClutterNoMore.MODID);
        return namespaces;
    }

    private static final List<DataGenerator> GENERATORS = List.of(
            new TagGenerator()
    );

    @Override
    public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((resourceManager, sink) -> {
            for (DataGenerator generator : GENERATORS) {
                generator.initialize(resourceManager, sink);

                List<Item> items = BuiltInRegistries.ITEM.stream().toList();
                for (Item item : items) {
                    generator.generate(item, resourceManager, sink);
                }

                generator.finish(resourceManager, sink);
            }
        });
    }

    @Override
    public Logger getLogger() {
        return ClutterNoMore.LOGGER;
    }
}
