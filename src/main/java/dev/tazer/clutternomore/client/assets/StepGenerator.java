package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.registry.CBlockSet;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class StepGenerator implements AssetGenerator {
    public static List<String> STEPS = new ArrayList<>();

    @Override
    public void initialize(ResourceManager manager, ResourceSink sink) {
        STEPS.clear();
    }

    public void generate(Item item, ResourceManager manager, ResourceSink sink) {
        if (!CNMConfig.STEPS.get()) return;

        CBlockSet.ShapeSet set = BlockSetAPI.getBlockTypeOf(item, CBlockSet.ShapeSet.class);
        if (set == null || item != set.getChild("stairs")) return;

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        String name = stepName(key.getPath());
        STEPS.add(name);

        ResourceLocation id = ClutterNoMore.location(name);

        if (manager.getResource(id.withPath(path -> "models/item/" + path + ".json")).isEmpty()) {
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", ClutterNoMore.MODID + ":block/" + name);
            sink.addItemModel(ClutterNoMore.location(name), itemModel);
        }

        String modelPath = getModel(manager, key);
        if (modelPath == null) return;

        ResourceLocation sourceModel = ResourceLocation.parse(modelPath).withPath(path -> "models/" + path + ".json");

        Optional<Resource> res = manager.getResource(sourceModel);
        String bottom = null;
        String side = null;
        String top = null;

        if (res.isPresent()) {
            try (BufferedReader reader = res.get().openAsReader()) {
                String line = reader.readLine();
                while (line != null) {
                    int end = line.lastIndexOf("\"");

                    if (line.contains("\"bottom\": ")) bottom = line.substring(line.indexOf("\"bottom\": ") + 11, end);
                    if (line.contains("\"side\": ")) side = line.substring(line.indexOf("\"side\": ") + 9, end);
                    if (line.contains("\"top\": ")) top = line.substring(line.indexOf("\"top\": ") + 8, end);

                    if (bottom != null && side != null && top != null) break;
                    line = reader.readLine();
                }
            } catch (IOException e) {
                ClutterNoMore.LOGGER.catching(e);
                throw new RuntimeException(e);
            }
        }

        if (top != null) {
            if (bottom == null) bottom = top;
            if (side == null) side = top;
        }

        JsonObject textures = new JsonObject();
        if (top != null) {
            textures.addProperty("bottom", bottom);
            textures.addProperty("side", side);
            textures.addProperty("top", top);
        }

        if (manager.getResource(id.withPath(path -> "blockstates/" + path + ".json")).isEmpty()) {
            if (manager.getResource(id.withPath(path -> "models/block/" + path + ".json")).isEmpty()) {
                JsonObject blockModel = new JsonObject();
                blockModel.addProperty("parent", ClutterNoMore.MODID + ":block/step");
                if (!textures.isEmpty()) blockModel.add("textures", textures);
                sink.addBlockModel(id, blockModel);
            }

            ResourceLocation topStepId = ClutterNoMore.location(name + "_top");
            if (manager.getResource(topStepId.withPath(path -> "models/block/" + path + ".json")).isEmpty()) {
                JsonObject fullModel = new JsonObject();
                fullModel.addProperty("parent", ClutterNoMore.MODID + ":block/step_top");
                if (!textures.isEmpty()) fullModel.add("textures", textures);
                sink.addBlockModel(topStepId, fullModel);
            }

            StaticResource template = StaticResource.getOrThrow(manager, ClutterNoMore.location("blockstates/step.json"));
            sink.addSimilarJsonResource(manager, template, string -> string
                    .replace("step", name)
            );
        }
    }

    @Override
    public void translate(AfterLanguageLoadEvent languageEvent) {
        STEPS.forEach(name -> languageEvent.addEntry("block." + ClutterNoMore.MODID + "." + name, langName(name)));
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

    public static String stepName(String name) {
        name = name.substring(0, name.length() - 7);

        if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
        if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
        if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
        return name + "_step";
    }
}

