package mcvmcomputers.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.gui.GuiFocus;
import mcvmcomputers.utils.KeyConverter;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(at = @At("HEAD"), method = "onKey()V")
	public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo ci) {
		MinecraftClient mcc = MinecraftClient.getInstance();
		 if (window == mcc.getWindow().getHandle() && MCVmComputersMod.vmTurnedOn) {
			 if(mcc.currentScreen instanceof GuiFocus) {
				 if(i != 2) {
					 MCVmComputersMod.vmKeyboardScancodes.addAll(KeyConverter.toVBKey(key, i));
				 }
			 }
		 }
	}
}
