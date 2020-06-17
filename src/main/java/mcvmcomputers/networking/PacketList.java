package mcvmcomputers.networking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import mcvmcomputers.item.ItemList;
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
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class PacketList {
	//Client-to-server
	public static final Identifier C2S_ORDER = new Identifier("mcvmcomputers", "c2s_order");
	public static final Identifier C2S_SCREEN = new Identifier("mcvmcomputers", "c2s_screen");
	public static final Identifier C2S_CHANGE_HDD = new Identifier("mcvmcomputers", "c2s_change_hdd");
	public static final Identifier C2S_TURN_ON_PC = new Identifier("mcvmcomputers", "c2s_turn_on_pc");
	public static final Identifier C2S_TURN_OFF_PC = new Identifier("mcvmcomputers", "c2s_turn_off_pc");
	
	public static final Identifier C2S_ADD_MOBO = new Identifier("mcvmcomputers", "c2s_add_mobo");
	public static final Identifier C2S_ADD_RAM = new Identifier("mcvmcomputers", "c2s_add_ram");
	public static final Identifier C2S_ADD_CPU = new Identifier("mcvmcomputers", "c2s_add_cpu");
	public static final Identifier C2S_ADD_GPU = new Identifier("mcvmcomputers", "c2s_add_gpu");
	public static final Identifier C2S_ADD_HARD_DRIVE = new Identifier("mcvmcomputers", "c2s_add_hard_drive");
	public static final Identifier C2S_ADD_ISO = new Identifier("mcvmcomputers", "c2s_add_iso");
	
	public static final Identifier C2S_REMOVE_MOBO = new Identifier("mcvmcomputers", "c2s_remove_mobo");
	public static final Identifier C2S_REMOVE_RAM = new Identifier("mcvmcomputers", "c2s_remove_ram");
	public static final Identifier C2S_REMOVE_CPU = new Identifier("mcvmcomputers", "c2s_remove_cpu");
	public static final Identifier C2S_REMOVE_GPU = new Identifier("mcvmcomputers", "c2s_remove_gpu");
	public static final Identifier C2S_REMOVE_HARD_DRIVE = new Identifier("mcvmcomputers", "c2s_remove_hard_drive");
	public static final Identifier C2S_REMOVE_ISO = new Identifier("mcvmcomputers", "c2s_remove_iso");
	
	//Server-to-client
	public static final Identifier S2C_SCREEN = new Identifier("mcvmcomputers", "s2c_screen");
	public static final Identifier S2C_STOP_SCREEN = new Identifier("mcvmcomputers", "s2c_stop_screen");
	public static final Identifier S2C_SYNC_ORDER = new Identifier("mcvmcomputers", "s2c_sync_order");
	
	public static void registerClientPackets() {
		ClientSidePacketRegistry.INSTANCE.register(S2C_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int compressedDataSize = attachedData.readInt();
			int dataSize = attachedData.readInt();
			UUID pcOwner = attachedData.readUuid();
			
			packetContext.getTaskQueue().execute(() -> {
				MinecraftClient mcc = MinecraftClient.getInstance();
				if(!pcOwner.equals(mcc.player.getUuid())) {
					if(ClientMod.vmScreenTextures.containsKey(pcOwner)) {
						mcc.getTextureManager().destroyTexture(ClientMod.vmScreenTextures.get(pcOwner));
					}
					if(ClientMod.vmScreenTextureNI.containsKey(pcOwner)) {
						ClientMod.vmScreenTextureNI.get(pcOwner).close();
					}
					if(ClientMod.vmScreenTextureNIBT.containsKey(pcOwner)) {
						ClientMod.vmScreenTextureNI.get(pcOwner).close();
					}
					try {
						Inflater inf = new Inflater();
						inf.setInput(screen, 0, compressedDataSize);
						byte[] actualScreen = new byte[dataSize+1];
						int size = inf.inflate(actualScreen);
						inf.end();
						NativeImage ni = NativeImage.read(new ByteArrayInputStream(actualScreen, 0, size));
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
				}
				if(ClientMod.vmScreenTextureNI.containsKey(pcOwner)) {
					ClientMod.vmScreenTextureNI.get(pcOwner).close();
				}
				if(ClientMod.vmScreenTextureNIBT.containsKey(pcOwner)) {
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
				to.items = new ArrayList<>();
				to.items.addAll(Arrays.asList(items));
				to.price = pr;
				to.orderUUID = packetContext.getPlayer().getUuid().toString();
				MainMod.orders.put(packetContext.getPlayer().getUuid(), to);
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int compressedDataSize = attachedData.readInt();
			int dataSize = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				if(MainMod.computers.containsKey(packetContext.getPlayer().getUuid())) {
					Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(MainMod.computers.get(packetContext.getPlayer().getUuid()));
					PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
					b.writeByteArray(screen);
					b.writeInt(compressedDataSize);
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
				
				Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(MainMod.computers.get(packetContext.getPlayer().getUuid()));
				PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
				b.writeUuid(packetContext.getPlayer().getUuid());
				watchingPlayers.forEach((player) -> {
					ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, S2C_STOP_SCREEN, b);
				});
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_CHANGE_HDD, (packetContext, attachedData) -> {
			String newHddName = attachedData.readString();
			
			packetContext.getTaskQueue().execute(() -> {
				for(ItemStack is : packetContext.getPlayer().getItemsHand()) {
					if(is != null) {
						if(is.getItem() instanceof ItemHarddrive) {
							CompoundTag ct = is.getOrCreateTag();
							ct.putString("vhdfile", newHddName);
							ct.putUuid("owner", packetContext.getPlayer().getUuid());
							break;
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_ADD_MOBO, (packetContext, attachedData) -> {
			boolean x64 = attachedData.readBoolean();
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = null;
				if(x64) {lookingFor = ItemList.ITEM_MOTHERBOARD64;} else {lookingFor = ItemList.ITEM_MOTHERBOARD;}
				if(packetContext.getPlayer().inventory.contains(new ItemStack(lookingFor))) {
					ItemStack inInv = packetContext.getPlayer().inventory.getInvStack(packetContext.getPlayer().inventory.getSlotWithStack(new ItemStack(lookingFor)));
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC) {
							EntityPC pc = (EntityPC) e;
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(!pc.getMotherboardInstalled()) {
									inInv.decrement(1);
									pc.setMotherboardInstalled(true);
									pc.set64Bit(x64);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.motherboard_not_present").formatted(Formatting.RED));
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_ADD_GPU, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = ItemList.ITEM_GPU;
				if(packetContext.getPlayer().inventory.contains(new ItemStack(lookingFor))) {
					ItemStack inInv = packetContext.getPlayer().inventory.getInvStack(packetContext.getPlayer().inventory.getSlotWithStack(new ItemStack(lookingFor)));
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC) {
							EntityPC pc = (EntityPC) e;
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(!pc.getGpuInstalled()) {
									inInv.decrement(1);
									pc.setGpuInstalled(true);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.gpu_not_present").formatted(Formatting.RED));
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_ADD_CPU, (packetContext, attachedData) -> {
			int dividedBy = attachedData.readInt();
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = null;
				if(dividedBy == 2) {lookingFor = ItemList.ITEM_CPU2;} else if(dividedBy == 4) {lookingFor = ItemList.ITEM_CPU4;} else if(dividedBy == 6) {lookingFor = ItemList.ITEM_CPU6;}
				if(packetContext.getPlayer().inventory.contains(new ItemStack(lookingFor))) {
					ItemStack inInv = packetContext.getPlayer().inventory.getInvStack(packetContext.getPlayer().inventory.getSlotWithStack(new ItemStack(lookingFor)));
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC) {
							EntityPC pc = (EntityPC) e;
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(pc.getCpuDividedBy() == 0) {
									inInv.decrement(1);
									pc.setCpuDividedBy(dividedBy);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.cpu_not_present").formatted(Formatting.RED));
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_ADD_RAM, (packetContext, attachedData) -> {
			int gb = attachedData.readInt();
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = null;
				if(gb == 1) {lookingFor = ItemList.ITEM_RAM1G;} else if(gb == 2) {lookingFor = ItemList.ITEM_RAM2G;} else if(gb == 4) {lookingFor = ItemList.ITEM_RAM4G;}
				if(packetContext.getPlayer().inventory.contains(new ItemStack(lookingFor))) {
					ItemStack inInv = packetContext.getPlayer().inventory.getInvStack(packetContext.getPlayer().inventory.getSlotWithStack(new ItemStack(lookingFor)));
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC) {
							EntityPC pc = (EntityPC) e;
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(pc.getGigsOfRamInSlot0() == 0) {
									inInv.decrement(1);
									pc.setGigsOfRamInSlot0(gb);
								} else if(pc.getGigsOfRamInSlot1() == 0) {
									inInv.decrement(1);
									pc.setGigsOfRamInSlot1(gb);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.cpu_not_present").formatted(Formatting.RED));
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_ADD_HARD_DRIVE, (packetContext, attachedData) -> {
			String vhdname = attachedData.readString();
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				ItemStack lookingFor = ItemHarddrive.createHardDrive(vhdname, packetContext.getPlayer().getUuid().toString());
				if(packetContext.getPlayer().inventory.contains(lookingFor)) {
					ItemStack inInv = packetContext.getPlayer().inventory.getInvStack(packetContext.getPlayer().inventory.getSlotWithStack(lookingFor));
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC) {
							EntityPC pc = (EntityPC) e;
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(pc.getHardDriveFileName().isEmpty()) {
									inInv.decrement(1);
									pc.setHardDriveFileName(vhdname);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.cpu_not_present").formatted(Formatting.RED));
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_REMOVE_MOBO, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							if(pc.getMotherboardInstalled()) {
								pc.setMotherboardInstalled(false);
								if(pc.get64Bit()) {
									pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ItemList.ITEM_MOTHERBOARD64)));
								}else {
									pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ItemList.ITEM_MOTHERBOARD)));
								}
								removeCpu(pc);
								removeGpu(pc);
								removeHdd(pc, packetContext.getPlayer().getUuid().toString());
								removeRam(pc, 0);
								removeRam(pc, 1);
							}
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_REMOVE_GPU, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeGpu(pc);
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_REMOVE_HARD_DRIVE, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeHdd(pc, packetContext.getPlayer().getUuid().toString());
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_REMOVE_CPU, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeCpu(pc);
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_REMOVE_RAM, (packetContext, attachedData) -> {
			int slot = attachedData.readInt();
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeRam(pc, slot);
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_ADD_ISO, (packetContext, attachedData) -> {
			String isoName = attachedData.readString();
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							if(pc.getIsoFileName().isEmpty()) {
								pc.setIsoFileName(isoName);
							}
						}
					}
				}
			});
		});
		
		ServerSidePacketRegistry.INSTANCE.register(C2S_REMOVE_ISO, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();
			
			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC) {
						EntityPC pc = (EntityPC) e;
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							if(!pc.getIsoFileName().isEmpty()) {
								pc.setIsoFileName("");
							}
						}
					}
				}
			});
		});
	}
	
	public static void removeGpu(EntityPC pc) {
		if(pc.getGpuInstalled()) {
			pc.setGpuInstalled(false);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ItemList.ITEM_GPU)));
		}
	}
	
	public static void removeHdd(EntityPC pc, String uuid) {
		if(!pc.getHardDriveFileName().isEmpty()) {
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), ItemHarddrive.createHardDrive(pc.getHardDriveFileName(), uuid)));
			pc.setHardDriveFileName("");
		}
	}
	
	public static void removeCpu(EntityPC pc) {
		if(pc.getCpuDividedBy() > 0) {
			Item cpuItem = null;
			switch(pc.getCpuDividedBy()) {
			case 2:
				cpuItem = ItemList.ITEM_CPU2;
				break;
			case 4:
				cpuItem = ItemList.ITEM_CPU4;
				break;
			case 6:
				cpuItem = ItemList.ITEM_CPU6;
				break;
			default:
				return;
			}
			pc.setCpuDividedBy(0);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(cpuItem)));
		}
	}
	
	public static void removeRam(EntityPC pc, int slot) {
		Item ramStickItem = null;
		if(slot == 0) {
			if(pc.getGigsOfRamInSlot0() == 1) {
				ramStickItem = ItemList.ITEM_RAM1G;
			}else if(pc.getGigsOfRamInSlot0() == 2) {
				ramStickItem = ItemList.ITEM_RAM2G;
			}else if(pc.getGigsOfRamInSlot0() == 4) {
				ramStickItem = ItemList.ITEM_RAM4G;
			}else {
				return;
			}
			pc.setGigsOfRamInSlot0(0);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ramStickItem)));
		}else {
			if(pc.getGigsOfRamInSlot1() == 1) {
				ramStickItem = ItemList.ITEM_RAM1G;
			}else if(pc.getGigsOfRamInSlot1() == 2) {
				ramStickItem = ItemList.ITEM_RAM2G;
			}else if(pc.getGigsOfRamInSlot1() == 4) {
				ramStickItem = ItemList.ITEM_RAM4G;
			}else {
				return;
			}
			pc.setGigsOfRamInSlot1(0);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ramStickItem)));
		}
	}
}
