package mcvmcomputers.client.gui.setup.pages;

import org.apache.commons.lang3.SystemUtils;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.setup.GuiSetup;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;

public class SetupPageIntroMessage extends SetupPage{
	public SetupPageIntroMessage(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		if(!setupGui.loadedConfiguration) {
			String text = ClientMod.qemu ? setupGui.translation("mcvmcomputers.setup.intro_message_qemu") : setupGui.translation("mcvmcomputers.setup.intro_message_vbox");
			String[] split = text.split("\n");
			
			int offY = -1 * ((split.length*8)/3);
			
			for(String s : split) {
				this.textRender.draw(s, setupGui.width/2 - this.textRender.getStringWidth(s)/2, setupGui.height/2 + offY, -1);
				offY+=10;
			}
			
			this.textRender.draw(setupGui.translation("mcvmcomputers.setup.vmSoftware"), setupGui.width/2-70, setupGui.height/2-69, -1);
			this.textRender.draw(setupGui.translation("mcvmcomputers.setup.clickToCycle"), setupGui.width/2-70, setupGui.height/2-39, -1);
		}
	}
	
	public void changeVMSoftware() {
		ClientMod.qemu = !ClientMod.qemu;
		setupGui.init();
	}
	
	public void normalSetup() {
		if(ClientMod.qemu && SystemUtils.IS_OS_MAC){
			setupGui.setupPages.remove(1);
		}
		this.setupGui.nextPage();
	}
	
	public void redoSetup() {
		setupGui.loadedConfiguration = false;
		setupGui.init();
	}

	@Override
	public void init() {
		if(!setupGui.loadedConfiguration) {
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - 70, setupGui.height / 2 - 60, 140, 20, ClientMod.qemu ? "QEMU" : "Oracle VirtualBox", (bw) -> this.changeVMSoftware()));
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - 40, setupGui.height - 40, 80, 20, setupGui.translation("mcvmcomputers.setup.nextButton"), (bw) -> normalSetup()));
		}else {
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - 100, setupGui.height / 2 - 25, 200, 20, setupGui.translation("mcvmcomputers.setup.useConfig"), (bw) -> this.setupGui.lastPage()));
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - 100, setupGui.height / 2 + 5, 200, 20, setupGui.translation("mcvmcomputers.setup.redoSetup"), (bw) -> redoSetup()));
		}
	}

}
