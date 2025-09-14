package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.assets.DynamicClientResources;

public class ClutterNoMoreClient {
    public static boolean showTooltip = false;

    public static void init() {
        DynamicClientResources.register();
    }
}
