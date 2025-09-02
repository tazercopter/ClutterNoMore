package dev.tazer.clutternomore.client;

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import static dev.tazer.clutternomore.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.event.DatamapHandler.SHAPES_DATAMAP;

@EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static ShapeSwitcherOverlay OVERLAY = null;

    public static final Lazy<KeyMapping> KEY_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    ));

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KEY_MAPPING.get());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        int action = event.getAction();
        if (event.getKey() == KEY_MAPPING.get().getKey().getValue()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen == null) {
                Player player = minecraft.player;
                if (player != null) {
                    ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (SHAPES_DATAMAP.containsKey(heldStack.getItem()) || INVERSE_SHAPES_DATAMAP.containsKey(heldStack.getItem())) {
                        switch (CNMConfig.HOLD.get()) {
                            case HOLD -> {
                                if (OVERLAY == null && action == 1)
                                    OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, true);
                                else if (action == 0) OVERLAY = null;
                            }
                            case TOGGLE -> {
                                if (action == 1) {
                                    if (OVERLAY == null) OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, true);
                                    else OVERLAY = null;
                                }
                            }
                            case PRESS -> {
                                if (action == 1) {
                                    if (OVERLAY == null)
                                        OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, false);
                                    OVERLAY.onMouseScrolled(-1);
                                    OVERLAY = null;
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled((int) event.getScrollDeltaY());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (OVERLAY != null && OVERLAY.render) {
            OVERLAY.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaTicks());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (OVERLAY != null) {
            if (!OVERLAY.shouldStayOpenTick()) OVERLAY = null;
        }
    }
}
