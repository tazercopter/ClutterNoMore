package dev.tazer.clutternomore.fabric;

//? if fabric {
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.common.mixin.screen.ScreenAccessor;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.DatamapHandler.SHAPES_DATAMAP;


public class ClientEvents {

    private static ShapeSwitcherOverlay OVERLAY = null;
    private static boolean showTooltip = false;

    public static final Supplier<KeyMapping> SHAPE_KEY = () -> new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    );


    public static void registerKeyMappings() {
        KeyBindingHelper.registerKeyBinding(SHAPE_KEY.get());
    }


    public static ClientShapeTooltip registerTooltipComponent(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof ShapeTooltip shapeTooltip) {
            return new ClientShapeTooltip(shapeTooltip);
        }
        return null;
    }


    public static void onItemTooltips(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltip) {
        Item item = stack.getItem();
        boolean hasShapes = SHAPES_DATAMAP.containsKey(item);
        boolean isShape = INVERSE_SHAPES_DATAMAP.containsKey(item);

        if (!showTooltip) {
            if (hasShapes || isShape) {
                Component component = tooltip.getFirst().copy().append(Component.literal(" [+]").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.removeFirst();
                tooltip.addFirst(component);
            }
        }
    }


//    public static void onRenderTooltip(RenderTooltipEvent.GatherComponents event) {
//        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
//        for (Either<FormattedText, TooltipComponent> element : new ArrayList<>(tooltipElements)) {
//            element.ifRight(tooltipComponent -> {
//                if (tooltipComponent instanceof ShapeTooltip) {
//                    if (!showTooltip) tooltipElements.remove(element);
//                }
//            });
//        }
//    }

    public static void onKeyInput(int keyCode, int keyAction) {
        if (keyCode == SHAPE_KEY.get().key.getValue()) {
            onKeyInput(keyAction);
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


    public static void onScreenScroll(Screen pScreen, double mouseX, double mouseY, double scrollX, double scrollY) {
        if (showTooltip) {
            if (pScreen instanceof AbstractContainerScreen<?> screen) {
                Slot slot = ((ScreenAccessor) screen).getSlotUnderMouse();
                if (slot != null) {
                    ItemStack heldStack = slot.getItem();
                    if (SHAPES_DATAMAP.containsKey(heldStack.getItem()) || INVERSE_SHAPES_DATAMAP.containsKey(heldStack.getItem())) {
                        switchShapeInSlot(Minecraft.getInstance().player, screen.getMenu().containerId, slot.index, heldStack, (int) scrollY);
                    }
                }
            }
        }
    }


    public static void onScreenInputKeyPressedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (keyCode == SHAPE_KEY.get().key.getValue()) {
            onKeyPress(screen);
        }
    }


    public static void onScreenInputMouseButtonPressedPost(Screen screen, double mouseX, double mouseY, int button) {
        if (button == SHAPE_KEY.get().key.getValue()) {
            onKeyPress(screen);
        }
    }


    public static void onScreenInputKeyReleasedPost(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (keyCode == SHAPE_KEY.get().key.getValue()) {
            onKeyPress(screen);
        }
    }


    public static void onScreenInputMouseButtonReleasedPost(Screen screen, double mouseX, double mouseY, int button) {
        if (button == SHAPE_KEY.get().key.getValue()) {
            onKeyRelease();
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

    public static void onKeyInput(int action) {
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

    public static void onKeyPress(Screen screen) {
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            Slot slot = ((ScreenAccessor) containerScreen).getSlotUnderMouse();
            if (slot != null) {
                ItemStack heldStack = slot.getItem();

                if (SHAPES_DATAMAP.containsKey(heldStack.getItem()) || INVERSE_SHAPES_DATAMAP.containsKey(heldStack.getItem())) {
                    switch (CNMConfig.HOLD.get()) {
                        case HOLD -> showTooltip = true;
                        case TOGGLE -> showTooltip = !showTooltip;
                        case PRESS -> switchShapeInSlot(
                                Minecraft.getInstance().player,
                                containerScreen.getMenu().containerId,
                                slot.index,
                                heldStack,
                                -1
                        );
                    }
                }
            }
        }
    }

    public static void onKeyRelease() {
        if (CNMConfig.HOLD.get() == CNMConfig.InputType.HOLD) {
            showTooltip = false;
        }
    }

    public static void switchShapeInSlot(Player player, int containerId, int slotId, ItemStack heldStack, int direction) {
        Item item = INVERSE_SHAPES_DATAMAP.getOrDefault(heldStack.getItem(), heldStack.getItem());
        int count = heldStack.getCount();

        List<Item> shapes = new ArrayList<>(SHAPES_DATAMAP.get(item));
        shapes.addFirst(item);
        int selectedIndex = shapes.indexOf(heldStack.getItem());

        int maxIndex = shapes.size() - 1;
        selectedIndex = selectedIndex - direction;
        if (selectedIndex < 0) selectedIndex = maxIndex;
        if (selectedIndex > maxIndex) selectedIndex = 0;

        Item nextItem = shapes.get(selectedIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
        if (slotId < 9) slotId += 36;
        ClientPlayNetworking.send(new ChangeStackPayload(containerId, slotId, next));
    }
}
//?}