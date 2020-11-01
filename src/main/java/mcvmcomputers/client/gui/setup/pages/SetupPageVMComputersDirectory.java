package mcvmcomputers.client.gui.setup.pages;

import java.io.File;
import java.io.IOException;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.setup.GuiSetup;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class SetupPageVMComputersDirectory extends SetupPage{
	private TextFieldWidget vmComputersDirectory;
	private ButtonWidget next;
	private String vboxStatus;
	
	public SetupPageVMComputersDirectory(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		this.textRender.draw(ms, setupGui.translation("mcvmcomputers.setup.vmcomputersdir"), setupGui.width/2-160, setupGui.height/2-20, -1);
		this.textRender.draw(ms, vboxStatus, setupGui.width/2-160, setupGui.height/2+13, -1);
		this.textRender.draw(ms, setupGui.translation("mcvmcomputers.setup.dontchange0"), setupGui.width/2-160, 60, -1);
		this.textRender.draw(ms, setupGui.translation("mcvmcomputers.setup.dontchange1"), setupGui.width/2-160, 70, -1);
		this.vmComputersDirectory.render(ms, mouseX, mouseY, delta);
	}
	
	private void next(ButtonWidget bw) {
		if(checkDirectory(vmComputersDirectory.getText())) {
			File parent = new File(vmComputersDirectory.getText());
			ClientMod.isoDirectory = new File(parent, "isos");
			ClientMod.vhdDirectory = new File(parent, "vhds");
			if(!ClientMod.isoDirectory.exists()) {
				ClientMod.isoDirectory.mkdir();
			}
			if(!ClientMod.vhdDirectory.exists()) {
				ClientMod.vhdDirectory.mkdir();
			}
			try {
				ClientMod.getVHDNum();
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
			vboxStatus = setupGui.translation("mcvmcomputers.input_empty");
			next.active = false;
			return false;
		}
		if(s.contains(" ")) {
			vboxStatus = setupGui.translation("mcvmcomputers.input_space");
			next.active = false;
			return false;
		}
		File vboxDir = new File(s);
		if(!vboxDir.exists()) {
			vboxStatus = setupGui.translation("mcvmcomputers.input_dir_notfound");
			next.active = false;
			return false;
		}
		if(vboxDir.isFile()) {
			vboxStatus = setupGui.translation("mcvmcomputers.input_dir_notdir");
			next.active = false;
			return false;
		}
		vboxStatus = setupGui.translation("mcvmcomputers.input_dir_yes");
		next.active = true;
		return true;
	}

	@Override
	public void init() {
		int nextButtonW = textRender.getWidth(setupGui.translation("mcvmcomputers.setup.nextButton"))+40;
		next = new ButtonWidget(setupGui.width/2 - (nextButtonW/2), setupGui.height - 40, nextButtonW, 20, new LiteralText(setupGui.translation("mcvmcomputers.setup.nextButton")), (bw) -> this.next(bw));
		String dirText = ClientMod.vhdDirectory.getParentFile().getAbsolutePath();
		if(vmComputersDirectory != null) {
			dirText = vmComputersDirectory.getText();
		}
		this.checkDirectory(dirText);
		vmComputersDirectory = new TextFieldWidget(this.textRender, setupGui.width/2 - 160, setupGui.height/2 - 10, 320, 20, new LiteralText(""));
		vmComputersDirectory.setMaxLength(35565);
		vmComputersDirectory.setText(dirText);
		vmComputersDirectory.setChangedListener((s) -> checkDirectory(s));
		setupGui.addElement(vmComputersDirectory);
		setupGui.addButton(next);
	}

}
