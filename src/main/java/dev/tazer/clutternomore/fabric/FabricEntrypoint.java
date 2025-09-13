package dev.tazer.clutternomore.fabric;

//? fabric {

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.event.CommonEvents;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.registry.CItems;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        CBlocks.register();
        CItems.register();
        ClutterNoMore.init();
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.STARTUP, CNMConfig.STARTUP_CONFIG);
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, CNMConfig.CLIENT_CONFIG);
        CommonEvents.registerPayloadHandlers();

    }

}
//?}