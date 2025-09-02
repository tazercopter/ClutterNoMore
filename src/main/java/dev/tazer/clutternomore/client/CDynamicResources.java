package dev.tazer.clutternomore.client;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.registry.CBlockSet;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SlabBlock;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class CDynamicResources {
    public static void init() {
        ClientAssetsGenerator generator = new ClientAssetsGenerator();
        generator.register();
    }

    public static class ClientAssetsGenerator extends DynClientResourcesGenerator {

        public static List<String> VERTICAL_SLABS = new ArrayList<>();

        protected ClientAssetsGenerator() {
            super(new DynamicTexturePack(ClutterNoMore.location("generated_pack"), Pack.Position.BOTTOM, false, false));
        }

        @Override
        public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
            VERTICAL_SLABS.clear();

            Stream<Item> slabs = BuiltInRegistries.ITEM.stream().filter(item -> item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock);

            for (Item slab : slabs.toList()) {
                ResourceLocation location = BuiltInRegistries.ITEM.getKey(slab);

                executor.accept(((resourceManager, sink) -> {
                    Optional<Resource> slabModel = resourceManager.getResource(location.withPath(path -> "models/block/" + path + ".json"));
                    if (slabModel.isPresent()) {
                        String bottomTexture = null;
                        String sideTexture = null;
                        String topTexture = null;

                        try {
                            BufferedReader reader = slabModel.get().openAsReader();
                            int read = 0;
                            for (int i = 0; i < 10; i++) {
                                String line = reader.readLine();
                                if (line.contains("\"bottom\": ")) {
                                    bottomTexture = line.substring(line.indexOf("\"bottom\": ") + 11, line.length() - (read == 2 ? 1 : 2));
                                    read++;
                                }

                                if (line.contains("\"side\": ")) {
                                    sideTexture = line.substring(line.indexOf("\"side\": ") + 9, line.length() - (read == 2 ? 1 : 2));
                                    read++;
                                }

                                if (line.contains("\"top\": ")) {
                                    topTexture = line.substring(line.indexOf("\"top\": ") + 8, line.length() - (read == 2 ? 1 : 2));
                                    read++;
                                }

                                if (bottomTexture != null && sideTexture != null && topTexture != null) break;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        String name = verticalSlabName(location.getPath());
                        VERTICAL_SLABS.add(name);

                        ResourceLocation slabModelLocation = ClutterNoMore.location(name);
                        if (resourceManager.getResource(slabModelLocation.withPath(path -> "blockstates/" + path + ".json")).isEmpty()) {
                            if (resourceManager.getResource(slabModelLocation.withPath(path -> "models/block/" + path + ".json")).isEmpty()) {
                                JsonObject slabModelJson = new JsonObject();
                                slabModelJson.addProperty("parent", ClutterNoMore.MODID + ":block/vertical_slab");

                                if (bottomTexture != null && sideTexture != null && topTexture != null) {
                                    JsonObject textures = new JsonObject();
                                    textures.addProperty("bottom", bottomTexture);
                                    textures.addProperty("side", sideTexture);
                                    textures.addProperty("top", topTexture);
                                    slabModelJson.add("textures", textures);
                                }

                                sink.addBlockModel(slabModelLocation, slabModelJson);
                            }

                            ResourceLocation blockModelLocation = ClutterNoMore.location(name + "_block");
                            if (resourceManager.getResource(blockModelLocation.withPath(path -> "models/block/" + path + ".json")).isEmpty()) {
                                JsonObject blockModelJson = new JsonObject();
                                blockModelJson.addProperty("parent", ClutterNoMore.MODID + ":block/full_block");

                                if (bottomTexture != null && sideTexture != null && topTexture != null) {
                                    JsonObject textures = new JsonObject();
                                    textures.addProperty("bottom", bottomTexture);
                                    textures.addProperty("side", sideTexture);
                                    textures.addProperty("top", topTexture);
                                    blockModelJson.add("textures", textures);
                                }

                                sink.addBlockModel(blockModelLocation, blockModelJson);
                            }

                            if (resourceManager.getResource(slabModelLocation.withPath(path -> "models/item/" + path + ".json")).isEmpty()) {
                                JsonObject itemModelJson = new JsonObject();
                                itemModelJson.addProperty("parent", ClutterNoMore.MODID + ":block/" + name);
                                sink.addItemModel(ClutterNoMore.location(name), itemModelJson);
                            }

                            StaticResource verticalSlabBlockstate = StaticResource.getOrThrow(resourceManager, ClutterNoMore.location("blockstates/vertical_slab.json"));
                            sink.addSimilarJsonResource(resourceManager, verticalSlabBlockstate, (string) -> {
                                string = string.replace("vertical_slab", name);
                                return string.replace("full_block", name + "_block");
                            });
                        }
                    }
                }));
            }
        }

        @Override
        public void addDynamicTranslations(AfterLanguageLoadEvent languageEvent) {
            VERTICAL_SLABS.forEach(name -> languageEvent.addEntry("block." + ClutterNoMore.MODID + "." + name, langName(name)));
        }

        @Override
        public Logger getLogger() {
            return ClutterNoMore.LOGGER;
        }
    }

    public static String verticalSlabName(String path) {
        return path.substring(0, path.length() - 4) + "vertical_slab";
    }

    public static String langName(String name) {
        String processed = name.replace("_", " ");

        List<String> nonCapital = List.of("of", "and", "with");

        String[] words = processed.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (!nonCapital.contains(word)) result.append(Character.toUpperCase(word.charAt(0)));
                else result.append(word.charAt(0));
                result.append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }
}
