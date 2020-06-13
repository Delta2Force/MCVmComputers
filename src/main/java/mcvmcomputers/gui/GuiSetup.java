package mcvmcomputers.gui;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.virtualbox_6_1.IVirtualBox;
import org.virtualbox_6_1.VirtualBoxManager;

import mcvmcomputers.MCVmComputersMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TranslatableText;

public class GuiSetup extends Screen{
	private TextFieldWidget maxRam;
	private TextFieldWidget videoMemory;
	private String statusMaxRam;
	private String statusVideoMemory;
	
	private String status;
	private boolean onlyStatusMessage = false;
	
	private static final char COLOR_CHAR = (char) (0xfeff00a7);
	
	public GuiSetup() {
		super(new TranslatableText("Setup"));
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	private boolean checkMaxRam(String input) {
		if(input.isEmpty()) {
			statusMaxRam = COLOR_CHAR + "cInput is empty.";
			return false;
		}
		if(!StringUtils.isNumeric(input)) {
			statusMaxRam = COLOR_CHAR + "cInput is NaN.";
			return false;
		}
		int rm = Integer.parseInt(input);
		if(rm < 16) {
			statusMaxRam = COLOR_CHAR + "cToo little! Min 16MB.";
			return false;
		}
		statusMaxRam = COLOR_CHAR + "aInput is valid.";
		return true;
	}
	
	private boolean videoMemory(String input) {
		if(input.isEmpty()) {
			statusVideoMemory = COLOR_CHAR + "cInput is empty.";
			return false;
		}
		if(!StringUtils.isNumeric(input)) {
			statusVideoMemory = COLOR_CHAR + "cInput is NaN.";
			return false;
		}
		int nm = Integer.parseInt(input);
		if(nm > 256) {
			statusVideoMemory = COLOR_CHAR + "cToo much! Max 256";
			return false;
		}
		statusVideoMemory = COLOR_CHAR + "aInput is valid.";
		return true;
	}
	
	private void confirmButton(ButtonWidget in) {
		if(MCVmComputersMod.vboxWebSrv != null) {
			MCVmComputersMod.vboxWebSrv.destroy();
		}
		
		if(SystemUtils.IS_OS_WINDOWS) {
			ProcessBuilder vboxConfig = new ProcessBuilder("C:\\Program Files\\Oracle\\VirtualBox\\vboxmanage.exe", "setproperty", "websrvauthlibrary", "null");
			try {
				vboxConfig.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			ProcessBuilder vboxWebSrv = new ProcessBuilder("C:\\Program Files\\Oracle\\VirtualBox\\vboxwebsrv.exe", "--timeout", "0");
			try {
				MCVmComputersMod.vboxWebSrv = vboxWebSrv.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else {
			ProcessBuilder vboxConfig = new ProcessBuilder("vboxmanage", "setproperty", "websrvauthlibrary", "null");
			try {
				vboxConfig.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			ProcessBuilder vboxWebSrv = new ProcessBuilder("vboxwebsrv", "--timeout", "0");
			try {
				MCVmComputersMod.vboxWebSrv = vboxWebSrv.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		Runnable runnable = new Runnable() {
		    public void run() {
		    	MCVmComputersMod.vboxWebSrv.destroy();
		    }
		};
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
		boolean[] bools = new boolean[] {checkMaxRam(maxRam.getText()), videoMemory(videoMemory.getText())};
		for(boolean b : bools) {
			if(!b) {
				return;
			}
		}
		this.buttons.clear();
		this.children.clear();
		onlyStatusMessage = true;
		MCVmComputersMod.maxRam = Integer.parseInt(maxRam.getText());
		MCVmComputersMod.videoMem = Integer.parseInt(videoMemory.getText());
		status = "All values valid. Attempting to start VirtualBox...";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					VirtualBoxManager vm = VirtualBoxManager.createInstance(null);
					vm.connect("http://localhost:18083", "should", "work");
					IVirtualBox vb = vm.getVBox();
					for(int i = 5;i>=0;i--) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						status = COLOR_CHAR + "aVirtualBox " + vb.getVersion() + " works!\n\n"+"Minecraft will start in " + i + " seconds.";
					}
					MCVmComputersMod.vbManager = vm;
					MCVmComputersMod.vb = vb;
					minecraft.openScreen(new TitleScreen());
					return;
				}catch(Exception ex) {
					ex.printStackTrace();
					for(int i = 5;i>=0;i--) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						status = COLOR_CHAR + "cFailed! Error will be printed to log.\n" + COLOR_CHAR + "cMaybe vboxwebsrv is already running? In that case,\n" + COLOR_CHAR + "cclose it using your Task Manager. If it's a VirtualBox\n" + COLOR_CHAR + "cproblem, start it and look at the error.\n\n"+"Settings menu in " + i + " seconds.";
					}
					onlyStatusMessage = false;
					init();
				}
			}
		}).start();
	}
	
	@Override
	public void init() {
		String maxRamText = "8192";
		if(maxRam != null) {
			maxRamText = maxRam.getText();
		}
		String videoMemoryText = "256";
		if(videoMemory != null) {
			videoMemoryText = videoMemory.getText();
		}
		if(!this.onlyStatusMessage) {
			maxRam = new TextFieldWidget(this.font, this.width/2-160, this.height/2, 150, 20, "");
			maxRam.setText(maxRamText);
			maxRam.setChangedListener((str) -> checkMaxRam(str));
			videoMemory = new TextFieldWidget(this.font, this.width/2+10, this.height/2, 150, 20, "");
			videoMemory.setText(videoMemoryText);
			videoMemory.setChangedListener((str) -> videoMemory(str));
			this.checkMaxRam(maxRam.getText());
			this.videoMemory(videoMemory.getText());
			this.children.add(maxRam);
			this.children.add(videoMemory);
			this.addButton(new ButtonWidget(this.width/2 - 50, this.height - 50, 100, 20, "Confirm", (btn) -> confirmButton(btn)));
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.font.draw("VM Computers: "+COLOR_CHAR+"lSetup", this.width/2 - this.font.getStringWidth("VM Computers: Setup")/2, 20, -1);
		if(!onlyStatusMessage) {
			String st = "You need to install VirtualBox for this mod to work.";
			this.font.draw(st, this.width/2 - this.font.getStringWidth(st)/2, this.height/2 - 70, -1);
			String str = "If you haven't done it yet, download and install it from " + COLOR_CHAR + "lvirtualbox.org";
			this.font.draw(str, this.width/2 - this.font.getStringWidth(str)/2, this.height/2 - 60, -1);
			String stri = "or your package manager. If you need to restart, do it and come back.";
			this.font.draw(stri, this.width/2 - this.font.getStringWidth(stri)/2, this.height/2 - 50, -1);
			String strin = "WARNING! This might cause VirtualBox to stop working, resulting in you having";
			this.font.draw(strin, this.width/2 - this.font.getStringWidth(strin)/2, this.height/2 - 36, -1);
			String string = "to reset your preferences. (If that happens, just google how to reset)";
			this.font.draw(string, this.width/2 - this.font.getStringWidth(string)/2, this.height/2 - 26, -1);
			this.font.draw("Maximum RAM used by VM:", this.width/2 - 160, this.height/2-10, -1);
			this.font.draw("VM Video Memory:", this.width/2 + 10, this.height/2-10, -1);
			String s = "Set these values in " + COLOR_CHAR + "lmegabytes." + COLOR_CHAR + "r 1 GB = 1024 MB. NaN = Not a Number.";
			this.font.draw(s, this.width/2 - this.font.getStringWidth(s)/2, this.height/2+47, -1);
			this.font.draw(COLOR_CHAR + "7You need to set up a few things before the mod can work.", this.width/2 - this.font.getStringWidth("You need to set up a few things before the mod can work.")/2, 30, -1);
			this.font.draw(statusMaxRam, this.width / 2 - 160, this.height/2 + 22, -1);
			this.font.draw(statusVideoMemory, this.width / 2 + 10, this.height/2 + 22, -1);
			this.maxRam.render(mouseX, mouseY, delta);
			this.videoMemory.render(mouseX, mouseY, delta);
		}else {
			int yOff = 0;
			for(String s : status.split("\n")) {
				this.font.draw(s, this.width/2 - this.font.getStringWidth(s)/2, (this.height/2-this.font.fontHeight/2)+yOff, -1);
				yOff+=this.font.fontHeight+1;
			}
		}
		super.render(mouseX, mouseY, delta);
	}
}
