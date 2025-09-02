package dev.tazer.clutternomore.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.builtin.RaidHeroGift;

import java.util.List;

public record Shapes(HolderSet<Item> items) {
    public static final Codec<Shapes> SHAPES_CODEC = RegistryCodecs.homogeneousList(Registries.ITEM).xmap(Shapes::new, Shapes::items);
    public static final Codec<Shapes> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(
                    instance -> instance.group(
                            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("shapes").forGetter(Shapes::items)
                    ).apply(instance, Shapes::new)
            ),
            SHAPES_CODEC
    );


}
