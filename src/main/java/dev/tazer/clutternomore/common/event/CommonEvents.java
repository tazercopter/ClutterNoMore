package dev.tazer.clutternomore.common.event;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.registry.CDataComponents;
import dev.tazer.clutternomore.networking.PlayerChangeStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = ClutterNoMore.MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {

        for (Item item : event.getAllItems().toList()) {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
            Block block = Block.byItem(item);
            if (block instanceof SlabBlock || block instanceof StairBlock || key.getPath().endsWith("_stairs") || key.getPath().endsWith("_slab")) {
                continue;
            }

            // todo: remove selected shape index, make the stairs and slab store their own index in the original block component
            // todo: remove the block itself from the shapes list
            Optional<Item> stairs = getOptional(key, "_stairs");
            Optional<Item> slab = getOptional(key, "_slab");

            if (stairs.isPresent() || slab.isPresent()) {
                List<Item> shapes = new ArrayList<>();
                stairs.ifPresent(s -> {
                    shapes.add(s);
                    event.modify(s, builder -> builder.set(CDataComponents.BLOCK.get(), item));
                });
                slab.ifPresent(s -> {
                    shapes.add(s);
                    event.modify(s, builder -> builder.set(CDataComponents.BLOCK.get(), item));
                });

                event.modify(item, builder -> builder.set(CDataComponents.SHAPES.get(), shapes));
            }
        }
    }

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event) {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item.getDefaultInstance().has(CDataComponents.BLOCK))
                .forEach(item -> event.remove(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY));
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                PlayerChangeStack.TYPE,
                PlayerChangeStack.STREAM_CODEC,
                PlayerChangeStack::handleDataOnServer
        );
    }

    public static Optional<Item> getOptional(ResourceLocation key, String suffix) {
        if (key.getPath().endsWith("_planks")) {
            return BuiltInRegistries.ITEM.getOptional(key.withPath(path -> path.substring(0, path.length() - 7) + suffix));
        } else {
            return BuiltInRegistries.ITEM.getOptional(key.getPath().endsWith("s") ? key.withPath(path -> path.substring(0, path.length() - 1) + suffix) : key.withSuffix(suffix));
        }
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }
}
