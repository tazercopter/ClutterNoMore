package dev.tazer.clutternomore.common.data;

import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DynamicServerResources extends DynamicServerResourceProvider {

    public static void register() {
        RegHelper.registerDynamicResourceProvider(new DynamicServerResources());
    }
    protected DynamicServerResources() {
        super(ClutterNoMore.location("generated_pack"), PackGenerationStrategy.runOnce());
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        List<String> namespaces = new ArrayList<>(List.of("minecraft", ClutterNoMore.MODID));
        ModList.get().getMods().forEach(info -> namespaces.add(info.getNamespace()));
        return namespaces;
    }

    private static final List<DataGenerator> GENERATORS = List.of(
            new TagsGenerator(),
            new LootGenerator()
    );

    @Override
    public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((resourceManager, sink) -> {
            for (DataGenerator generator : GENERATORS) {
                generator.initialize(resourceManager, sink);

                generator.generate(resourceManager, sink);

                List<Item> items = BuiltInRegistries.ITEM.stream().toList();
                for (Item item : items) {
                    generator.generate(item, resourceManager, sink);
                }

                List<Block> blocks = BuiltInRegistries.BLOCK.stream().toList();
                for (Block block : blocks) {
                    generator.generate(block, resourceManager, sink);
                }

                generator.finish(resourceManager, sink);
            }
        });
    }
}
