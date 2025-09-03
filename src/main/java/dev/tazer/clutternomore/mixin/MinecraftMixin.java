package dev.tazer.clutternomore.mixin;

import dev.tazer.clutternomore.networking.ChangeStackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static dev.tazer.clutternomore.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.event.DatamapHandler.SHAPES_DATAMAP;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public abstract ClientPacketListener getConnection();

    @Redirect(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int pickBlock(Inventory inventory, ItemStack targetStack) {
        int exactIndex = inventory.findSlotMatchingItem(targetStack);

        if (exactIndex != -1) {
            Item targetItem = targetStack.getItem();
            Item originalItem = INVERSE_SHAPES_DATAMAP.getOrDefault(targetItem, targetItem);
            List<Item> groupShapes = SHAPES_DATAMAP.getOrDefault(originalItem, List.of());

            ItemStack slotStack = inventory.items.get(exactIndex);
            Item slotItem = slotStack.getItem();

            if (slotItem == originalItem || groupShapes.contains(slotItem)) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                Objects.requireNonNull(getConnection()).send(new ChangeStackPayload(-1, exactIndex, replaced));
                inventory.items.set(exactIndex, replaced);
            }
        }

        return exactIndex;
    }
}
