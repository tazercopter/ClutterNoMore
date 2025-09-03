package dev.tazer.clutternomore.common.datamap;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ListRemover(Shapes shapes) implements DataMapValueRemover<Item, Shapes> {
    public static final Codec<ListRemover> CODEC = Shapes.CODEC.xmap(ListRemover::new, ListRemover::shapes);

    @Override
    public Optional<Shapes> remove(Shapes shapes, Registry<Item> registry, Either<TagKey<Item>, ResourceKey<Item>> either, Item item) {
        List<Holder<Item>> finalList = new ArrayList<>(shapes.items().stream().toList());
        for (Holder<Item> shape : this.shapes.items()) {
            finalList.remove(shape);
        }

        return Optional.of(new Shapes(HolderSet.direct(finalList)));
    }
}
