package dev.tazer.clutternomore.common.networking;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;

import java.util.List;

public record ShapeTooltip(List<Item> shapes, int selectedIndex) implements TooltipComponent {
}
