package dev.tazer.clutternomore.fabric;

//? if fabric {

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.common.mixin.SlotAccessor;
import dev.tazer.clutternomore.common.mixin.screen.ScreenAccessor;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static dev.tazer.clutternomore.ClutterNoMoreClient.OVERLAY;
import static dev.tazer.clutternomore.ClutterNoMoreClient.showTooltip;
import static dev.tazer.clutternomore.common.event.ShapeMapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.ShapeMapHandler.SHAPES_DATAMAP;


public class ClientEvents {

    public static final KeyMapping SHAPE_KEY = new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    );


    public static void registerKeyMappings() {
        KeyBindingHelper.registerKeyBinding(SHAPE_KEY);
    }

    public static ClientShapeTooltip registerTooltipComponent(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof ShapeTooltip shapeTooltip) {
            return new ClientShapeTooltip(shapeTooltip);
        }
        return null;
    }

    public static void onKeyInput(int keyCode, int keyAction) {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyInput(keyAction);
        }
    }

    public static boolean onMouseScrolling(double yOffset) {
        int direction = (int) yOffset;
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled(direction);
            return true;
        }
        return false;
    }

    public static boolean allowScreenScroll(Screen pScreen, double mouseX, double mouseY, double scrollX, double scrollY) {
        if (showTooltip) {
            if (pScreen instanceof AbstractContainerScreen<?> screen) {
                Slot slot = ((ScreenAccessor) screen).getSlotUnderMouse();
                Player player = Minecraft.getInstance().player;
                if (slot != null && slot.allowModification(player)) {
                    ItemStack heldStack = slot.getItem();
                    if (SHAPES_DATAMAP.containsKey(heldStack.getItem()) || INVERSE_SHAPES_DATAMAP.containsKey(heldStack.getItem())) {
                        switchShapeInSlot(player, screen.getMenu().containerId, ((SlotAccessor) slot).getSlotIndex(), heldStack, (int) scrollY);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static void onScreenInputKeyPressedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyPress(screen);
        }
    }

    public static void onScreenInputMouseButtonPressedPost(Screen screen, double mouseX, double mouseY, int button) {
        if (button == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyPress(screen);
        }
    }

    public static void onScreenInputKeyReleasedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyRelease();
        }
    }

    public static void onScreenInputMouseButtonReleasedPost(Screen screen, double mouseX, double mouseY, int button) {
        if (button == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyRelease();
        }
    }

    public static void onRenderGui(GuiGraphics guiGraphics, DeltaTracker tracker) {
        if (OVERLAY != null && OVERLAY.render) {
            OVERLAY.render(guiGraphics, tracker.getGameTimeDeltaTicks());
        }
    }

    public static void onPlayerTick(Minecraft minecraft) {
        if (OVERLAY != null) {
            if (!OVERLAY.shouldStayOpenThisTick()) OVERLAY = null;
        }
    }

    public static void switchShapeInSlot(Player player, int containerId, int slotId, ItemStack heldStack, int direction) {
        ClientPlayNetworking.send(ClutterNoMoreClient.switchShapeInSlot(player, containerId, slotId, heldStack, direction));
    }
}
//?}