package mcvmcomputers.client.utils;

import static mcvmcomputers.client.ClientMod.*;

import java.util.Arrays;
import java.util.Collections;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.GuiFocus;
import net.minecraft.client.MinecraftClient;

public class VMRunnable implements Runnable{
	@Override
	public void run() {
		MinecraftClient mcc = MinecraftClient.getInstance();
		while(true) {
			try {
				double deltaX;
				double deltaY;
				
				deltaX = mouseCurX - mouseLastX;
				deltaY = mouseCurY - mouseLastY;
				mouseLastX = mouseCurX;
				mouseLastY = mouseCurY;

				if(vbMachine == 0L) continue;
				if(VB_HOOK.vm_powered_on(vbMachine)) {
					if(!vmTurningOff && vmTurnedOn) {
						VB_HOOK.start_vm(vmSession, vbMachine);
					}else {
						vmUpdateThread = null;
						return;
					}
				}
				int mouseVal = 0;
				if(mcc.currentScreen instanceof GuiFocus) {
					mouseVal += (leftMouseButton ? 0x01 : 0) + (middleMouseButton ? 0x04 : 0) + (rightMouseButton ? 0x02 : 0);
				}
				int[] array;
				if(releaseKeys) {
					array = new int[]{0x1d + 0x80, 0xe0, 0x1d+0x80, 0x03+0x80};
				}else{
					array = new int[vmKeyboardScancodes.size()];
					for(int i = 0;i<array.length;i++){
						array[i] = vmKeyboardScancodes.get(i);
					}
				}
				byte[] image = VB_HOOK.tick_vm(vbClient, vbMachine, (int)deltaX, (int)deltaY, mouseDeltaScroll, mouseVal, array);
				vmTextureBytesSize = image.length;
				vmTextureBytes = image;
			}catch(Exception ignored) {} //TERRIBLE PRACTICE BTW
		}
	}

}
