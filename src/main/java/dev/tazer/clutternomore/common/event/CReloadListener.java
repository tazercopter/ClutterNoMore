package dev.tazer.clutternomore.common.event;

import dev.tazer.clutternomore.ClutterNoMore;
//? if fabric
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;

public record CReloadListener
        //? if neoforge {
        /*(ReloadableServerResources resources) implements ResourceManagerReloadListener
        *///?} else {
        (HolderLookup.Provider registries) implements IdentifiableResourceReloadListener, ResourceManagerReloadListener
        //?}
{


    //? if fabric {

    @Override
    public ResourceLocation getFabricId() {
        return ClutterNoMore.location("data");
    }
    //?}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        boolean changed = false;

        //? if neoforge {
        /*HolderLookup.Provider registries = resources.getRegistryLookup();
        RecipeManager recipeManager = resources.getRecipeManager();
        *///?} else {
        //FIXME
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        //?}
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
