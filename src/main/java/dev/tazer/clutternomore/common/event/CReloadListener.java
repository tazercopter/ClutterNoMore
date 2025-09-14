package dev.tazer.clutternomore.common.event;

//? if neoforge {

/*import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeManager;

public record CReloadListener(ReloadableServerResources resources) implements ResourceManagerReloadListener{

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        HolderLookup.Provider registries = resources.getRegistryLookup();
        RecipeManager recipeManager = resources.getRecipeManager();
        ClutterNoMore.load(registries, recipeManager);
    }
}
*///?}