package dev.tazer.clutternomore.fabric;

//? fabric {
import dev.tazer.clutternomore.Platform;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformImpl implements Platform {

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public String loader() {
        return "fabric";
    }

}
//?}