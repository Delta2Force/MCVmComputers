package mcvmcomputers.gui.setup.pages;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.virtualbox_6_1.IVirtualBox;
import org.virtualbox_6_1.VirtualBoxManager;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.gui.setup.GuiSetup;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class SetupPageMaxValues extends SetupPage{
	private String statusMaxRam;
	private String statusVideoMemory;
	private TextFieldWidget maxRam;
	private TextFieldWidget videoMemory;
	private String status;
	private boolean onlyStatusMessage = false;
	
	public SetupPageMaxValues(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}
	
	private boolean checkMaxRam(String input) {
		if(input.isEmpty()) {
			statusMaxRam = MVCUtils.getColorChar('c') + "Input is empty.";
			return false;
		}
		if(!StringUtils.isNumeric(input)) {
			statusMaxRam = MVCUtils.getColorChar('c') + "Input is NaN.";
			return false;
		}
		int rm = Integer.parseInt(input);
		if(rm < 16) {
			statusMaxRam = MVCUtils.getColorChar('c') + "Too little! Min 16MB.";
			return false;
		}
		statusMaxRam = MVCUtils.getColorChar('a') + "Input is valid.";
		return true;
	}
	
	private boolean videoMemory(String input) {
		if(input.isEmpty()) {
			statusVideoMemory = MVCUtils.getColorChar('c') + "Input is empty.";
			return false;
		}
		if(!StringUtils.isNumeric(input)) {
			statusVideoMemory = MVCUtils.getColorChar('c') + "Input is NaN.";
			return false;
		}
		int nm = Integer.parseInt(input);
		if(nm > 256) {
			statusVideoMemory = MVCUtils.getColorChar('c') + "Too much! Max 256";
			return false;
		}
		statusVideoMemory = MVCUtils.getColorChar('a') + "Input is valid.";
		return true;
	}
	
	private void confirmButton(ButtonWidget in) {
		if(MCVmComputersMod.vboxWebSrv != null) {
			MCVmComputersMod.vboxWebSrv.destroy();
		}
		
		if(SystemUtils.IS_OS_WINDOWS) {
			ProcessBuilder vboxConfig = new ProcessBuilder(this.setupGui.virtualBoxDirectory + "\\vboxmanage.exe", "setproperty", "websrvauthlibrary", "null");
			try {
				vboxConfig.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			ProcessBuilder vboxWebSrv = new ProcessBuilder(this.setupGui.virtualBoxDirectory + "\\vboxwebsrv.exe", "--timeout", "0");
			try {
				MCVmComputersMod.vboxWebSrv = vboxWebSrv.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else if(SystemUtils.IS_OS_MAC){
			ProcessBuilder vboxConfig = new ProcessBuilder(this.setupGui.virtualBoxDirectory + "/VBoxManage", "setproperty", "websrvauthlibrary", "null");
			try {
				vboxConfig.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			ProcessBuilder vboxWebSrv = new ProcessBuilder(this.setupGui.virtualBoxDirectory + "/vboxwebsrv", "--timeout", "0");
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
		this.setupGui.clearElements();
		this.setupGui.clearButtons();
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
						status = MVCUtils.getColorChar('a') + "VirtualBox " + vb.getVersion() + " works!\n\n"+"Minecraft will start in " + i + " seconds.";
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
						status = MVCUtils.getColorChar('c') + "Failed! Error will be printed to log.\n" + MVCUtils.getColorChar('c') + "Maybe vboxwebsrv is already running? In that case,\n" + MVCUtils.getColorChar('c')  + "close it using your Task Manager. If it's a VirtualBox\n" + MVCUtils.getColorChar('c')  + "problem, start it and look at the error.\n\n"+"Settings menu in " + i + " seconds.";
					}
					onlyStatusMessage = false;
					init();
				}
			}
		}).start();
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if(!onlyStatusMessage) {
			this.textRender.draw("Maximum RAM used by VM:", setupGui.width/2 - 160, setupGui.height/2-30, -1);
			this.textRender.draw("VM Video Memory:", setupGui.width/2 + 10, setupGui.height/2-30, -1);
			String s = "Set these values in " + MVCUtils.getColorChar('l') + "megabytes." + MVCUtils.getColorChar('r') + " 1 GB = 1024 MB. NaN = Not a Number.";
			this.textRender.draw(s, setupGui.width/2 - textRender.getStringWidth(s)/2, setupGui.height/2+30, -1);
			this.textRender.draw(statusMaxRam, setupGui.width / 2 - 160, setupGui.height/2 + 3, -1);
			this.textRender.draw(statusVideoMemory, setupGui.width / 2 + 10, setupGui.height/2 + 3, -1);
			this.maxRam.render(mouseX, mouseY, delta);
			this.videoMemory.render(mouseX, mouseY, delta);
		}else {
			int yOff = -((this.textRender.fontHeight * status.split("\n").length)/2);
			for(String s : status.split("\n")) {
				this.textRender.draw(s, setupGui.width/2 - this.textRender.getStringWidth(s)/2, (setupGui.height/2-this.textRender.fontHeight/2)+yOff, -1);
				yOff+=this.textRender.fontHeight+1;
			}
		}
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
			maxRam = new TextFieldWidget(this.textRender, setupGui.width/2-160, setupGui.height/2-20, 150, 20, "");
			maxRam.setText(maxRamText);
			maxRam.setChangedListener((str) -> checkMaxRam(str));
			videoMemory = new TextFieldWidget(this.textRender, setupGui.width/2+10, setupGui.height/2-20, 150, 20, "");
			videoMemory.setText(videoMemoryText);
			videoMemory.setChangedListener((str) -> videoMemory(str));
			this.checkMaxRam(maxRam.getText());
			this.videoMemory(videoMemory.getText());
			this.setupGui.addElement(maxRam);
			this.setupGui.addElement(videoMemory);
			this.setupGui.addButton(new ButtonWidget(setupGui.width/2 - 50, setupGui.height - 50, 100, 20, "Confirm", (btn) -> confirmButton(btn)));
		}
	}

}
