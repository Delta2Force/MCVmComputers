package mcvmcomputers.gui.setup.pages;

import java.io.File;
import java.io.IOException;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.gui.setup.GuiSetup;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class SetupPageVMComputersDirectory extends SetupPage{
	private TextFieldWidget vmComputersDirectory;
	private ButtonWidget next;
	private String vboxStatus;
	
	public SetupPageVMComputersDirectory(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.textRender.draw("VM Computers directory (VHDs and ISOs will be stored here)", setupGui.width/2-160, setupGui.height/2-20, -1);
		this.textRender.draw(vboxStatus, setupGui.width/2-160, setupGui.height/2+13, -1);
		this.textRender.draw(MVCUtils.getColorChar('7') + "If the value is valid, you don't need to change anything.", setupGui.width/2-160, 60, -1);
		this.textRender.draw(MVCUtils.getColorChar('7') + "Please only change this if you know what you're doing.", setupGui.width/2-160, 70, -1);
		this.vmComputersDirectory.render(mouseX, mouseY, delta);
	}
	
	private void next(ButtonWidget bw) {
		if(checkDirectory(vmComputersDirectory.getText())) {
			File parent = new File(vmComputersDirectory.getText());
			MCVmComputersMod.isoDirectory = new File(parent, "isos");
			MCVmComputersMod.vhdDirectory = new File(parent, "vhds");
			if(!MCVmComputersMod.isoDirectory.exists()) {
				MCVmComputersMod.isoDirectory.mkdir();
			}else if(!MCVmComputersMod.vhdDirectory.exists()) {
				MCVmComputersMod.vhdDirectory.mkdir();
			}
			try {
				MCVmComputersMod.getVHDNum();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.setupGui.nextPage();
		}
	}
	
	private boolean checkDirectory(String s) {
		if(s.isEmpty()) {
			vboxStatus = MVCUtils.getColorChar('c') + "The string is empty.";
			next.active = false;
			return false;
		}
		File vboxDir = new File(s);
		if(!vboxDir.exists()) {
			vboxStatus = MVCUtils.getColorChar('c') + "Directory doesn't exist.";
			next.active = false;
			return false;
		}else if(vboxDir.isFile()) {
			vboxStatus = MVCUtils.getColorChar('c') + "Path points to a file, not a directory.";
			next.active = false;
			return false;
		}
		vboxStatus = MVCUtils.getColorChar('a') + "Valid directory!";
		next.active = true;
		return true;
	}

	@Override
	public void init() {
		next = new ButtonWidget(setupGui.width/2 - 40, setupGui.height - 40, 80, 20, "Next", (bw) -> this.next(bw));
		String dirText = MCVmComputersMod.vhdDirectory.getParentFile().getAbsolutePath();
		if(vmComputersDirectory != null) {
			dirText = vmComputersDirectory.getText();
		}
		this.checkDirectory(dirText);
		vmComputersDirectory = new TextFieldWidget(this.textRender, setupGui.width/2 - 160, setupGui.height/2 - 10, 320, 20, "");
		vmComputersDirectory.setMaxLength(35565);
		vmComputersDirectory.setText(dirText);
		vmComputersDirectory.setChangedListener((s) -> checkDirectory(s));
		setupGui.addElement(vmComputersDirectory);
		setupGui.addButton(next);
	}

}
