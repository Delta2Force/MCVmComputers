package mcvmcomputers.utils;

import static mcvmcomputers.MCVmComputersMod.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.virtualbox_6_1.GuestMonitorStatus;
import org.virtualbox_6_1.Holder;
import org.virtualbox_6_1.IConsole;
import org.virtualbox_6_1.IMachine;
import org.virtualbox_6_1.IProgress;
import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.LockType;
import org.virtualbox_6_1.MachineState;

import mcvmcomputers.gui.GuiFocus;
import net.minecraft.client.MinecraftClient;
import net.propero.rdp.ConnectionException;
import net.propero.rdp.OrderException;
import net.propero.rdp.RdesktopCanvas_Localised;
import net.propero.rdp.RdesktopException;
import net.propero.rdp.Rdp;
import net.propero.rdp.crypto.CryptoException;
import net.propero.rdp.rdp5.Rdp5;
import net.propero.rdp.rdp5.VChannels;

public class VMRunnable implements Runnable{
	public static Rdp5 rdp;
	
	@Override
	public void run() {
		while(vmTurningOn) {}
		MinecraftClient mcc = MinecraftClient.getInstance();
		rdp = new Rdp5(new VChannels());
		RdesktopCanvas_Localised canvas = new RdesktopCanvas_Localised(800, 800);
		rdp.registerDrawingSurface(canvas);
		try {
			rdp.connect("", InetAddress.getByName("127.0.0.1"), Rdp.RDP_LOGON_NORMAL, "", "", "", "");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		Thread rdpThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rdp.mainLoop(null, null); //<- cant be null
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RdesktopException e) {
					e.printStackTrace();
				} catch (OrderException e) {
					e.printStackTrace();
				} catch (CryptoException e) {
					e.printStackTrace();
				}
			}
		});
		if(rdp.isConnected()) {
			rdpThread.start();
		}
		while(true) {
			try {
				if(!rdp.isConnected()) {
					vmUpdateThread = null;
					return;
				}
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
				int w = Math.toIntExact(width.value);
				int h = Math.toIntExact(height.value);
				if(w != canvas.getWidth() || h != canvas.getHeight()) {
					canvas.changeResolution(w, h);
				}
				vmTextureBytes = new ByteArrayInputStream(canvas.getBackstore());
			}catch(Exception ex) {} //TERRIBLE PRACTICE BTW
		}
	}

}
