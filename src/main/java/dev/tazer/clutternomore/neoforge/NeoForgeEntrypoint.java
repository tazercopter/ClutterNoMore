//? neoforge {
/*package dev.tazer.clutternomore.neoforge;

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ClutterNoMore.MODID)
public class NeoForgeEntrypoint {
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public NeoForgeEntrypoint(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        ClutterNoMore.init();
        modContainer.registerConfig(ModConfig.Type.STARTUP, CNMConfig.STARTUP_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, CNMConfig.CLIENT_CONFIG);

        if (dist.isClient()) {
            ClutterNoMoreClient.init();
        }
    }
}
*///?}