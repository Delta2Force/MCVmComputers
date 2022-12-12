package mcvmcomputers;

import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import io.netty.buffer.Unpooled;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemHardDrive;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.item.OrderableItem;
import mcvmcomputers.sound.SoundList;
import mcvmcomputers.utils.TabletOrder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import static mcvmcomputers.networking.PacketList.*;

public class MainMod implements ModInitializer{
	public static Map<UUID, TabletOrder> orders;
	public static Map<UUID, EntityPC> computers;
	
	public static Runnable hardDriveClick = () -> {};
	public static Runnable deliveryChestSound = () -> {};
	public static Runnable focus = () -> {};
	public static Runnable pcOpenGui = () -> {};
	
	public void onInitialize() {
		orders = new HashMap<>();
		computers = new HashMap<>();
		ItemList.init();
		EntityList.init();
		SoundList.init();
		registerServerPackets();
	}
	
	public static void registerServerPackets() {
		ServerPlayNetworking.registerGlobalReceiver(C2S_ORDER, (server, player, handler, buf, responseSender) -> {
			int arraySize = buf.readInt();
			OrderableItem[] items = new OrderableItem[arraySize];
			int price = 0;
			for(int i = 0; i < arraySize; i++) {
				items[i] = (OrderableItem) buf.readItemStack().getItem();
				price += items[i].getPrice();
			}
			int finalPrice = price;
			server.execute(()->{
				TabletOrder to = new TabletOrder();
				to.items = new ArrayList<>();
				to.items.addAll(List.of(items));
				to.price = finalPrice;
				to.orderUUID = player.getUuidAsString();
				MainMod.orders.put(player.getUuid(), to);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_SCREEN, (server, player, handler, buf, responseSender) -> {
			byte[] screen = buf.readByteArray();
			int compressedDataSize = buf.readInt();
			int dataSize = buf.readInt();
			
			server.execute(() -> {
				if(MainMod.computers.containsKey(player.getUuid())) {
					Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(MainMod.computers.get(player.getUuid()));
					PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
					b.writeByteArray(screen);
					b.writeInt(compressedDataSize);
					b.writeInt(dataSize);
					b.writeUuid(player.getUuid());
					watchingPlayers.forEach(p -> ServerPlayNetworking.send(p, S2C_SCREEN, b));
				}
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_TURN_ON_PC, (server, player, handler, buf, responseSender) -> {
			int pcEntityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(pcEntityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()))
					MainMod.computers.put(player.getUuid(), (EntityPC) e);
			});
		});

		ServerPlayNetworking.registerGlobalReceiver(C2S_TURN_ON_PC, (server, player, handler, buf, responseSender) -> {
			server.execute(() -> {
				Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(MainMod.computers.get(player.getUuid()));
				PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
				b.writeUuid(player.getUuid());
				watchingPlayers.forEach(p -> ServerPlayNetworking.send(p, S2C_STOP_SCREEN, b));
				MainMod.computers.remove(player.getUuid());
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_CHANGE_HDD, (server, player, handler, buf, responseSender) -> {
			String newHddName = buf.readString(32767);
			
			server.execute(() -> {
				for(ItemStack is : player.getHandItems()) {
					if(is != null)
						if(is.getItem() instanceof ItemHardDrive) {
							NbtCompound nc = is.getOrCreateNbt();
							nc.putString("vhdfile", newHddName);
							break;
						}
				}
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_ADD_MOBO, (server, player, handler, buf, responseSender) -> {
			boolean x64 = buf.readBoolean();
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Item lookingFor;
				if (x64)
					lookingFor = ItemList.ITEM_MOTHERBOARD64;
				else
					lookingFor = ItemList.ITEM_MOTHERBOARD;

				if(player.getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = player.world.getEntityById(entityId);
					if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && !pc.getMotherboardInstalled()) {
						removeStick(player.getInventory(), new ItemStack(lookingFor));
						pc.setMotherboardInstalled(true);
						pc.set64Bit(x64);
					}
				} else
					player.sendMessage(Text.translatable("mcvmcomputers.motherboard_not_present").formatted(Formatting.RED), false);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_ADD_GPU, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Item lookingFor = ItemList.ITEM_GPU;
				if(player.getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = player.world.getEntityById(entityId);
					if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && !pc.getGpuInstalled()) {
						removeStick(player.getInventory(), new ItemStack(lookingFor));
						pc.setGpuInstalled(true);
					}
				} else
					player.sendMessage(Text.translatable("mcvmcomputers.gpu_not_present").formatted(Formatting.RED), false);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_ADD_CPU, (server, player, handler, buf, responseSender) -> {
			int dividedBy = buf.readInt();
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Item lookingFor = switch (dividedBy) {
					case 2 -> ItemList.ITEM_CPU2;
					case 4 -> ItemList.ITEM_CPU4;
					case 6 -> ItemList.ITEM_CPU6;
					default -> null;
				};

				if(player.getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = player.world.getEntityById(entityId);
					if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && pc.getCpuDividedBy() == 0) {
						removeStick(player.getInventory(), new ItemStack(lookingFor));
						pc.setCpuDividedBy(dividedBy);
					}
				} else
					player.sendMessage(Text.translatable("mcvmcomputers.cpu_not_present").formatted(Formatting.RED), false);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_ADD_RAM, (server, player, handler, buf, responseSender) -> {
			int mb = buf.readInt();
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Item lookingFor = switch (mb) {
					case 64 -> ItemList.ITEM_RAM64M;
					case 128 -> ItemList.ITEM_RAM128M;
					case 256 -> ItemList.ITEM_RAM256M;
					case 512 -> ItemList.ITEM_RAM512M;
					case 1024 -> ItemList.ITEM_RAM1G;
					case 2048 -> ItemList.ITEM_RAM2G;
					case 4096 -> ItemList.ITEM_RAM4G;
					default -> null;
				};

				if(player.getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = player.world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC pc) {
							if(pc.getOwner().equals(player.getUuidAsString())) {
								if(pc.getGigsOfRamInSlot0() == 0) {
									removeStick(player.getInventory(), new ItemStack(lookingFor));
									pc.setGigsOfRamInSlot0(mb);
								} else if(pc.getGigsOfRamInSlot1() == 0) {
									removeStick(player.getInventory(), new ItemStack(lookingFor));
									pc.setGigsOfRamInSlot1(mb);
								}
							}
						}
					}
				} else
					player.sendMessage(Text.translatable("mcvmcomputers.cpu_not_present").formatted(Formatting.RED), false);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_ADD_HARD_DRIVE, (server, player, handler, buf, responseSender) -> {
			String vhdName = buf.readString(32767);
			int entityId = buf.readInt();
			
			server.execute(() -> {
				ItemStack lookingFor = ItemHardDrive.createHardDrive(vhdName);
				if(player.getInventory().contains(lookingFor)) {
					Entity e = player.world.getEntityById(entityId);
					if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && pc.getHardDriveFileName().isEmpty()) {
						removeStick(player.getInventory(), lookingFor);
						pc.setHardDriveFileName(vhdName);
					}
				} else
					player.sendMessage(Text.translatable("mcvmcomputers.cpu_not_present").formatted(Formatting.RED), false);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_REMOVE_MOBO, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && pc.getMotherboardInstalled()) {
					pc.setMotherboardInstalled(false);
					if(pc.get64Bit())
						pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ItemList.ITEM_MOTHERBOARD64)));
					else
						pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ItemList.ITEM_MOTHERBOARD)));

					removeCpu(pc);
					removeGpu(pc);
					removeHdd(pc, player.getUuidAsString());
					removeRam(pc, 0);
					removeRam(pc, 1);
				}
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_REMOVE_GPU, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()))
					removeGpu(pc);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_REMOVE_HARD_DRIVE, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()))
					removeHdd(pc, player.getUuidAsString());
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_REMOVE_CPU, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()))
					removeCpu(pc);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_REMOVE_RAM, (server, player, handler, buf, responseSender) -> {
			int slot = buf.readInt();
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()))
					removeRam(pc, slot);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_ADD_ISO, (server, player, handler, buf, responseSender) -> {
			String isoName = buf.readString(32767);
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && pc.getIsoFileName().isEmpty())
					pc.setIsoFileName(isoName);
			});
		});
		
		ServerPlayNetworking.registerGlobalReceiver(C2S_REMOVE_ISO, (server, player, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			
			server.execute(() -> {
				Entity e = player.world.getEntityById(entityId);
				if(e instanceof EntityPC pc && pc.getOwner().equals(player.getUuidAsString()) && !pc.getIsoFileName().isEmpty())
					pc.setIsoFileName("");
			});
		});
	}
	
	private static void removeStick(PlayerInventory inv, ItemStack is) {
		for (List<ItemStack> list : ImmutableList.of(inv.main, inv.armor, inv.offHand)) {
			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && itemStack.isItemEqualIgnoreDamage(is)) {
					itemStack.decrement(1);
					return;
				}
			}
		}
		throw new RuntimeException("Doesn't contain item!");
	}
}
