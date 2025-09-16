package dev.tazer.clutternomore;

//import dev.tazer.clutternomore.common.data.DynamicServerResources;
import dev.tazer.clutternomore.common.access.RegistryAccess;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
        BlockSetRegistry.init();
//        DynamicServerResources.register();
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void load(HolderLookup.Provider registries, RecipeManager recipeManager) {
        //FIXME 1.21.8
        //? if <1.21.2 {
        /*boolean changed = false;
        Collection<RecipeHolder<?>> originalRecipes = recipeManager.getRecipes();
        List<RecipeHolder<?>> newRecipes = new ArrayList<>();

        for (RecipeHolder<?> recipeHolder : originalRecipes) {
            Recipe<?> recipe = recipeHolder.value();

            Item result = recipe.getResultItem(registries).getItem();

            if (ShapeMap.isShape(result)) continue;

            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : new ArrayList<>(ingredients)) {
                ArrayList<ItemStack> stacks = new ArrayList<>();
                for (ItemStack stack : ingredient.getItems()) {
                    Item item = stack.getItem();
                    if (ShapeMap.isShape(item)) {
                        ItemStack originalStack = ShapeMap.getParent(item).getDefaultInstance();
                        originalStack.setCount(stack.getCount());
                        stacks.add(originalStack);
                        changed = true;
                    } else stacks.add(stack);
                }

                Stream<ItemStack> newStacks = stacks.stream();
                if (changed) {
                    try {
                        int index = ingredients.indexOf(ingredient);
                        ingredients.set(index, Ingredient.of(newStacks));
                    } catch (Exception ignored) {}
                }
            }

            RecipeHolder<?> newHolder = new RecipeHolder<>(recipeHolder.id(), recipe);
            newRecipes.add(newHolder);
        }

        if (changed) {
            recipeManager.replaceRecipes(newRecipes);
        }
        *///?}
    }

    public static void registerVariants() {
        if (CNMConfig.VERTICAL_SLABS.get() || CNMConfig.STEPS.get()) {
            //? if neoforge {
            ((RegistryAccess) BuiltInRegistries.BLOCK).clutternomore$unfreeze();
            ((RegistryAccess) BuiltInRegistries.ITEM).clutternomore$unfreeze();
            //?}
            LinkedHashMap<String, Supplier<? extends Block>> toRegister = new LinkedHashMap<>();
            for (Map.Entry<ResourceKey<Item>, Item> resourceKeyItemEntry : BuiltInRegistries.ITEM.entrySet()) {
                if (resourceKeyItemEntry.getValue().asItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock() instanceof SlabBlock slabBlock && CNMConfig.VERTICAL_SLABS.get()) {
                        var path = resourceKeyItemEntry.getKey().location().getPath().replace("slab", "vertical_slab");
                        toRegister.put(path, ()->new VerticalSlabBlock(BlockBehaviour.Properties.ofFullCopy(slabBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));
                    }
                    if (blockItem.getBlock() instanceof StairBlock stairBlock && CNMConfig.STEPS.get()) {
                        var path = resourceKeyItemEntry.getKey().location().getPath().replace("stair", "step");
                        toRegister.put(path, ()->new StepBlock(BlockBehaviour.Properties.ofFullCopy(stairBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));
                    }
                }
            }
            toRegister.forEach(CBlocks::register);
            //? if neoforge {
            BuiltInRegistries.BLOCK.freeze();
            BuiltInRegistries.ITEM.freeze();
            //?}
        }
    }
}