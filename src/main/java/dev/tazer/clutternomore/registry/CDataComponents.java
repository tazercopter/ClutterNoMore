package dev.tazer.clutternomore.registry;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class CDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ClutterNoMore.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Item>>> SHAPES = COMPONENTS.registerComponentType(
            "shapes",
            builder -> builder
                    .persistent(BuiltInRegistries.ITEM.byNameCodec().listOf())
                    .networkSynchronized(ByteBufCodecs.fromCodec(BuiltInRegistries.ITEM.byNameCodec().listOf()))
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Item>> BLOCK = COMPONENTS.registerComponentType(
            "block",
            builder -> builder
                    .persistent(BuiltInRegistries.ITEM.byNameCodec())
                    .networkSynchronized(ByteBufCodecs.fromCodec(BuiltInRegistries.ITEM.byNameCodec()))
    );
}
