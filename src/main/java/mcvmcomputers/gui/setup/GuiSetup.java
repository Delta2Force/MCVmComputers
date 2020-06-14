package mcvmcomputers.gui.setup;

import java.util.Arrays;
import java.util.List;

import mcvmcomputers.gui.setup.pages.SetupPage;
import mcvmcomputers.gui.setup.pages.SetupPageIntroMessage;
import mcvmcomputers.gui.setup.pages.SetupPageMaxValues;
import mcvmcomputers.gui.setup.pages.SetupPageVMComputersDirectory;
import mcvmcomputers.gui.setup.pages.SetupPageVboxDirectory;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

public class GuiSetup extends Screen{
	private List<SetupPage> setupPages;
	private int setupIndex;
	private SetupPage currentSetupPage;
	private boolean initialized = false;
	public String virtualBoxDirectory = "C:\\Program Files\\Oracle\\VirtualBox";
	
	public GuiSetup() {
		super(new LiteralText("Setup"));
	}
	
	public void addElement(Element e) {
		this.children.add(e);
	}
	
	public void clearElements() {
		this.children.clear();
	}
	
	public void clearButtons() {
		this.buttons.clear();
	}
	
	public void addButton(ButtonWidget bw) {
		super.addButton(bw);
	}
	
	public void nextPage() {
		if(setupIndex < setupPages.size()) {
			setupIndex++;
			currentSetupPage = setupPages.get(setupIndex);
			this.init();
		}
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Override
	public void init() {
		if(!initialized) {
			setupPages = Arrays.asList(new SetupPageIntroMessage(this, this.font), new SetupPageVboxDirectory(this, this.font), new SetupPageVMComputersDirectory(this, this.font), new SetupPageMaxValues(this, this.font));
			currentSetupPage = setupPages.get(0);
			initialized = true;
		}
		this.clearButtons();
		this.clearElements();
		currentSetupPage.init();
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderDirtBackground(0);
		this.font.draw("VM Computers: "+MVCUtils.getColorChar('l')+"Setup", this.width/2 - this.font.getStringWidth("VM Computers: Setup")/2, 20, -1);
		String s = MVCUtils.getColorChar('7') + "Page " + (setupIndex+1) + "/" + setupPages.size();
		this.font.draw(s, this.width/2 - this.font.getStringWidth(s)/2, 30, -1);
		currentSetupPage.render(mouseX, mouseY, delta);
		super.render(mouseX, mouseY, delta);
	}
}
