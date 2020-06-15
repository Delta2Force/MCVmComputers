package mcvmcomputers;

import mcvmcomputers.client.tablet.TabletOrder;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.sound.SoundList;
import net.fabricmc.api.ModInitializer;

public class MainInitializer implements ModInitializer{
	public static TabletOrder currentOrder;
	
	public void onInitialize() {
		ItemList.init();
		EntityList.init();
		SoundList.init();
		PacketList.registerServerPackets();
	}

}
