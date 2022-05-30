package mcvmcomputers.client.gui.setup.pages;

import java.io.File;

import mcvmcomputers.client.gui.setup.GuiSetup;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class SetupPageIntroMessage extends SetupPage{
	public SetupPageIntroMessage(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		if(!setupGui.loadedConfiguration) {
			String text = setupGui.translation("mcvmcomputers.setup.intro_message");
			
			int offY = -36;
			
			for(String s : text.split("\n")) {
				this.textRender.draw(ms, s, setupGui.width/2 - this.textRender.getWidth(s)/2, setupGui.height/2 + offY, -1);
				offY+=10;
			}
		}
	}

	@Override
	public void init() {
		if(!setupGui.loadedConfiguration) {
			int buttonW = textRender.getWidth(setupGui.translation("mcvmcomputers.setup.nextButton"))+20;
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - (buttonW/2), setupGui.height - 40, buttonW, 20, new LiteralText(setupGui.translation("mcvmcomputers.setup.nextButton")), (bw) -> this.setupGui.nextPage()));
		}else {
			int useConfigW = textRender.getWidth(setupGui.translation("mcvmcomputers.setup.useConfig"))+20;
			int redoSetupW = textRender.getWidth(setupGui.translation("mcvmcomputers.setup.redoSetup"))+20;
			int w = useConfigW;
			if(redoSetupW > useConfigW)
				w = redoSetupW;
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - (w/2), setupGui.height / 2 - 25, w, 20, new LiteralText(setupGui.translation("mcvmcomputers.setup.useConfig")), (bw) -> this.setupGui.lastPage()));
			setupGui.addButton(new ButtonWidget(setupGui.width/2 - (w/2), setupGui.height / 2 + 5, w, 20, new LiteralText(setupGui.translation("mcvmcomputers.setup.redoSetup")), (bw) -> this.delete()));
		}
	}
	public void delete() {
		setupGui.loadedConfiguration = false;
		File f = new File(minecraft.runDirectory, "vm_computers/setup.json");
		if(f.exists()) {
			f.delete();
		}
		this.setupGui.nextPage();
	}
}
