package dev.tazer.clutternomore.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.List;

public record Shapes(List<Item> items) {
    public static final Codec<Shapes> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("shapes").forGetter(Shapes::items)
            ).apply(instance, Shapes::new)
    );
}
