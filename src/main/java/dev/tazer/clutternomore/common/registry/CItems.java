package dev.tazer.clutternomore.common.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ClutterNoMore.MODID);

    public static <T extends Item> Supplier<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
