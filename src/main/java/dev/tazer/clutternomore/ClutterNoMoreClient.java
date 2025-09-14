package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.client.assets.DynamicClientResources;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.mixin.SlotAccessor;
import dev.tazer.clutternomore.common.mixin.screen.ScreenAccessor;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
//? fabric
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? neoforge
/*import net.neoforged.neoforge.network.PacketDistributor;*/

import java.util.ArrayList;
import java.util.List;

public class ClutterNoMoreClient {
    public static boolean showTooltip = false;
    public static ShapeSwitcherOverlay OVERLAY = null;

    public static void init() {
        DynamicClientResources.register();
    }

    public static void onItemTooltips(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltip) {
        if (!showTooltip) {
            if (ShapeMap.contains(stack.getItem())) {
                Component component = tooltip.getFirst().copy().append(Component.literal(" [+]").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.removeFirst();
                tooltip.addFirst(component);
            }
        }
    }

    public static void onKeyInput(int action) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen == null) {
            Player player = minecraft.player;
            if (player != null) {
                ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (ShapeMap.contains(heldStack.getItem())) {
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
            Slot slot = ((ScreenAccessor) screen).getSlotUnderMouse();
            if (slot != null) {
                ItemStack heldStack = slot.getItem();
                //? neoforge
                /*Player player = screen.getMinecraft().player;*/
                //? fabric
                Player player = Minecraft.getInstance().player;

                if (slot.allowModification(player) && (ShapeMap.contains(heldStack.getItem()))) {
                    switch (CNMConfig.HOLD.get()) {
                        case HOLD -> showTooltip = true;
                        case TOGGLE -> showTooltip = !showTooltip;
                        case PRESS -> switchShapeInSlot(
                                player,
                                containerScreen.getMenu().containerId,
                                ((SlotAccessor) slot).getSlotIndex(),
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
        Item item = ShapeMap.getParent(heldStack.getItem());
        int count = heldStack.getCount();

        List<Item> shapes = new ArrayList<>(ShapeMap.getShapes(item));
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
        var p = new ChangeStackPayload(containerId, slotId, next);
        //? fabric
        ClientPlayNetworking.send(p);
        //? neoforge
        /*PacketDistributor.sendToServer(p);*/
    }
}
