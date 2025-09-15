package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
import java.util.Objects;
 *///?} else {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//?}

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    @Nullable
    public abstract ClientPacketListener getConnection();

    @Redirect(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int pickBlock(Inventory inventory, ItemStack targetStack) {
        int exactIndex = inventory.findSlotMatchingItem(targetStack);

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.items.get(exactIndex);

            if (ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
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
