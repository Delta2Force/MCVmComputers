package mcvmcomputers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mcvmcomputers.entities.EntityList;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.sound.SoundList;
import mcvmcomputers.utils.TabletOrder;
import net.fabricmc.api.ModInitializer;

public class MainMod implements ModInitializer{
	public static Map<UUID, TabletOrder> orders;
	public static Map<UUID, EntityPC> computers;
	
	public static Runnable hardDriveClick = new Runnable() { @Override public void run() {} };
	public static Runnable deliveryChestSound = new Runnable() { @Override public void run() {} };
	public static Runnable focus = new Runnable() { @Override public void run() {} };
	public static Runnable pcOpenGui = new Runnable() { @Override public void run() {} };
	
	public void onInitialize() {
		orders = new HashMap<UUID, TabletOrder>();
		computers = new HashMap<UUID, EntityPC>();
		ItemList.init();
		EntityList.init();
		SoundList.init();
		PacketList.registerServerPackets();
	}

}
