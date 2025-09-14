package dev.tazer.clutternomore.common;

import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.world.item.Item;

import static dev.tazer.clutternomore.common.event.ShapeMapHandler.INVERSE_SHAPES_DATAMAP;

public class CHooks {
    public static boolean denyItem(Item item) {
        BlockSetRegistry.ShapeSet shapeSet = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
        if (shapeSet != null && shapeSet.mainChild().asItem() != item) return true;
        return INVERSE_SHAPES_DATAMAP.containsKey(item);
    }
}
