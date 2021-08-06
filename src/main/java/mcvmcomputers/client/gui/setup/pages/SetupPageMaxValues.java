package mcvmcomputers.client.gui.setup.pages;

import com.google.gson.Gson;
import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.gui.setup.GuiSetup;
import mcvmcomputers.client.utils.VMSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;

@Environment(EnvType.CLIENT)
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
			statusMaxRam = setupGui.translation("mcvmcomputers.input_empty");
			return false;
		}
		if(!StringUtils.isNumeric(input)) {
			statusMaxRam = setupGui.translation("mcvmcomputers.input_nan");
			return false;
		}
		int rm = Integer.parseInt(input);
		if(rm < 16) {
			statusMaxRam = setupGui.translation("mcvmcomputers.input_too_little").replace("%s", "16");
			return false;
		}
		statusMaxRam = setupGui.translation("mcvmcomputers.input_valid");
		return true;
	}
	
	private boolean videoMemory(String input) {
		if(input.isEmpty()) {
			statusVideoMemory = setupGui.translation("mcvmcomputers.input_empty");
			return false;
		}
		if(!StringUtils.isNumeric(input)) {
			statusVideoMemory = setupGui.translation("mcvmcomputers.input_nan");
			return false;
		}
		int nm = Integer.parseInt(input);
		if(nm > 256) {
			statusVideoMemory = setupGui.translation("mcvmcomputers.input_too_much").replace("%s", "256");
			return false;
		}
		statusVideoMemory = setupGui.translation("mcvmcomputers.input_valid");
		return true;
	}
	
	private void confirmButton(ButtonWidget in) {
		boolean[] bools = new boolean[] {checkMaxRam(maxRam.getText()), videoMemory(videoMemory.getText())};
		for(boolean b : bools) {
			if(!b) {
				return;
			}
		}
		this.setupGui.clearChildren();
		onlyStatusMessage = true;
		ClientMod.maxRam = Integer.parseInt(maxRam.getText());
		ClientMod.videoMem = Integer.parseInt(videoMemory.getText());
		status = setupGui.translation("mcvmcomputers.setup.startingStatus");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ClientMod.VB_HOOK.loadLibraries(new File(ClientMod.vhdDirectory.getParentFile().getAbsolutePath()));
					if(!ClientMod.VB_HOOK.init_glue(setupGui.virtualBoxDirectory)) {
						throw new Exception("Failed to initialize Glue");
					}
					long vbclient = ClientMod.VB_HOOK.create_vb_client();
					long vb = ClientMod.VB_HOOK.create_vb(vbclient);
					VMSettings set = new VMSettings();
					set.vboxDirectory = setupGui.virtualBoxDirectory;
					set.vmComputersDirectory = ClientMod.vhdDirectory.getParentFile().getAbsolutePath();
					set.unfocusKey1 = ClientMod.glfwUnfocusKey1;
					set.unfocusKey2 = ClientMod.glfwUnfocusKey2;
					set.unfocusKey3 = ClientMod.glfwUnfocusKey3;
					set.unfocusKey4 = ClientMod.glfwUnfocusKey4;
					set.maxRam = ClientMod.maxRam;
					set.videoMem = ClientMod.videoMem;
					File f = new File(minecraft.runDirectory, "vm_computers/setup.json");
					if(f.exists()) {
						f.delete();
					}
					f.createNewFile();
					FileWriter fw = new FileWriter(f);
					fw.append(new Gson().toJson(set));
					fw.flush();
					fw.close();
					ClientMod.vbClient = vbclient;
					ClientMod.vb = vb;
					ClientMod.VB_HOOK.stop_vm_if_exists(ClientMod.vb, ClientMod.vbClient, "VmComputersVm");
					for(int i = 5;i>=0;i--) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						status = setupGui.translation("mcvmcomputers.setup.successStatus").replaceFirst("%s", ClientMod.VB_HOOK.get_vb_version()).replaceFirst("%s", ""+i);
					}
					var titleScreen = new TitleScreen(false);
					titleScreen.init(MinecraftClient.getInstance(), setupGui.width, setupGui.height);
					minecraft.setScreen(titleScreen);
					return;
				}catch(Exception ex) {
					ex.printStackTrace();
					for(int i = 5;i>=0;i--) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						status = setupGui.translation("mcvmcomputers.setup.failedStatus").replace("%s", ""+i);
					}
					onlyStatusMessage = false;
					setupGui.firstPage();
				}
			}
		}).start();
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		if(!onlyStatusMessage) {
			this.textRender.draw(ms, setupGui.translation("mcvmcomputers.setup.max_ram_input"), setupGui.width/2 - 160, setupGui.height/2-30, -1);
			this.textRender.draw(ms, setupGui.translation("mcvmcomputers.setup.vram_input"), setupGui.width/2 + 10, setupGui.height/2-30, -1);
			String s = setupGui.translation("mcvmcomputers.setup.ram_input_help");
			this.textRender.draw(ms, s, setupGui.width/2 - textRender.getWidth(s)/2, setupGui.height/2+30, -1);
			this.textRender.draw(ms, statusMaxRam, setupGui.width / 2 - 160, setupGui.height/2 + 3, -1);
			this.textRender.draw(ms, statusVideoMemory, setupGui.width / 2 + 10, setupGui.height/2 + 3, -1);
			this.maxRam.render(ms, mouseX, mouseY, delta);
			this.videoMemory.render(ms, mouseX, mouseY, delta);
		}else {
			int yOff = -((this.textRender.fontHeight * status.split("\n").length)/2);
			for(String s : status.split("\n")) {
				this.textRender.draw(ms, s, setupGui.width/2 - this.textRender.getWidth(s)/2, (setupGui.height/2-this.textRender.fontHeight/2)+yOff, -1);
				yOff+=this.textRender.fontHeight+1;
			}
		}
	}

	@Override
	public void init() {
		String maxRamText = ""+ClientMod.maxRam;
		if(maxRam != null) {
			maxRamText = maxRam.getText();
		}
		String videoMemoryText = ""+ClientMod.videoMem;
		if(videoMemory != null) {
			videoMemoryText = videoMemory.getText();
		}
		if(!onlyStatusMessage) {
			maxRam = new TextFieldWidget(this.textRender, setupGui.width/2-160, setupGui.height/2-20, 150, 20, new LiteralText(""));
			maxRam.setText(maxRamText);
			maxRam.setChangedListener((str) -> checkMaxRam(str));
			videoMemory = new TextFieldWidget(this.textRender, setupGui.width/2+10, setupGui.height/2-20, 150, 20, new LiteralText(""));
			videoMemory.setText(videoMemoryText);
			videoMemory.setChangedListener((str) -> videoMemory(str));
			checkMaxRam(maxRam.getText());
			videoMemory(videoMemory.getText());
			setupGui.addChild(maxRam);
			setupGui.addChild(videoMemory);
			int confirmW = textRender.getWidth(setupGui.translation("mcvmcomputers.setup.confirmButton"))+40;
			setupGui.addChild(new ButtonWidget(setupGui.width/2 - (confirmW/2), setupGui.height - 40, confirmW, 20, new LiteralText(setupGui.translation("mcvmcomputers.setup.confirmButton")), (btn) -> confirmButton(btn)));
			
			if(setupGui.startVb) {
				confirmButton(null);
				setupGui.startVb = false;
			}
		}
	}

}
