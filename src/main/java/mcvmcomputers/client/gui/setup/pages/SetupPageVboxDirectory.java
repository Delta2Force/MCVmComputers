package mcvmcomputers.client.gui.setup.pages;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

import mcvmcomputers.client.gui.setup.GuiSetup;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class SetupPageVboxDirectory extends SetupPage{
	private TextFieldWidget vboxDirectory;
	private ButtonWidget next;
	private String vboxStatus;
	
	public SetupPageVboxDirectory(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.textRender.draw("VirtualBox installation directory", setupGui.width/2-160, setupGui.height/2-20, -1);
		this.textRender.draw(vboxStatus, setupGui.width/2-160, setupGui.height/2+13, -1);
		this.textRender.draw(MVCUtils.getColorChar('7') + "If the value is valid, you don't need to change anything.", setupGui.width/2-160, 60, -1);
		this.textRender.draw(MVCUtils.getColorChar('7') + "Please only change this if you know what you're doing.", setupGui.width/2-160, 70, -1);
		this.vboxDirectory.render(mouseX, mouseY, delta);
	}
	
	private void next(ButtonWidget bw) {
		if(checkDirectory(vboxDirectory.getText())) {
			this.setupGui.virtualBoxDirectory = vboxDirectory.getText();
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
		}else if(!new File(vboxDir, "vboxmanage.exe").exists() || !new File(vboxDir, "vboxwebsrv.exe").exists()) {
			vboxStatus = MVCUtils.getColorChar('c') + "The directory isn't the VirtualBox directory.";
			next.active = false;
			return false;
		}
		vboxStatus = MVCUtils.getColorChar('a') + "This should be a valid VirtualBox directory!";
		next.active = true;
		return true;
	}

	@Override
	public void init() {
		next = new ButtonWidget(setupGui.width/2 - 40, setupGui.height - 40, 80, 20, "Next", (bw) -> this.next(bw));
		String dirText = this.setupGui.virtualBoxDirectory;
		if(vboxDirectory != null) {
			dirText = vboxDirectory.getText();
		}
		this.checkDirectory(dirText);
		vboxDirectory = new TextFieldWidget(this.textRender, setupGui.width/2 - 160, setupGui.height/2 - 10, 320, 20, "");
		vboxDirectory.setMaxLength(35565);
		vboxDirectory.setText(dirText);
		vboxDirectory.setChangedListener((s) -> checkDirectory(s));
		setupGui.addElement(vboxDirectory);
		setupGui.addButton(next);
	}

}
