package mcvmcomputers.gui.setup.pages;

import mcvmcomputers.gui.setup.GuiSetup;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;

public class SetupPageIntroMessage extends SetupPage{
	public SetupPageIntroMessage(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		String st = "You need to install VirtualBox (version 6.1.*) for this mod to work.";
		this.textRender.draw(st, setupGui.width/2 - this.textRender.getStringWidth(st)/2, setupGui.height/2 - 36, -1);
		String str = "If you haven't done it yet, download and install it from " + MVCUtils.getColorChar('l') + "virtualbox.org";
		this.textRender.draw(str, setupGui.width/2 - this.textRender.getStringWidth(str)/2, setupGui.height/2 - 26, -1);
		String stri = "or your package manager. If you need to restart, do it and come back.";
		this.textRender.draw(stri, setupGui.width/2 - this.textRender.getStringWidth(stri)/2, setupGui.height/2 - 16, -1);
		String strin = "WARNING! This might cause VirtualBox to stop working, resulting in you having";
		this.textRender.draw(strin, setupGui.width/2 - this.textRender.getStringWidth(strin)/2, setupGui.height/2 + 8, -1);
		String string = "to reset your preferences. (If that happens, just google how to reset)";
		this.textRender.draw(string, setupGui.width/2 - this.textRender.getStringWidth(string)/2, setupGui.height/2 + 18, -1);
	}

	@Override
	public void init() {
		setupGui.addButton(new ButtonWidget(setupGui.width/2 - 40, setupGui.height - 40, 80, 20, "Next", (bw) -> this.setupGui.nextPage()));
	}

}
