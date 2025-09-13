package dev.tazer.clutternomore.common.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;

@Mixin(value = RecipeManager.class, priority = 999)
public class RecipeManagerMixin {
    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    private boolean hasErrors;

    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Redirect(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V"))
    private void apply(Optional<WithConditions<Recipe<?>>> instance, Consumer<? super WithConditions<Recipe<?>>> action, Runnable emptyAction, @Local ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> byType, @Local ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> byName, @Local ResourceLocation location) {
        instance.ifPresentOrElse(r -> {
            Recipe<?> recipe = r.carrier();
            Item result = recipe.getResultItem(registries).getItem();

            if (INVERSE_SHAPES_DATAMAP.containsKey(result)) return;

            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : new ArrayList<>(ingredients)) {
                ArrayList<ItemStack> stacks = new ArrayList<>();
                for (ItemStack stack : ingredient.getItems()) {
                    Item item = stack.getItem();
                    if (INVERSE_SHAPES_DATAMAP.containsKey(item)) {
                        ItemStack originalStack = INVERSE_SHAPES_DATAMAP.get(item).getDefaultInstance();
                        originalStack.setCount(stack.getCount());
                        stacks.add(originalStack);
                    } else stacks.add(stack);
                }

                Stream<ItemStack> newStacks = stacks.stream();
                if (!newStacks.equals(Arrays.stream(ingredient.getItems()))) {
                    int index = ingredients.indexOf(ingredient);
                    ingredients.set(index, Ingredient.of(newStacks));
                }
            }

            RecipeHolder<?> recipeholder = new RecipeHolder<>(location, recipe);

            byType.put(recipe.getType(), recipeholder);
            byName.put(location, recipeholder);
        }, emptyAction);
    }

    @Inject(method = "replaceRecipes", at = @At("HEAD"), cancellable = true)
    private void replaceRecipes(Iterable<RecipeHolder<?>> recipes, CallbackInfo ci) {
        hasErrors = false;
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> recipeTypeBuilder = ImmutableMultimap.builder();
        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> recipeNameBuilder = ImmutableMap.builder();

        for (RecipeHolder<?> recipeHolder : recipes) {
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
                    } else stacks.add(stack);
                }

                Stream<ItemStack> newStacks = stacks.stream();
                if (!newStacks.equals(Arrays.stream(ingredient.getItems()))) {
                    int index = ingredients.indexOf(ingredient);
                    ingredients.set(index, Ingredient.of(newStacks));
                }
            }

            RecipeHolder<?> newHolder = new RecipeHolder<>(recipeHolder.id(), recipe);

            RecipeType<?> recipetype = recipe.getType();
            recipeTypeBuilder.put(recipetype, newHolder);
            recipeNameBuilder.put(newHolder.id(), newHolder);
        }

        byType = recipeTypeBuilder.build();
        byName = recipeNameBuilder.build();

        ci.cancel();
    }
}
