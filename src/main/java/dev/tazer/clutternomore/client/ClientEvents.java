package dev.tazer.clutternomore.client;

import com.mojang.datafixers.util.Either;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.DatamapHandler.SHAPES_DATAMAP;

@EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static ShapeSwitcherOverlay OVERLAY = null;
    private static boolean showTooltip = false;

    public static final Lazy<KeyMapping> SHAPE_KEY = Lazy.of(() -> new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    ));

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SHAPE_KEY.get());
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ShapeTooltip.class, ClientShapeTooltip::new);
    }

    @SubscribeEvent
    public static void onItemTooltips(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        List<Component> tooltip = event.getToolTip();
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

    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        for (Either<FormattedText, TooltipComponent> element : new ArrayList<>(tooltipElements)) {
            element.ifRight(tooltipComponent -> {
                if (tooltipComponent instanceof ShapeTooltip) {
                    if (!showTooltip) tooltipElements.remove(element);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        int action = event.getAction();
        if (event.getKey() == SHAPE_KEY.get().getKey().getValue()) {
            onKeyInput(action);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.MouseButton.Post event) {
        int action = event.getAction();
        if (event.getButton() == SHAPE_KEY.get().getKey().getValue()) {
            onKeyInput(action);
        }
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        int direction = (int) event.getScrollDeltaY();
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled(direction);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onScreenScroll(ScreenEvent.MouseScrolled.Post event) {
        if (showTooltip) {
            if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
                Slot slot = screen.getSlotUnderMouse();
                if (slot != null) {
                    ItemStack heldStack = slot.getItem();
                    if (SHAPES_DATAMAP.containsKey(heldStack.getItem()) || INVERSE_SHAPES_DATAMAP.containsKey(heldStack.getItem())) {
                        switchShapeInSlot(screen.getMinecraft().player, screen.getMenu().containerId, slot.getSlotIndex(), heldStack, (int) event.getScrollDeltaY());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.KeyPressed.Post event) {
        if (event.getKeyCode() == SHAPE_KEY.get().getKey().getValue()) {
            onKeyPress(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.MouseButtonPressed.Post event) {
        if (event.getButton() == SHAPE_KEY.get().getKey().getValue()) {
            onKeyPress(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.KeyReleased.Post event) {
        if (event.getKeyCode() == SHAPE_KEY.get().getKey().getValue()) {
            onKeyRelease();
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.MouseButtonReleased.Post event) {
        if (event.getButton() == SHAPE_KEY.get().getKey().getValue()) {
            onKeyRelease();
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
            Slot slot = containerScreen.getSlotUnderMouse();
            if (slot != null) {
                ItemStack heldStack = slot.getItem();

                if (SHAPES_DATAMAP.containsKey(heldStack.getItem()) || INVERSE_SHAPES_DATAMAP.containsKey(heldStack.getItem())) {
                    switch (CNMConfig.HOLD.get()) {
                        case HOLD -> showTooltip = true;
                        case TOGGLE -> showTooltip = !showTooltip;
                        case PRESS -> switchShapeInSlot(
                                screen.getMinecraft().player,
                                containerScreen.getMenu().containerId,
                                slot.getSlotIndex(),
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
        PacketDistributor.sendToServer(new ChangeStackPayload(containerId, slotId, next));
    }
}
