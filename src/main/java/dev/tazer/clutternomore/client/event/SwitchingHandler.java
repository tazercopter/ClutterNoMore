package dev.tazer.clutternomore.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.registry.CDataComponents;
import dev.tazer.clutternomore.networking.PlayerChangeStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

@EventBusSubscriber(modid = ClutterNoMore.MODID)
public class SwitchingHandler {
    private static final Map<UUID, Float> ROW_INDEX = new HashMap<>();
    private static final ResourceLocation LEFT_ARROW = ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "left_arrow");
    private static final ResourceLocation RIGHT_ARROW = ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "right_arrow");
    private static final ResourceLocation OUTLINE_SPRITE = ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "outline");

    private static ItemStack getActiveHeldStack() {
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) && Minecraft.getInstance().screen == null) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (heldStack.has(CDataComponents.SHAPES) || heldStack.has(CDataComponents.BLOCK)) {
                    return heldStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.MouseScrollingEvent event) {
        ItemStack heldStack = getActiveHeldStack();
        if (heldStack.isEmpty()) return;
        event.setCanceled(true);

        ItemStack stack = heldStack.getOrDefault(CDataComponents.BLOCK, heldStack.getItem()).getDefaultInstance();
        List<Item> shapes = new ArrayList<>(Objects.requireNonNull(stack.get(CDataComponents.SHAPES)));

        int originalIndex = shapes.size() / 2;
        shapes.add(originalIndex, stack.getItem());

        int scroll = (int) event.getScrollDeltaY();
        int maxIndex = shapes.size() - 1;
        int nextIndex = shapes.indexOf(heldStack.getItem()) + scroll;
        if (nextIndex < 0) nextIndex = maxIndex;
        if (nextIndex > maxIndex) nextIndex = 0;

        int count = heldStack.getCount();

        Item nextItem = shapes.get(nextIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        PacketDistributor.sendToServer(new PlayerChangeStack(next));
    }

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiEvent.Post event) {
        ItemStack heldStack = getActiveHeldStack();
        if (heldStack.isEmpty()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        ItemStack stack = heldStack.getOrDefault(CDataComponents.BLOCK, heldStack.getItem()).getDefaultInstance();

        List<Item> shapes = new ArrayList<>(Objects.requireNonNull(stack.get(CDataComponents.SHAPES)));

        int originalIndex = shapes.size() / 2;
        shapes.add(originalIndex, stack.getItem());

        int targetIndex = shapes.indexOf(heldStack.getItem());

        int centreX = guiGraphics.guiWidth() / 2 - 8;
        int baseY = guiGraphics.guiHeight() / 2 + 30;

        UUID id = Minecraft.getInstance().player.getUUID();
        float dt = Mth.clamp(event.getPartialTick().getRealtimeDeltaTicks(), 0, 1.5F);
        Float smoothed = ROW_INDEX.get(id);
        float currentIndex = smoothed == null ? targetIndex : smoothed;
        float smoothing = 1 - (float) Math.exp(-10 * dt);
        currentIndex = Mth.lerp(smoothing, currentIndex, targetIndex);
        ROW_INDEX.put(id, currentIndex);

        int startX = Mth.floor(centreX - currentIndex * 20);
        for (int i = 0; i < shapes.size(); i++) {
            int x = startX + i * 20;
            float raiseFactor = 1 - Mth.clamp(Math.abs(i - currentIndex), 0, 1);
            int y = baseY - Mth.floor(5 * raiseFactor);
            guiGraphics.renderItem(shapes.get(i).getDefaultInstance(), x, y);
        }

        int selectedSnap = Mth.floor(currentIndex + 0.5F);
        int xSelected = startX + selectedSnap * 20;
        float raiseSelected = 1 - Mth.clamp(Math.abs(currentIndex - selectedSnap), 0, 1);
        int ySelected = baseY - Mth.floor(5 * raiseSelected);

        guiGraphics.blitSprite(LEFT_ARROW, xSelected - 5, ySelected, 8, 8);
        guiGraphics.blitSprite(RIGHT_ARROW, xSelected + 13, ySelected, 8, 8);
    }
}
