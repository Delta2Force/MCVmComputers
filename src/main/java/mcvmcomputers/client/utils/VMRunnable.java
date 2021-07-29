package mcvmcomputers.client.utils;

import static mcvmcomputers.client.ClientMod.*;

import java.nio.ByteBuffer;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.GuiFocus;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.system.MemoryUtil;

public class VMRunnable implements Runnable{
	@Override
	public void run() {
		MinecraftClient mcc = MinecraftClient.getInstance();
		while(true) {
			double deltaX = mouseCurX - mouseLastX;
			double deltaY = mouseCurY - mouseLastY;
			mouseLastX = mouseCurX;
			mouseLastY = mouseCurY;

			if(vbMachine == 0L || vmSession == 0L) {
				vmIsOn = false;
				continue;
			}
			synchronized (ClientMod.vbHookLock) {
				boolean power = VB_HOOK.vm_powered_on(vbMachine);
				if(vmShouldBeOn) {
					if(!power) {
						VB_HOOK.start_vm(vmSession, vbMachine);
						vmIsOn = true;
						power = true;
					}else if(power && !vmIsOn) {
						vmShouldBeOn = false;
						System.out.println("Turn off the VM in VirtualBox first.");
					}
				}
				if(!vmShouldBeOn && power) {
					VB_HOOK.stop_vm(vmSession);
					VB_HOOK.free_session(vmSession);
					vmSession = 0L;
					vbMachine = 0L;
					vmEntityID = -1;
					vmIsOn = false;
					vmUpdateThread = null;
					return;
				}
				int mouseVal = 0;
				if (mcc.currentScreen instanceof GuiFocus) {
					mouseVal += (leftMouseButton ? 0x01 : 0) + (middleMouseButton ? 0x04 : 0) + (rightMouseButton ? 0x02 : 0);
				}
				int[] array;
				if (releaseKeys) {
					array = new int[]{0x1d + 0x80, 0xe0, 0x1d + 0x80, 0x03 + 0x80};
				} else {
					array = new int[vmKeyboardScancodes.size()];
					for (int i = 0; i < array.length; i++) {
						array[i] = vmKeyboardScancodes.get(i);
					}
				}
				long[] data = VB_HOOK.tick_vm(vbClient, vbMachine, (int) deltaX, (int) deltaY, mouseDeltaScroll, mouseVal, array);
				if (data.length > 0) {
					synchronized (vmTextureLock) {
						if (vmTextureBytes == null || (vmTextureWidth != data[0] || vmTextureHeight != data[1])) {
							vmTextureBytes = ByteBuffer.allocateDirect((int) (data[0] * data[1] * 4));
						}
						//Could be made a little smaller by passing the entire array at once instead of each individual value
						VB_HOOK.screenshot_vm(data[2], data[3], data[4], data[0], data[1], MemoryUtil.memAddress(vmTextureBytes));
						vmTextureBytesSize = vmTextureBytes.capacity();
						vmTextureWidth = (int) data[0];
						vmTextureHeight = (int) data[1];
						vmTextureUpdate = true;
					}
				}
			}
		}
	}

}
