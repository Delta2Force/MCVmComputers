package mcvmcomputers.networking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import io.netty.buffer.Unpooled;
import mcvmcomputers.ClientMod;
import mcvmcomputers.MainMod;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemHarddrive;
import mcvmcomputers.item.OrderableItem;
import mcvmcomputers.utils.TabletOrder;
import mcvmcomputers.utils.TabletOrder.OrderStatus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class PacketList {
	//Client-to-server
	public static final Identifier C2S_ORDER = new Identifier("mcvmcomputers", "c2s_order");
	public static final Identifier C2S_SCREEN = new Identifier("mcvmcomputers", "c2s_screen");
	public static final Identifier C2S_CHANGE_HDD = new Identifier("mcvmcomputers", "c2s_change_hdd");
	public static final Identifier C2S_TURN_ON_PC = new Identifier("mcvmcomputers", "c2s_turn_on_pc");
	public static final Identifier C2S_TURN_OFF_PC = new Identifier("mcvmcomputers", "c2s_turn_off_pc");
	
	//Server-to-client
	public static final Identifier S2C_SCREEN = new Identifier("mcvmcomputers", "s2c_screen");
	public static final Identifier S2C_STOP_SCREEN = new Identifier("mcvmcomputers", "s2c_stop_screen");
	public static final Identifier S2C_SYNC_ORDER = new Identifier("mcvmcomputers", "s2c_sync_order");
	
	public static void registerClientPackets() {
		ClientSidePacketRegistry.INSTANCE.register(S2C_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int dataSize = attachedData.readInt();
			UUID pcOwner = attachedData.readUuid();
			
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient mcc = MinecraftClient.getInstance();
				if(!pcOwner.equals(mcc.player.getUuid())) {
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
		
		ClientSidePacketRegistry.INSTANCE.register(S2C_SYNC_ORDER, (packetContext, attachedData) -> {
			int arraySize = attachedData.readInt();
			OrderableItem[] arr = new OrderableItem[arraySize];
			for(int i = 0;i<arraySize;i++) {
				arr[i] = (OrderableItem) attachedData.readItemStack().getItem();
			}
			int price = attachedData.readInt();
			OrderStatus status = OrderStatus.values()[attachedData.readInt()]; //send ordinal
			
			packetContext.getTaskQueue().execute(() -> {
				if(ClientMod.myOrder == null) {
					ClientMod.myOrder = new TabletOrder();
				}
				ClientMod.myOrder.price = price;
				ClientMod.myOrder.items = Arrays.asList(arr);
				ClientMod.myOrder.orderUUID = packetContext.getPlayer().getUuid().toString();
				ClientMod.myOrder.currentStatus = status;
			});
		});
	}
	
	public static void registerServerPackets() {
		ServerSidePacketRegistry.INSTANCE.register(C2S_ORDER, (packetContext, attachedData) -> {
			int arraySize = attachedData.readInt();
			OrderableItem[] items = new OrderableItem[arraySize];
			int price = 0;
			for(int i = 0;i<arraySize;i++) {
				items[i] = (OrderableItem) attachedData.readItemStack().getItem();
				price += items[i].getPrice();
			}
			
			final int pr = price;
			
			packetContext.getTaskQueue().execute(() -> {
				TabletOrder to = new TabletOrder();
				to.items = Arrays.asList(items);
				to.price = pr;
				to.orderUUID = packetContext.getPlayer().getUuid().toString();
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
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_CHANGE_HDD, (packetContext, attachedData) -> {
			String newHddName = attachedData.readString();
			
			packetContext.getTaskQueue().execute(() -> {
				for(ItemStack is : packetContext.getPlayer().getItemsHand()) {
					if(is != null) {
						if(is.getItem() instanceof ItemHarddrive) {
							CompoundTag ct = is.getTag();
							ct.putString("vhdfile", newHddName);
							ct.putUuid("owner", packetContext.getPlayer().getUuid());
							is.setTag(ct);
							break;
						}
					}
				}
			});
		});
	}
}
