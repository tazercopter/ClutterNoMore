package dev.tazer.clutternomore;

import com.mojang.logging.LogUtils;
import dev.tazer.clutternomore.client.event.SwitchingHandler;
import dev.tazer.clutternomore.common.registry.CDataComponents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ClutterNoMore.MODID)
public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ClutterNoMore(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        CDataComponents.COMPONENTS.register(modEventBus);
    }
}