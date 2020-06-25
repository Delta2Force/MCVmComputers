package mcvmcomputers.client.utils;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

import mcvmcomputers.client.ClientMod;
import net.minecraft.client.MinecraftClient;

public class VMSettings {
	public String vboxDirectory;
	public String vmComputersDirectory;
	
	public VMSettings() {
		if(SystemUtils.IS_OS_WINDOWS) {
			vboxDirectory = "C:\\Program Files\\Oracle\\VirtualBox";
		}else if(SystemUtils.IS_OS_MAC) {
			vboxDirectory = "/Applications/VirtualBox.app/Contents/MacOS";
		}
		if(ClientMod.vhdDirectory != null) {
			vmComputersDirectory = ClientMod.vhdDirectory.getParentFile().getAbsolutePath();
		}else {
			MinecraftClient mc = MinecraftClient.getInstance();
			vmComputersDirectory = new File(mc.runDirectory, "vm_computers").getAbsolutePath();
		}
	}
}
