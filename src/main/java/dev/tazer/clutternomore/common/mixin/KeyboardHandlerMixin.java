package dev.tazer.clutternomore.common.mixin;

//? fabric {
import dev.tazer.clutternomore.fabric.ClientEvents;
//?}
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    //? fabric {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void key(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        ClientEvents.onKeyInput(key, action);
    }
    //?}

}