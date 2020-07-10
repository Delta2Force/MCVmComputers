package mcvmcomputers.client.gui.setup.pages;

import mcvmcomputers.client.gui.setup.GuiSetup;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Language;

public class SetupPageIntroMessage extends SetupPage{
	public SetupPageIntroMessage(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		String text = setupGui.translation("mcvmcomputers.setup.intro_message");
		
		int offY = -36;
		
		for(String s : text.split("\n")) {
			this.textRender.draw(s, setupGui.width/2 - this.textRender.getStringWidth(s)/2, setupGui.height/2 + offY, -1);
			offY+=10;
		}
	}

	@Override
	public void init() {
		setupGui.addButton(new ButtonWidget(setupGui.width/2 - 40, setupGui.height - 40, 80, 20, setupGui.translation("mcvmcomputers.setup.nextButton"), (bw) -> this.setupGui.nextPage()));
	}

}
