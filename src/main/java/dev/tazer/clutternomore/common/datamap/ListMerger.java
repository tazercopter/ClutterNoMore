package dev.tazer.clutternomore.common.datamap;
//? if neoforge {
/*import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;

import java.util.ArrayList;
import java.util.List;

public class ListMerger implements DataMapValueMerger<Item, Shapes> {
    @Override
    public Shapes merge(Registry<Item> registry, Either<TagKey<Item>, ResourceKey<Item>> firstKey, Shapes firstList, Either<TagKey<Item>, ResourceKey<Item>> secondKey, Shapes secondList) {
        List<Holder<Item>> finalList = new ArrayList<>(firstList.items().stream().toList());
        finalList.addAll(secondList.items().stream().toList());
        return new Shapes(HolderSet.direct(finalList));
    }
}
*///?}