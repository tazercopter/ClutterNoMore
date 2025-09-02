package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.assets.CDynamicResources;
import dev.tazer.clutternomore.registry.CBlockSet;
import dev.tazer.clutternomore.registry.CBlocks;
import dev.tazer.clutternomore.registry.CItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ClutterNoMore.MODID)
public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public ClutterNoMore(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        CBlocks.BLOCKS.register(modEventBus);
        CItems.ITEMS.register(modEventBus);
        CBlockSet.init();
        modContainer.registerConfig(ModConfig.Type.CLIENT, CNMConfig.CLIENT_CONFIG);

        if (dist.isClient()) {
            CDynamicResources.init();
        }
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}