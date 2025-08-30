package dev.tazer.clutternomore.networking;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerChangeStack(ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerChangeStack> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "player_change_stack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerChangeStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            PlayerChangeStack::stack,
            PlayerChangeStack::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleDataOnServer(final PlayerChangeStack data, final IPayloadContext context) {
        context.player().setItemInHand(InteractionHand.MAIN_HAND, data.stack);
    }
}
