package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
//? if fabric
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Objects;

import static dev.tazer.clutternomore.common.event.DatamapHandler.INVERSE_SHAPES_DATAMAP;
import static dev.tazer.clutternomore.common.event.DatamapHandler.SHAPES_DATAMAP;

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
            List<Item> shapes = SHAPES_DATAMAP.getOrDefault(originalItem, List.of());

            ItemStack slotStack = inventory.items.get(exactIndex);
            Item slotItem = slotStack.getItem();

            if (slotItem == originalItem || shapes.contains(slotItem)) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                //? if neoforge {
                /*Objects.requireNonNull(getConnection())
                *///?} else {
                ClientPlayNetworking
                //?}
                        .send(new ChangeStackPayload(-1, exactIndex, replaced));
            }
        }

        return exactIndex;
    }
}
