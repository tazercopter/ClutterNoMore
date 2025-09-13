package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import org.joml.Matrix4f;

import java.util.List;

public class ClientShapeTooltip implements ClientTooltipComponent {
    public final List<Item> shapes;
    public int selectedIndex;

    public ClientShapeTooltip(ShapeTooltip shapeTooltip) {
        shapes = shapeTooltip.shapes();
        selectedIndex = shapeTooltip.selectedIndex();
    }

    @Override
    public int getHeight() {
        return 22;
    }

    @Override
    public int getWidth(Font font) {
        return shapes.size() * 22;
    }

    @Override
    public void renderText(Font font, int mouseX, int mouseY, Matrix4f matrix, MultiBufferSource.BufferSource bufferSource) {
        ClientTooltipComponent.super.renderText(font, mouseX, mouseY, matrix, bufferSource);
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics guiGraphics) {
        ResourceLocation selected = ClutterNoMore.location("textures/gui/selected_shape_inventory.png");

        int spacing = 22;
        int startX = mouseX + 2;

        for (int index = 0; index < shapes.size(); index++) {
            int x = startX + index * spacing;
            guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, mouseY);
        }

        RenderSystem.enableBlend();
        guiGraphics.blit(selected, Mth.floor(startX + selectedIndex * spacing) - 3, mouseY - 3, 0, 0, 22, 22, 22, 22);
        RenderSystem.disableBlend();
    }
}
