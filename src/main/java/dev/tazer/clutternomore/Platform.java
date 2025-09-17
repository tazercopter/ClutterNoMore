package dev.tazer.clutternomore;

//? fabric {

import dev.tazer.clutternomore.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import dev.tazer.clutternomore.neoforge.NeoForgePlatformImpl;
*///?}
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;
import com.google.gson.JsonObject;


public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoForgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

    Path getResourcePack();

    JsonObject getFileInJar(String namespace, String path);
}
