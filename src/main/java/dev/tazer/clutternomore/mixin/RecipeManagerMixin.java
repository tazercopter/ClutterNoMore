package dev.tazer.clutternomore.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.tazer.clutternomore.registry.CDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Map;

import static dev.tazer.clutternomore.event.CommonEvents.INVERSE_SHAPES_DATAMAP;

@Mixin(value = RecipeManager.class)
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

    @Inject(method = "replaceRecipes", at = @At("HEAD"), cancellable = true)
    private void replaceRecipes(Iterable<RecipeHolder<?>> recipes, CallbackInfo ci) {
        hasErrors = false;
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> builder = ImmutableMultimap.builder();
        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> builder1 = ImmutableMap.builder();

        for(RecipeHolder<?> recipeholder : recipes) {
            Recipe<?> recipe = recipeholder.value();
            if (INVERSE_SHAPES_DATAMAP.containsKey(recipe.getResultItem(registries).getItem())) continue;
            recipe.getIngredients().replaceAll(ingredient -> Ingredient.of(
                    Arrays.stream(ingredient.getItems()).map(stack -> {
                        if (INVERSE_SHAPES_DATAMAP.containsKey(stack.getItem())) {
                            ItemStack originalStack = INVERSE_SHAPES_DATAMAP.get(stack.getItem()).getDefaultInstance();
                            originalStack.setCount(stack.getCount());
                            return originalStack;
                        }
                        return stack;
                    }).toArray(ItemStack[]::new)
            ));

            RecipeType<?> recipetype = recipeholder.value().getType();
            builder.put(recipetype, recipeholder);
            builder1.put(recipeholder.id(), recipeholder);
        }

        byType = builder.build();
        byName = builder1.build();

        ci.cancel();
    }
}
