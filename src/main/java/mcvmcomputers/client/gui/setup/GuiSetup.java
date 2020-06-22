package mcvmcomputers.client.gui.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import com.google.gson.Gson;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.setup.pages.SetupPage;
import mcvmcomputers.client.gui.setup.pages.SetupPageIntroMessage;
import mcvmcomputers.client.gui.setup.pages.SetupPageMaxValues;
import mcvmcomputers.client.gui.setup.pages.SetupPageUnfocusBinding;
import mcvmcomputers.client.gui.setup.pages.SetupPageVMComputersDirectory;
import mcvmcomputers.client.gui.setup.pages.SetupPageVboxDirectory;
import mcvmcomputers.client.utils.VMSettings;
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
	public String virtualBoxDirectory;
	
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
		if(new File(minecraft.runDirectory, "vm_computers/settings.json").exists()) {
			FileReader fr;
			VMSettings set = null;
			try {
				fr = new FileReader(new File(minecraft.runDirectory, "vm_computers/settings.json"));
				set = new Gson().fromJson(fr, VMSettings.class);
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(set.vboxDirectory != null) {
				virtualBoxDirectory = set.vboxDirectory;
			}
			ClientMod.isoDirectory = new File(set.vmComputersDirectory, "isos");
			ClientMod.vhdDirectory = new File(set.vmComputersDirectory, "vhds");
		}
		if(!initialized) {
			setupPages = new ArrayList<>();
			setupPages.add(new SetupPageIntroMessage(this, this.font));
			if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) {
				setupPages.add(new SetupPageVboxDirectory(this, this.font));
			}
			setupPages.add(new SetupPageVMComputersDirectory(this, this.font));
			setupPages.add(new SetupPageUnfocusBinding(this, this.font));
			setupPages.add(new SetupPageMaxValues(this, this.font));
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
