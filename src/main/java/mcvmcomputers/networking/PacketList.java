package mcvmcomputers.networking;

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.util.Identifier;

public class PacketList {
	//Client-to-server
	public static final Identifier C2S_ORDER = new Identifier("mcvmcomputers", "c2s_order");
	public static final Identifier C2S_SCREEN = new Identifier("mcvmcomputers", "c2s_screen");
	
	//Server-to-client
	public static final Identifier S2C_SCREEN = new Identifier("mcvmcomputers", "s2c_screen");
	public static final Identifier S2C_STOP_SCREEN = new Identifier("mcvmcomputers", "s2c_stop_screen");
	
	public static void registerClientPackets() {
		ClientSidePacketRegistry.INSTANCE.register(S2C_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int pcId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				
			});
		});
	}
	
	public static void registerServerPackets() {
		
	}
}
