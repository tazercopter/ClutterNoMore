package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;
import static dev.tazer.clutternomore.client.assets.AssetGenerator.write;

public final class VerticalSlabGenerator {
    public static ArrayList<ResourceLocation> SLABS = new ArrayList<>();

    public static void generate() {

        for (ResourceLocation id : SLABS) {
            try {
                var resourceManager = Minecraft.getInstance().getResourceManager();
                //blockstate
                JsonElement smoothStoneBlockState = Platform.INSTANCE.getFileInJar(MODID, "assets/clutternomore/blockstates/vertical_smooth_stone_slab.json");
                String blockstate = smoothStoneBlockState.toString().replaceAll("smooth_stone_slab", id.getPath());
                write(AssetGenerator.assets.resolve("blockstates"), "vertical_%s.json".formatted(id.getPath()), blockstate);

                // block models
                var potentialSlabModel = resourceManager.getResource(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "models/block/" + id.getPath() + ".json"));
                if (potentialSlabModel.isPresent()) {
                    JsonObject blockModel = JsonParser.parseReader(potentialSlabModel.get().openAsReader()).getAsJsonObject();
                    blockModel.addProperty("parent", "clutternomore:block/templates/vertical_slab");
                    write(AssetGenerator.assets.resolve("models/block"), "vertical_"+id.getPath() + ".json", blockModel.toString());
                    blockModel.addProperty("parent", "clutternomore:block/templates/vertical_slab_double");
                    write(AssetGenerator.assets.resolve("models/block"), "vertical_"+ id.getPath() + "_double.json", blockModel.toString());
                }
                // item models
                //? if >1.21.4 {
                JsonObject itemState = new JsonObject();
                JsonObject model = new JsonObject();
                model.addProperty("type", "minecraft:model");
                model.addProperty("model", "clutternomore:block/vertical_"+id.getPath());
                itemState.add("model", model);
                write(AssetGenerator.assets.resolve("items") , "vertical_%s.json".formatted(id.getPath()), itemState.toString());
                //?} else {
                /*JsonObject itemModel = new JsonObject();
                itemModel.addProperty("parent", "clutternomore:block/vertical_"+id.getPath());
                write(AssetGenerator.assets.resolve("models/item") , "vertical_%s.json".formatted(id.getPath()), itemModel.toString());
                *///?}
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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

    public static String verticalSlabName(String name) {
        name = "vertical_" + name.substring(0, name.length() - 5);

        if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
        if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
        if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
        return name + "_slab";
    }
}

