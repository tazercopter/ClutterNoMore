package dev.tazer.clutternomore;

import com.mojang.logging.LogUtils;
import dev.tazer.clutternomore.registry.CDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ClutterNoMore.MODID)
public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ClutterNoMore(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        CDataComponents.COMPONENTS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.CLIENT, CNMConfig.CLIENT_CONFIG);
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}