package mcvmcomputers.mixins;

import static mcvmcomputers.client.ClientMod.*;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.GuiFocus;
import mcvmcomputers.client.gui.setup.pages.SetupPageUnfocusBinding;
import mcvmcomputers.client.utils.KeyConverter;
import mcvmcomputers.client.utils.QemuKey;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	public void onConstructed(CallbackInfo ci){
		GLFW.glfwSetInputMode(GLFW.glfwGetCurrentContext(),GLFW.GLFW_LOCK_KEY_MODS, GLFW.GLFW_TRUE);
	}

	@Inject(at = @At("HEAD"), method = "onKey")
	public void onKey(long window, int key, int scancode, int pressed, int modKey, CallbackInfo ci) {
		MinecraftClient mcc = MinecraftClient.getInstance();
		 if (window == mcc.getWindow().getHandle()) {
			 if((ClientMod.qemu ? ClientMod.isQemuRunning() : ClientMod.vmTurnedOn) && mcc.currentScreen instanceof GuiFocus) {
				 if(ClientMod.qemu) {
					 System.out.println("onKey: key=" + key + ", scancode=" + scancode + ", pressed="+pressed + ", modKey=" + modKey);
					 ClientMod.qemuKeys.add(new QemuKey(KeyConverter.keySymFromGLFW(key, modKey == 16), pressed == GLFW.GLFW_PRESS));
				 }
				 else if(pressed != 2) {
					 ClientMod.vmKeyboardScancodes.addAll(KeyConverter.toVBKey(key, pressed));
				 }
			 }else if(SetupPageUnfocusBinding.changeBinding) {
				 if(pressed == GLFW.GLFW_PRESS) {
					 if(getKeyName(key) != null) {
						 switch(SetupPageUnfocusBinding.bindingToBeChangedNum) {
							case 1:
								glfwUnfocusKey1 = key;
								break;
							case 2:
								glfwUnfocusKey2 = key;
								break;
							case 3:
								glfwUnfocusKey3 = key;
								break;
							case 4:
								glfwUnfocusKey4 = key;
								break;
						 }
						 SetupPageUnfocusBinding.bindingJustChanged = true;
						 SetupPageUnfocusBinding.changeBinding = false;
					 }
				 }
			 }
		 }
	}
}
