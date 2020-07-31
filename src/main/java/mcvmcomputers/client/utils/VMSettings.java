package mcvmcomputers.client.utils;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.glfw.GLFW;

import mcvmcomputers.client.ClientMod;
import net.minecraft.client.MinecraftClient;

public class VMSettings {
	public static final int SETTINGS_VERSION = 2;
	
	public int settingsVersion = -1;
	public String vboxDirectory;
	public String vmComputersDirectory;
	
	public int maxRam = 8192;
	public int videoMem = 256;
	public int unfocusKey1 = GLFW.GLFW_KEY_LEFT_CONTROL;
	public int unfocusKey2 = GLFW.GLFW_KEY_RIGHT_CONTROL;
	public int unfocusKey3 = GLFW.GLFW_KEY_BACKSPACE;
	public int unfocusKey4 = -1;
	
	protected void fillWithDefaults() {
		settingsVersion = SETTINGS_VERSION;
		
		if(SystemUtils.IS_OS_WINDOWS) {
			vboxDirectory = "C:\\Program Files\\Oracle\\VirtualBox";
		}else if(SystemUtils.IS_OS_MAC) {
			vboxDirectory = "/Applications/VirtualBox.app/Contents/MacOS";
			unfocusKey1 = GLFW.GLFW_KEY_LEFT_ALT;
			unfocusKey2 = GLFW.GLFW_KEY_RIGHT_ALT;
		}
		if(ClientMod.vhdDirectory != null) {
			vmComputersDirectory = ClientMod.vhdDirectory.getParentFile().getAbsolutePath();
		}else {
			MinecraftClient mc = MinecraftClient.getInstance();
			vmComputersDirectory = new File(mc.runDirectory, "vm_computers").getAbsolutePath();
		}
	}
	
	public static VMSettings create() {
		VMSettings set = new VMSettings();
		set.fillWithDefaults();
		return set;
	}
}
