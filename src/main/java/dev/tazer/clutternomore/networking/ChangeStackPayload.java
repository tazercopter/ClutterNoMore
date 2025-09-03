package dev.tazer.clutternomore.networking;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChangeStackPayload(int containerId, int slot, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeStackPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "player_change_stack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeStackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ChangeStackPayload::containerId,
            ByteBufCodecs.INT,
            ChangeStackPayload::slot,
            ItemStack.STREAM_CODEC,
            ChangeStackPayload::stack,
            ChangeStackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleDataOnServer(final ChangeStackPayload data, final IPayloadContext context) {
        if (data.containerId == -1) {
            if (data.slot == -1) context.player().setItemInHand(InteractionHand.MAIN_HAND, data.stack);
            else context.player().getInventory().setItem(data.slot, data.stack);
        } else {
            if (data.containerId == context.player().containerMenu.containerId) {
                AbstractContainerMenu menu = context.player().containerMenu;
                menu.setItem(data.slot, menu.getStateId(), data.stack);
            }
        }
    }
}
