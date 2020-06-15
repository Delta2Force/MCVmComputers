package mcvmcomputers.networking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

import mcvmcomputers.ClientMod;
import mcvmcomputers.MainMod;
import mcvmcomputers.client.tablet.TabletOrder;
import mcvmcomputers.item.OrderableItem;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
			UUID pcOwner = attachedData.readUuid();
			
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient mcc = MinecraftClient.getInstance();
				if(ClientMod.vmScreenTextures.containsKey(pcOwner)) {
					mcc.getTextureManager().destroyTexture(ClientMod.vmScreenTextures.get(pcOwner));
					ClientMod.vmScreenTextureNI.get(pcOwner).close();
					ClientMod.vmScreenTextureNIBT.get(pcOwner).close();
				}
				try {
					NativeImage ni = NativeImage.read(new ByteArrayInputStream(screen));
					NativeImageBackedTexture nibt = new NativeImageBackedTexture(ni);
					ClientMod.vmScreenTextures.put(pcOwner, mcc.getTextureManager().registerDynamicTexture("pc_screen_mp", nibt));
					ClientMod.vmScreenTextureNI.put(pcOwner, ni);
					ClientMod.vmScreenTextureNIBT.put(pcOwner, nibt);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			});
		});
		
		ClientSidePacketRegistry.INSTANCE.register(S2C_STOP_SCREEN, (packetContext, attachedData) -> {
			UUID pcOwner = attachedData.readUuid();
			
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient mcc = MinecraftClient.getInstance();
				if(ClientMod.vmScreenTextures.containsKey(pcOwner)) {
					mcc.getTextureManager().destroyTexture(ClientMod.vmScreenTextures.get(pcOwner));
					ClientMod.vmScreenTextureNI.get(pcOwner).close();
					ClientMod.vmScreenTextureNIBT.get(pcOwner).close();
				}
			});
		});
	}
	
	public static void registerServerPackets() {
		ServerSidePacketRegistry.INSTANCE.register(C2S_ORDER, (packetContext, attachedData) -> {
			int arraySize = attachedData.readInt();
			OrderableItem[] items = new OrderableItem[arraySize];
			for(int i = 0;i<arraySize;i++) {
				items[i] = (OrderableItem) attachedData.readItemStack().getItem();
			}
			
			packetContext.getTaskQueue().execute(() -> {
				TabletOrder to = new TabletOrder();
				to.items = Arrays.asList(items);
				MainMod.orders.put(packetContext.getPlayer().getUuid(), to);
			});
		});
	}
}
