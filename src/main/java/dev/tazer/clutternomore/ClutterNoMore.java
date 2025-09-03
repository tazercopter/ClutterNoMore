package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.assets.DynamicClientResources;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import dev.tazer.clutternomore.common.data.DynamicServerResources;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.registry.CItems;
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
        BlockSetRegistry.init();
        modContainer.registerConfig(ModConfig.Type.STARTUP, CNMConfig.STARTUP_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, CNMConfig.CLIENT_CONFIG);
        DynamicServerResources.INSTANCE.register();

        if (dist.isClient()) {
            DynamicClientResources.INSTANCE.register();
        }
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}