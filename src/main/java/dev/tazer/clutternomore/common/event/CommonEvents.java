package dev.tazer.clutternomore.common.event;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.registry.CItems;
import net.minecraft.core.registries.Registries;
//? neoforge {
/*import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;


@EventBusSubscriber(modid = ClutterNoMore.MODID)
*///?} else {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import static dev.tazer.clutternomore.ClutterNoMore.MODID;
//?}

public class CommonEvents {

    //? neoforge {
    /*@SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ChangeStackPayload.TYPE,
                ChangeStackPayload.STREAM_CODEC,
                ChangeStackPayload::handleDataOnServer
        );
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new CReloadListener(event.getServerResources()));
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            CItems.register();
        } else if (event.getRegistryKey().equals(Registries.BLOCK)) {
            CBlocks.register();
        }
    }
    *///?} else {
    public static void registerPayloadHandlers() {
        PayloadTypeRegistry.playC2S().register(ChangeStackPayload.TYPE, ChangeStackPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ChangeStackPayload.TYPE, ChangeStackPayload::handleDataOnServer);
    }

    public static void addReloadListeners() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(ResourceLocation.fromNamespaceAndPath(MODID, "data"), CReloadListener::new);
    }
    //?}
}
