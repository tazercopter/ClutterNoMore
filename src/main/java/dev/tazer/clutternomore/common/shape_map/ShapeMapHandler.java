package dev.tazer.clutternomore.common.shape_map;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<ResourceLocation, List<ResourceLocation>> result = new HashMap<>();
        Map<String, NamespaceData> namespaceMap = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            JsonElement content = entry.getValue();

            String namespace = fileId.getNamespace();
            String path = fileId.getPath();

            if (!path.equals("add_shapes") && !path.equals("remove_shapes")) continue;

            NamespaceData nsData = namespaceMap.computeIfAbsent(namespace, k -> new NamespaceData(new HashMap<>(), new HashMap<>()));

            Map<ResourceLocation, List<ResourceLocation>> ids = parseIdList(content);

            if (path.equals("add_shapes")) {
                nsData.addMap().putAll(ids);
            } else {
                nsData.removeMap().putAll(ids);
            }
        }

        for (NamespaceData nsData : namespaceMap.values()) {
            result.putAll(nsData.getResultingMap());
        }

        ShapeMap.set(result);
    }

    private Map<ResourceLocation, List<ResourceLocation>> parseIdList(JsonElement element) {
        return new HashMap<>();
    }

    private record NamespaceData(Map<ResourceLocation, List<ResourceLocation>> addMap, Map<ResourceLocation, List<ResourceLocation>> removeMap) {
        public Map<ResourceLocation, List<ResourceLocation>> getResultingMap() {
            for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : new HashSet<>(addMap.entrySet())) {
                ResourceLocation item = entry.getKey();
                List<ResourceLocation> shapes = entry.getValue();

                if (new HashSet<>(removeMap.getOrDefault(item, List.of())).containsAll(shapes)) {
                    addMap.remove(item);
                }
            }

            return addMap;
        }
    }
}
