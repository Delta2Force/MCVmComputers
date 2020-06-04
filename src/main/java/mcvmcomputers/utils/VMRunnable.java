package mcvmcomputers.utils;



import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.gui.GuiFocus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.LiteralText;

public class VMRunnable implements Runnable{
	@Override
	public void run() {
		MinecraftClient mcc = MinecraftClient.getInstance();
		while(true) {
			try {
				double deltaX = 0;
				double deltaY = 0;
				
				deltaX = MCVmComputersMod.mouseCurX - MCVmComputersMod.mouseLastX;
				deltaY = MCVmComputersMod.mouseCurY - MCVmComputersMod.mouseLastY;
				MCVmComputersMod.mouseLastX = MCVmComputersMod.mouseCurX;
				MCVmComputersMod.mouseLastY = MCVmComputersMod.mouseCurY;
				
				IMachine m = MCVmComputersMod.vb.findMachine("VmComputersVm");
				if(m.getState() == MachineState.PoweredOff) {
					if(!MCVmComputersMod.vmTurningOff && MCVmComputersMod.vmTurnedOn) {
						IProgress pr = m.launchVMProcess(MCVmComputersMod.vbManager.getSessionObject(), "headless", Arrays.asList());
						pr.waitForCompletion(-1);
					}else {
						MCVmComputersMod.vmUpdateThread = null;
						return;
					}
				}
					ISession ns = MCVmComputersMod.vbManager.getSessionObject();
					m.lockMachine(ns, LockType.Shared);
					IConsole console = ns.getConsole();
					if(mcc.currentScreen instanceof GuiFocus) {
						int val = 0x00;
						if(MCVmComputersMod.leftMouseButton) {
							val += 0x01;
						}
						if(MCVmComputersMod.middleMouseButton) {
							val += 0x04;
						}
						if(MCVmComputersMod.rightMouseButton) {
							val += 0x02;
						}
						console.getMouse().putMouseEvent((int)deltaX, (int)deltaY, MCVmComputersMod.mouseDeltaScroll, 0, val);
					}
					if(MCVmComputersMod.releaseKeys) {
						console.getKeyboard().putScancode(0x1d + 0x80);
						console.getKeyboard().putScancode(0xe0);
						console.getKeyboard().putScancode(0x1d + 0x80);
						console.getKeyboard().putScancode(0x0e + 0x80);
						MCVmComputersMod.vmKeyboardScancodes.clear();
						MCVmComputersMod.releaseKeys = false;
					}else {
						for(int i : MCVmComputersMod.vmKeyboardScancodes) {
							console.getKeyboard().putScancode(i);
						}
						MCVmComputersMod.vmKeyboardScancodes.clear();
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
					MCVmComputersMod.vmTextureBytes = new ByteArrayInputStream(image);
			}catch(Exception ex) {} //TERRIBLE PRACTICE BTW
		}
	}

}
