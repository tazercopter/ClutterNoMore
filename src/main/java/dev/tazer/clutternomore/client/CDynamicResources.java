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
import net.neoforged.fml.ModList;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
        public Collection<String> additionalNamespaces() {
            List<String> namespaces = new ArrayList<>(List.of("minecraft"));
            ModList.get().getMods().forEach(info -> namespaces.add(info.getNamespace()));
            namespaces.remove(ClutterNoMore.MODID);
            return namespaces;
        }

        @Override
        public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
            VERTICAL_SLABS.clear();

            for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
                CBlockSet.ShapeSet shapeSet = BlockSetAPI.getBlockTypeOf(item, CBlockSet.ShapeSet.class);

                if (shapeSet != null && item == shapeSet.getChild("slab")) {
                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);

                    executor.accept(((resourceManager, sink) -> {
                        Optional<Resource> slabModel = resourceManager.getResource(location.withPath(path -> "models/block/" + path + ".json"));
                        if (slabModel.isPresent()) {
                            String name = verticalSlabName(location.getPath());
                            VERTICAL_SLABS.add(name);
                            ResourceLocation slabModelLocation = ClutterNoMore.location(name);
                            if (resourceManager.getResource(slabModelLocation.withPath(path -> "models/item/" + path + ".json")).isEmpty()) {
                                JsonObject itemModelJson = new JsonObject();
                                itemModelJson.addProperty("parent", ClutterNoMore.MODID + ":block/" + name);
                                sink.addItemModel(ClutterNoMore.location(name), itemModelJson);
                            }

                            String bottomTexture = null;
                            String sideTexture = null;
                            String topTexture = null;

                            try {
                                BufferedReader reader = slabModel.get().openAsReader();
                                String line = reader.readLine();
                                while (!line.isEmpty()) {
                                    if (BuiltInRegistries.ITEM.getKey(item).getPath().endsWith("checker_slab"))
                                        System.out.println(BuiltInRegistries.ITEM.getKey(item));

                                    int endIndex = line.length() - (line.endsWith(",") ? 2 : 1);

                                    if (line.contains("\"bottom\": ")) {
                                        bottomTexture = line.substring(line.indexOf("\"bottom\": ") + 11, endIndex);
                                    }

                                    if (line.contains("\"side\": ")) {
                                        sideTexture = line.substring(line.indexOf("\"side\": ") + 9, endIndex);
                                    }

                                    if (line.contains("\"top\": ")) {
                                        topTexture = line.substring(line.indexOf("\"top\": ") + 8, endIndex);
                                    }

                                    if (bottomTexture != null && sideTexture != null && topTexture != null) break;

                                    line = reader.readLine();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            if (bottomTexture == null) bottomTexture = topTexture;
                            if (sideTexture == null) sideTexture = topTexture;

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

    public static String verticalSlabName(String name) {
        name = "vertical_" + name.substring(0, name.length() - 5);

        if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
        if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
        if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
        return name + "_slab";
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
