package mcvmcomputers.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcvmcomputers.MainMod;
import mcvmcomputers.client.ClientMod;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin {
	@Shadow
	private double eventDeltaWheel;
	
	@Shadow
	private int activeButton;
	
	@Shadow
	private boolean leftButtonClicked;
	
	@Shadow
	private boolean middleButtonClicked;
	
	@Shadow
	private boolean rightButtonClicked;
	
	@Inject(at = @At("TAIL"), method = "onMouseScroll")
	private void onMouseScroll(CallbackInfo ci) {
		ClientMod.mouseDeltaScroll = (int) this.eventDeltaWheel;
	}
	
	@Inject(at = @At("TAIL"), method = "onMouseButton")
	private void onMouseButton(CallbackInfo ci) {
		ClientMod.leftMouseButton = leftButtonClicked;
		ClientMod.middleMouseButton = middleButtonClicked;
		ClientMod.rightMouseButton = rightButtonClicked;
	}
}
