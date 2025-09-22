package dev.tazer.clutternomore.fabric;

//? fabric {

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import dev.tazer.clutternomore.common.shape_map.ShapeMapHandler;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.config.ModConfig;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        ClutterNoMore.init();
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.STARTUP, CNMConfig.STARTUP_CONFIG);
        NeoForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.CLIENT, CNMConfig.CLIENT_CONFIG);
        registerPayloadHandlers();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ShapeMapHandler());
//        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(((minecraftServer, closeableResourceManager) -> {
//            ClutterNoMore.load(minecraftServer.registryAccess(), minecraftServer.getRecipeManager());
//        }));
//        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
//            ClutterNoMore.load(minecraftServer.registryAccess(), minecraftServer.getRecipeManager());
//        });
    }

    public void registerPayloadHandlers() {
        PayloadTypeRegistry.playC2S().register(ChangeStackPayload.TYPE, ChangeStackPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ChangeStackPayload.TYPE, ChangeStackPayload::handleDataOnServer);
    }
}
//?}