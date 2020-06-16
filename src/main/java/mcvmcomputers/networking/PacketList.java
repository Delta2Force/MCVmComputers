package mcvmcomputers.networking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import io.netty.buffer.Unpooled;
import mcvmcomputers.ClientMod;
import mcvmcomputers.MainMod;
import mcvmcomputers.client.tablet.TabletOrder;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.OrderableItem;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class PacketList {
	//Client-to-server
	public static final Identifier C2S_ORDER = new Identifier("mcvmcomputers", "c2s_order");
	public static final Identifier C2S_SCREEN = new Identifier("mcvmcomputers", "c2s_screen");
	public static final Identifier C2S_TURN_ON_PC = new Identifier("mcvmcomputers", "c2s_turn_on_pc");
	public static final Identifier C2S_TURN_OFF_PC = new Identifier("mcvmcomputers", "c2s_turn_off_pc");
	
	//Server-to-client
	public static final Identifier S2C_SCREEN = new Identifier("mcvmcomputers", "s2c_screen");
	public static final Identifier S2C_STOP_SCREEN = new Identifier("mcvmcomputers", "s2c_stop_screen");
	
	public static void registerClientPackets() {
		ClientSidePacketRegistry.INSTANCE.register(S2C_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int dataSize = attachedData.readInt();
			UUID pcOwner = attachedData.readUuid();
			
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient mcc = MinecraftClient.getInstance();
				if(ClientMod.vmScreenTextures.containsKey(pcOwner)) {
					mcc.getTextureManager().destroyTexture(ClientMod.vmScreenTextures.get(pcOwner));
					ClientMod.vmScreenTextureNI.get(pcOwner).close();
					ClientMod.vmScreenTextureNIBT.get(pcOwner).close();
				}
				try {
					Inflater inf = new Inflater();
					inf.setInput(screen);
					byte[] actualScreen = new byte[dataSize];
					inf.inflate(actualScreen);
					NativeImage ni = NativeImage.read(new ByteArrayInputStream(actualScreen));
					NativeImageBackedTexture nibt = new NativeImageBackedTexture(ni);
					ClientMod.vmScreenTextures.put(pcOwner, mcc.getTextureManager().registerDynamicTexture("pc_screen_mp", nibt));
					ClientMod.vmScreenTextureNI.put(pcOwner, ni);
					ClientMod.vmScreenTextureNIBT.put(pcOwner, nibt);
				} catch (IOException | DataFormatException e) {
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
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int dataSize = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				if(MainMod.computers.containsKey(packetContext.getPlayer().getUuid())) {
					Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(MainMod.computers.get(packetContext.getPlayer().getUuid()));
					PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
					b.writeByteArray(screen);
					b.writeInt(dataSize);
					b.writeUuid(packetContext.getPlayer().getUuid());
					watchingPlayers.forEach((player) -> {
						ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, S2C_SCREEN, b);
					});
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_TURN_ON_PC, (packetContext, attachedData) -> {
			int pcEntityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(pcEntityId);
				if(e != null) {
					if(e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							MainMod.computers.put(packetContext.getPlayer().getUuid(), (EntityPC) e);
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_TURN_OFF_PC, (packetContext, attachedData) -> {
			packetContext.getTaskQueue().execute(() -> {
				if(MainMod.computers.containsKey(packetContext.getPlayer().getUuid())) {
					MainMod.computers.remove(packetContext.getPlayer().getUuid());
				}
			});
		});
	}
}
