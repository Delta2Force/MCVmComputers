package mcvmcomputers.client.utils;

import static mcvmcomputers.client.ClientMod.*;

import java.util.Arrays;

import org.virtualbox_6_1.BitmapFormat;
import org.virtualbox_6_1.GuestMonitorStatus;
import org.virtualbox_6_1.Holder;
import org.virtualbox_6_1.IConsole;
import org.virtualbox_6_1.IMachine;
import org.virtualbox_6_1.IProgress;
import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.LockType;
import org.virtualbox_6_1.MachineState;

import mcvmcomputers.client.gui.GuiFocus;
import net.minecraft.client.MinecraftClient;

public class VMRunnable implements Runnable{
	@Override
	public void run() {
		MinecraftClient mcc = MinecraftClient.getInstance();
		while(true) {
			try {
				double deltaX = 0;
				double deltaY = 0;
				
				deltaX = mouseCurX - mouseLastX;
				deltaY = mouseCurY - mouseLastY;
				mouseLastX = mouseCurX;
				mouseLastY = mouseCurY;
				
				IMachine m = vb.findMachine("VmComputersVm");
				if(m.getState() == MachineState.PoweredOff) {
					if(!vmTurningOff && vmTurnedOn) {
						IProgress pr = m.launchVMProcess(vbManager.getSessionObject(), "headless", Arrays.asList());
						pr.waitForCompletion(-1);
					}else {
						vmUpdateThread = null;
						return;
					}
				}
					ISession ns = vbManager.getSessionObject();
					m.lockMachine(ns, LockType.Shared);
					IConsole console = ns.getConsole();
					if(mcc.currentScreen instanceof GuiFocus) {
						int val = 0x00;
						if(leftMouseButton) {
							val += 0x01;
						}
						if(middleMouseButton) {
							val += 0x04;
						}
						if(rightMouseButton) {
							val += 0x02;
						}
						console.getMouse().putMouseEvent((int)deltaX, (int)deltaY, mouseDeltaScroll, 0, val);
					}
					if(releaseKeys) {
						console.getKeyboard().putScancodes(Arrays.asList(0x1d + 0x80, 0xe0, 0x1d + 0x80, 0x0e + 0x80));
						vmKeyboardScancodes.clear();
						releaseKeys = false;
					}else {
						console.getKeyboard().putScancodes(vmKeyboardScancodes);
						vmKeyboardScancodes.clear();
					}
					Holder<Long> width = new Holder<Long>();
					Holder<Long> height = new Holder<Long>();
					Holder<Long> bitsPP = new Holder<Long>();
					Holder<Integer> xOrigin = new Holder<Integer>();
					Holder<Integer> yOrigin = new Holder<Integer>();
					Holder<GuestMonitorStatus> status = new Holder<GuestMonitorStatus>();
					console.getDisplay().getScreenResolution(0L, width, height, bitsPP, xOrigin, yOrigin, status);
					Long w = width.value;
					Long h = height.value;
					byte[] image = null;
					try {
						image = console.getDisplay().takeScreenShotToArray(0L, w, h, BitmapFormat.PNG);
					}catch(Exception ex) {
						ns.unlockMachine();
						continue;
					}
					ns.unlockMachine();
					vmTextureBytesSize = image.length;
					vmTextureBytes = image;
			}catch(Exception ex) {} //TERRIBLE PRACTICE BTW
		}
	}

}
