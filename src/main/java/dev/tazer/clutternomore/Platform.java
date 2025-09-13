package dev.tazer.clutternomore;

//? fabric {
import dev.tazer.clutternomore.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import dev.tazer.clutternomore.neoforge.NeoForgePlatformImpl;
*///?}




public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoForgePlatformImpl();
    *///?}


    boolean isModLoaded(String modid);
    String loader();

}
