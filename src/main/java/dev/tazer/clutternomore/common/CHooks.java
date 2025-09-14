package dev.tazer.clutternomore.common;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.world.item.Item;

public class CHooks {
    public static boolean denyItem(Item item) {
        return ShapeMap.isShape(item);
    }
}
