package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.DatamapHandler.SHAPES_DATAMAP;

public class ShapeSwitcherOverlay {

    public final Minecraft minecraft;
    public final boolean render;
    public final int selected;
    public int count;
    public final List<Item> shapes;
    public int selectedIndex;
    public float currentIndex;

    public ShapeSwitcherOverlay(Minecraft minecraft, ItemStack heldStack, boolean render) {
        this.minecraft = minecraft;
        this.render = render;
        selected = minecraft.player.getInventory().selected;

        Item item = INVERSE_SHAPES_DATAMAP.getOrDefault(heldStack.getItem(), heldStack.getItem());
        count = heldStack.getCount();

        shapes = new ArrayList<>(SHAPES_DATAMAP.get(item));
        shapes.addFirst(item);

        selectedIndex = shapes.indexOf(heldStack.getItem());
        currentIndex = selectedIndex;
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {
        ResourceLocation background = ClutterNoMore.location("textures/gui/shape_background.png");
        ResourceLocation selected = ClutterNoMore.location("textures/gui/selected_shape.png");

        int y = guiGraphics.guiHeight() / 2 + 20;
        int centreX = guiGraphics.guiWidth() / 2 - 8;
        int spacing = 22;
        int startX;

        float smoothing = 1 - (float) Math.exp(-5 * partialTick);
        currentIndex = Mth.lerp(smoothing, currentIndex, selectedIndex);

        if (CNMConfig.SCROLLING.get()) {
            startX = Mth.floor(centreX - currentIndex * spacing);

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, y);
                RenderSystem.enableBlend();
                guiGraphics.blit(background, x, y, 0, 0, 16, 16, 16, 16);

            }

            RenderSystem.enableBlend();
            guiGraphics.blit(selected, centreX - 3, y - 3, 0, 0, 22, 22, 22, 22);
        } else {
            startX = Mth.floor(centreX - (float) shapes.size() / 2 * spacing) + spacing / 2;

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, y);
                RenderSystem.enableBlend();
                guiGraphics.blit(background, x, y, 0, 0, 16, 16, 16, 16);
            }

            RenderSystem.enableBlend();
            guiGraphics.blit(selected, Mth.floor(startX + currentIndex * spacing) - 3, y - 3, 0, 0, 22, 22, 22, 22);
        }

        RenderSystem.disableBlend();
    }

    public void onMouseScrolled(int direction) {
        int maxIndex = shapes.size() - 1;
        selectedIndex = selectedIndex - direction;
        if (selectedIndex < 0) selectedIndex = maxIndex;
        if (selectedIndex > maxIndex) selectedIndex = 0;

        Item nextItem = shapes.get(selectedIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        Player player = Objects.requireNonNull(minecraft.player);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
        player.setItemInHand(InteractionHand.MAIN_HAND, next);
        PacketDistributor.sendToServer(new ChangeStackPayload(-1, -1, next));
    }

    public boolean shouldStayOpenThisTick() {
        int selected = minecraft.player.getInventory().selected;
        ItemStack heldStack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        count = heldStack.getCount();
        return shapes.contains(heldStack.getItem()) && selected == this.selected;
    }
}
