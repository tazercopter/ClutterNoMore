package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class AssetGenerator {
    public final static Path pack = Platform.INSTANCE.getResourcePack().resolve("clutternomore");
    public final static Path assets = pack.resolve("assets/clutternomore");
    public static Set<String> keys;

    public static @Nullable String getModel(ResourceManager manager, ResourceLocation blockstate) {
        Optional<Resource> file = manager.getResource(blockstate.withPath(path -> "blockstates/" + path + ".json"));
        if (file.isEmpty()) return null;

        try (BufferedReader reader = file.get().openAsReader()) {
            String line = reader.readLine();
            while (line != null) {
                int idx = line.indexOf("\"model\":");
                if (idx >= 0) {
                    int start = line.indexOf('"', idx + 8) + 1;
                    int end = line.indexOf('"', start);
                    if (start > 0 && end > start) return line.substring(start, end);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            ClutterNoMore.LOGGER.catching(e);
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void generate() {
        //lang
        var jsonObject = new JsonObject();
        keys.forEach((s)-> {
            jsonObject.addProperty("block.clutternomore." + s, VerticalSlabGenerator.langName(s));
        });
        final Path assets = pack.resolve("assets/clutternomore");
        write(assets.resolve("lang"), "en_us.json", jsonObject.toString());
        // pack.mcmeta
        //? if >1.21.1 {
        String packVersion = "64";
        //?} else {
        /*String packVersion = "34";
         *///?}
        write(pack, "pack.mcmeta", "{   \"pack\": {     \"description\": \"Dynamic data for Clutter No More\",     \"pack_format\": "+packVersion+"   } }");
        VerticalSlabGenerator.generate();

    }

    public static void write(Path path, String fileName, String contents) {
        try {
            path.toFile().mkdirs();
            FileWriter langWriter = new FileWriter(path.resolve(fileName).toFile());
            langWriter.write(contents);
            langWriter.close();  // must close manually
            ClutterNoMore.LOGGER.info("Successfully wrote to {}", path.resolve(fileName));
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write dynamic data. %s".formatted(e));
        }
    }
}
