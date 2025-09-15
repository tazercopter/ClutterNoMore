package dev.tazer.clutternomore.common;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.Item;

public class CHooks {
    public static boolean denyItem(Item item) {
        return ShapeMap.isShape(item);
    }
}
