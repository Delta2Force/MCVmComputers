package mcvmcomputers.client.utils;

import static mcvmcomputers.client.ClientMod.*;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.virtualbox_6_1.BitmapFormat;
import org.virtualbox_6_1.GuestMonitorStatus;
import org.virtualbox_6_1.Holder;
import org.virtualbox_6_1.IConsole;
import org.virtualbox_6_1.IMachine;
import org.virtualbox_6_1.IProgress;
import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.LockType;
import org.virtualbox_6_1.MachineState;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import com.shinyhut.vernacular.client.rendering.ColorDepth;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.GuiFocus;
import net.minecraft.client.MinecraftClient;

public class VMRunnable implements Runnable{
	private static Image vnc_image;
	private static VernacularClient qemu_vnc_client;
	private static int vncWidth = 0;
	private static int vncHeight = 0;
	@Override
	public void run() {
		VernacularConfig vnc_config = null;
		if(ClientMod.qemu) {
			vnc_config = new VernacularConfig();
			qemu_vnc_client = new VernacularClient(vnc_config);
			vnc_config.setColorDepth(ColorDepth.BPP_24_TRUE);
			vnc_config.setScreenUpdateListener(image -> {
				vnc_image = image;
			});
			vnc_config.setTargetFramesPerSecond(60);
		}
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		MinecraftClient mcc = MinecraftClient.getInstance();


		if(ClientMod.qemu) {
			new Thread(()->{
				int mouseX = 0, mouseY = 0;
				double deltaX = 0, deltaY = 0;
				while(true){

					deltaX = mouseCurX - mouseLastX;
					deltaY = mouseCurY - mouseLastY;
					mouseLastX = mouseCurX;
					mouseLastY = mouseCurY;
					if(deltaX != 0 && deltaY != 0){
						mouseX = (int) Math.max(0, Math.min(vncWidth, mouseX+deltaX));
						mouseY = (int) Math.max(0, Math.min(vncHeight, mouseY+deltaY));
						qemu_vnc_client.moveMouse(mouseX, mouseY);
					}
					qemu_vnc_client.updateMouseButton(1, leftMouseButton);
					qemu_vnc_client.updateMouseButton(2, middleMouseButton);
					qemu_vnc_client.updateMouseButton(3, rightMouseButton);
					synchronized (qemuKeys){
						for(QemuKey qk : qemuKeys) {
							qemu_vnc_client.updateKey(qk.keySym, qk.pressed);
						}
						qemuKeys.clear();
					}
				}
			}, "VMRunnable Keyboard").start();
			while(true) {
				if(!ClientMod.isQemuRunning()) {
					qemu_vnc_client.stop();
					vmUpdateThread = null;
					return;
				}

				if(!qemu_vnc_client.isRunning()) {
					System.out.println("Starting connection to VNC.");
					do{
						try{
							qemu_vnc_client.start("127.0.0.1", 5901);
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}
					} while(!qemu_vnc_client.isRunning());
					System.out.println("Connected to VNC!");
				}
				if(vmTextureBytes == null && vnc_image != null) {
					try {
						LocalTime time = LocalTime.now();
						ImageIO.write((RenderedImage) vnc_image, "PNG", byteStream);
						vncWidth = vnc_image.getWidth(null);
						vncHeight = vnc_image.getHeight(null);
						byte[] image = byteStream.toByteArray();
						byteStream.reset();
						System.out.println("Write and tobyte serialize: " + (time.getSecond() - LocalTime.now().getSecond()));
						vmTextureBytes = image;
						vmTextureBytesSize = image.length;
						vnc_image = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else {
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

}
