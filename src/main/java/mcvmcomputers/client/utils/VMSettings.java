package mcvmcomputers.client.utils;

import org.apache.commons.lang3.SystemUtils;

import mcvmcomputers.client.ClientMod;

public class VMSettings {
	public String vboxDirectory;
	public String vmComputersDirectory;
	
	public VMSettings() {
		if(SystemUtils.IS_OS_WINDOWS) {
			vboxDirectory = "C:\\Program Files\\Oracle\\VirtualBox";
		}else if(SystemUtils.IS_OS_MAC) {
			vboxDirectory = "/Applications/VirtualBox.app/Contents/MacOS";
		}
		vmComputersDirectory = ClientMod.vhdDirectory.getParentFile().getAbsolutePath();
	}
}
