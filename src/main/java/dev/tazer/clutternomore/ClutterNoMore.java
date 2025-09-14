package dev.tazer.clutternomore;

import dev.tazer.clutternomore.common.data.DynamicServerResources;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static dev.tazer.clutternomore.common.event.ShapeMapHandler.INVERSE_SHAPES_DATAMAP;

public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
        BlockSetRegistry.init();
        DynamicServerResources.register();
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void load(HolderLookup.Provider registries, RecipeManager recipeManager) {
        boolean changed = false;
        List<RecipeHolder<?>> originalRecipes = new ArrayList<>(recipeManager.getRecipes());
        List<RecipeHolder<?>> newRecipes = new ArrayList<>();

        for (RecipeHolder<?> recipeHolder : originalRecipes) {
            Recipe<?> recipe = recipeHolder.value();

            Item result = recipe.getResultItem(registries).getItem();

            if (INVERSE_SHAPES_DATAMAP.containsKey(result)) continue;

            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : new ArrayList<>(ingredients)) {
                ArrayList<ItemStack> stacks = new ArrayList<>();
                for (ItemStack stack : ingredient.getItems()) {
                    Item item = stack.getItem();
                    if (INVERSE_SHAPES_DATAMAP.containsKey(item)) {
                        ItemStack originalStack = INVERSE_SHAPES_DATAMAP.get(item).getDefaultInstance();
                        originalStack.setCount(stack.getCount());
                        stacks.add(originalStack);
                        changed = true;
                    } else stacks.add(stack);
                }

                Stream<ItemStack> newStacks = stacks.stream();
                if (changed) {
                    int index = ingredients.indexOf(ingredient);
                    ingredients.set(index, Ingredient.of(newStacks));
                }
            }

            RecipeHolder<?> newHolder = new RecipeHolder<>(recipeHolder.id(), recipe);
            newRecipes.add(newHolder);
        }

        if (changed) {
            recipeManager.replaceRecipes(newRecipes);
        }
    }
}