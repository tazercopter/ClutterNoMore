package dev.tazer.clutternomore.client.assets;

import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DynamicClientResources extends DynamicClientResourceProvider {

    public static void register() {
        RegHelper.registerDynamicResourceProvider(new DynamicClientResources());
    }
    protected DynamicClientResources() {
        super(ClutterNoMore.location("generated_pack"), PackGenerationStrategy.runOnce());
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        List<String> namespaces = new ArrayList<>(List.of("minecraft", ClutterNoMore.MODID));
        ModList.get().getMods().forEach(info -> namespaces.add(info.getNamespace()));
        return namespaces;
    }

    private static final List<AssetGenerator> GENERATORS = List.of(
            new VerticalSlabGenerator(),
            new StepGenerator()
    );

    @Override
    public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((resourceManager, sink) -> {
            for (AssetGenerator generator : GENERATORS) {
                generator.initialize(resourceManager, sink);

                List<Item> items = BuiltInRegistries.ITEM.stream().toList();
                for (Item item : items) {
                    generator.generate(item, resourceManager, sink);
                }
            }
        });
    }

    @Override
    public void addDynamicTranslations(AfterLanguageLoadEvent languageEvent) {
        for (AssetGenerator generator : GENERATORS) {
            generator.translate(languageEvent);
        }
    }
}
