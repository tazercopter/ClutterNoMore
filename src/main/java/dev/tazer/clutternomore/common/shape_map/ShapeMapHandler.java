package dev.tazer.clutternomore.common.shape_map;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
//? fabric {
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
//?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public class ShapeMapHandler extends SimpleJsonResourceReloadListener
//? fabric {
    implements IdentifiableResourceReloadListener
//?}
{

    public static final Gson GSON = new GsonBuilder().create();

    public ShapeMapHandler() {
        super(GSON, "shape_map");
    }

    //? fabric {
    @Override
    public ResourceLocation getFabricId() {
        return ClutterNoMore.location("shape_map");
    }
    //?}

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> file, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, List<ResourceLocation>> result = new HashMap<>();
        Map<String, NamespaceShapeMap> namespaceMap = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> fileEntry : file.entrySet()) {
            ResourceLocation fileName = fileEntry.getKey();
            String path = fileName.getPath();
            if (!path.equals("add_shapes") && !path.equals("remove_shapes")) continue;

            Map<ResourceLocation, List<ResourceLocation>> fileShapeMap = new HashMap<>();

            JsonObject content = fileEntry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> contentEntry : content.entrySet()) {
                ResourceLocation key = ResourceLocation.parse(contentEntry.getKey());
                List<ResourceLocation> values = new ArrayList<>();

                for (JsonElement element : contentEntry.getValue().getAsJsonArray()) {
                    values.add(ResourceLocation.parse(element.getAsString()));
                }

                fileShapeMap.put(key, values);
            }

            String namespace = fileName.getNamespace();
            NamespaceShapeMap namespaceShapeMap = namespaceMap.computeIfAbsent(namespace, k -> new NamespaceShapeMap(new HashMap<>(), new HashMap<>()));

            if (path.equals("add_shapes")) {
                namespaceShapeMap.addMap().putAll(fileShapeMap);
            } else {
                namespaceShapeMap.removeMap().putAll(fileShapeMap);
            }
        }

        for (NamespaceShapeMap namespaceShapeMap : namespaceMap.values()) {
            result.putAll(namespaceShapeMap.getResultingMap());
        }

        ShapeMap.set(result);
    }

    private record NamespaceShapeMap(Map<ResourceLocation, List<ResourceLocation>> addMap, Map<ResourceLocation, List<ResourceLocation>> removeMap) {
        public Map<ResourceLocation, List<ResourceLocation>> getResultingMap() {
            for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : new HashSet<>(removeMap.entrySet())) {
                ResourceLocation key = entry.getKey();
                List<ResourceLocation> removeList = entry.getValue();

                List<ResourceLocation> list = addMap.get(key);
                if (list != null) {
                    if (list.removeAll(removeList)) {
                        if (list.isEmpty()) addMap.remove(key);
                    }
                }
            }

            return addMap;
        }
    }
}
