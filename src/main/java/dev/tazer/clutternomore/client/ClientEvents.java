package dev.tazer.clutternomore.client;

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.registry.CDataComponents;
import dev.tazer.clutternomore.networking.ChangeStackPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.*;

@EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static final Map<UUID, Float> ROW_INDEX = new HashMap<>();

    public static final Lazy<KeyMapping> KEY_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    ));

    private static ItemStack getActiveHeldStack() {
        boolean activated = CNMConfig.HOLD.get() ? KEY_MAPPING.get().isDown() : KEY_MAPPING.get().consumeClick();

        if (activated && Minecraft.getInstance().screen == null) {
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
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KEY_MAPPING.get());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getKey() == KEY_MAPPING.get().getKey().getValue()) {

        }
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        ItemStack heldStack = getActiveHeldStack();
        if (heldStack.isEmpty()) return;
        event.setCanceled(true);

        ItemStack stack = heldStack.getOrDefault(CDataComponents.BLOCK, heldStack.getItem()).getDefaultInstance();
        List<Item> shapes = new ArrayList<>(Objects.requireNonNull(stack.get(CDataComponents.SHAPES)));

        shapes.addFirst(stack.getItem());

        int scroll = (int) event.getScrollDeltaY();
        int maxIndex = shapes.size() - 1;
        int nextIndex = shapes.indexOf(heldStack.getItem()) - scroll;
        if (nextIndex < 0) nextIndex = maxIndex;
        if (nextIndex > maxIndex) nextIndex = 0;

        int count = heldStack.getCount();

        Item nextItem = shapes.get(nextIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        Player player = Objects.requireNonNull(Minecraft.getInstance().player);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
        player.setItemInHand(InteractionHand.MAIN_HAND, next);
        PacketDistributor.sendToServer(new ChangeStackPayload(next));
    }

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiEvent.Post event) {
        ItemStack heldStack = getActiveHeldStack();
        if (heldStack.isEmpty()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        ItemStack stack = heldStack.getOrDefault(CDataComponents.BLOCK, heldStack.getItem()).getDefaultInstance();

        List<Item> shapes = new ArrayList<>(Objects.requireNonNull(stack.get(CDataComponents.SHAPES)));

        boolean alternative = !CNMConfig.SCROLLING.get();

        shapes.addFirst(stack.getItem());
        int selectedIndex = shapes.indexOf(heldStack.getItem());

        UUID id = Minecraft.getInstance().player.getUUID();
        float deltaTicks = Mth.clamp(event.getPartialTick().getRealtimeDeltaTicks(), 0, 1.5F);
        float smoothing = 1 - (float) Math.exp(-10 * deltaTicks);

        int y = guiGraphics.guiHeight() / 2 + 20;
        int centreX = guiGraphics.guiWidth() / 2 - 8;
        int spacing = 22;


        Float smoothed = ROW_INDEX.get(id);
        float currentIndex = smoothed == null ? selectedIndex : smoothed;
        currentIndex = Mth.lerp(smoothing, currentIndex, selectedIndex);
        ROW_INDEX.put(id, currentIndex);
        int startX;

        ResourceLocation background = ClutterNoMore.location("textures/gui/shape_background.png");
        ResourceLocation selected = ClutterNoMore.location("textures/gui/selected_shape.png");

        if (alternative) {
            startX = Mth.floor(centreX - (float) shapes.size() / 2 * spacing) + spacing / 2;

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;

                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, y);
                guiGraphics.blit(background, x, y, 0, 0, 16, 16, 16, 16);
            }

            guiGraphics.blit(selected, Mth.floor(startX + currentIndex * spacing) - 3, y - 3, 0, 0, 22, 22, 22, 22);
        } else {
            startX = Mth.floor(centreX - currentIndex * spacing);

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, y);
                guiGraphics.blit(background, x, y, 0, 0, 16, 16, 16, 16);
            }

            guiGraphics.blit(selected, centreX - 3, y - 3, 0, 0, 22, 22, 22, 22);
        }
    }
}
