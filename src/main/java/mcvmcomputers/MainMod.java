package mcvmcomputers;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.Unpooled;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemHarddrive;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.item.OrderableItem;
import mcvmcomputers.sound.SoundList;
import mcvmcomputers.utils.TabletOrder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;

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
		SoundList.init();
		registerServerPackets();
	}

	public static void registerServerPackets() {
		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ORDER, (packetContext, attachedData) -> {
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

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_SCREEN, (packetContext, attachedData) -> {
			byte[] screen = attachedData.readByteArray();
			int compressedDataSize = attachedData.readInt();
			int dataSize = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				if(MainMod.computers.containsKey(packetContext.getPlayer().getUuid())) {
					Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(MainMod.computers.get(packetContext.getPlayer().getUuid()));
					PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
					b.writeByteArray(screen);
					b.writeInt(compressedDataSize);
					b.writeInt(dataSize);
					b.writeUuid(packetContext.getPlayer().getUuid());
					watchingPlayers.forEach((player) -> ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, S2C_SCREEN, b));
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_TURN_ON_PC, (packetContext, attachedData) -> {
			int pcEntityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(pcEntityId);
				if(e != null) {
					if(e instanceof EntityPC pc) {
						if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							MainMod.computers.put(packetContext.getPlayer().getUuid(), (EntityPC) e);
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_TURN_OFF_PC, (packetContext, attachedData) -> packetContext.getTaskQueue().execute(() -> {
            Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(MainMod.computers.get(packetContext.getPlayer().getUuid()));
			PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
			b.writeUuid(packetContext.getPlayer().getUuid());
			watchingPlayers.forEach((player) -> ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, S2C_STOP_SCREEN, b));
			MainMod.computers.remove(packetContext.getPlayer().getUuid());
		}));

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_CHANGE_HDD, (packetContext, attachedData) -> {
			String newHddName = attachedData.readString(32767);

			packetContext.getTaskQueue().execute(() -> {
				for(ItemStack is : packetContext.getPlayer().getItemsHand()) {
					if(is != null) {
						if(is.getItem() instanceof ItemHarddrive) {
							NbtCompound ct = is.getOrCreateTag();
							ct.putString("vhdfile", newHddName);
							break;
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ADD_MOBO, (packetContext, attachedData) -> {
			boolean x64 = attachedData.readBoolean();
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor;
				if(x64) {lookingFor = ItemList.ITEM_MOTHERBOARD64;} else {lookingFor = ItemList.ITEM_MOTHERBOARD;}
				if(packetContext.getPlayer().getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC pc) {
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(!pc.getMotherboardInstalled()) {
									removeStack(packetContext.getPlayer().getInventory(), new ItemStack(lookingFor));
									pc.setMotherboardInstalled(true);
									pc.set64Bit(x64);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.motherboard_not_present").formatted(Formatting.RED), false);
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ADD_GPU, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = ItemList.ITEM_GPU;
				if(packetContext.getPlayer().getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC pc) {
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(!pc.getGpuInstalled()) {
									removeStack(packetContext.getPlayer().getInventory(), new ItemStack(lookingFor));
									pc.setGpuInstalled(true);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.gpu_not_present").formatted(Formatting.RED), false);
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ADD_CPU, (packetContext, attachedData) -> {
			int dividedBy = attachedData.readInt();
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = null;
				if(dividedBy == 2) {lookingFor = ItemList.ITEM_CPU2;} else if(dividedBy == 4) {lookingFor = ItemList.ITEM_CPU4;} else if(dividedBy == 6) {lookingFor = ItemList.ITEM_CPU6;}
				if(packetContext.getPlayer().getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC pc) {
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(pc.getCpuDividedBy() == 0) {
									removeStack(packetContext.getPlayer().getInventory(), new ItemStack(lookingFor));
									pc.setCpuDividedBy(dividedBy);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.cpu_not_present").formatted(Formatting.RED), false);
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ADD_RAM, (packetContext, attachedData) -> {
			int mb = attachedData.readInt();
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Item lookingFor = null;
				if(mb == 64) {lookingFor = ItemList.ITEM_RAM64M;} else if(mb == 128) {lookingFor = ItemList.ITEM_RAM128M;} else if(mb == 256) {lookingFor = ItemList.ITEM_RAM256M;} else if(mb == 512) {lookingFor = ItemList.ITEM_RAM512M;} else if(mb == 1024) {lookingFor = ItemList.ITEM_RAM1G;} else if(mb == 2048) {lookingFor = ItemList.ITEM_RAM2G;} else if(mb == 4096) {lookingFor = ItemList.ITEM_RAM4G;}
				if(packetContext.getPlayer().getInventory().contains(new ItemStack(lookingFor))) {
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC pc) {
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(pc.getGigsOfRamInSlot0() == 0) {
									removeStack(packetContext.getPlayer().getInventory(), new ItemStack(lookingFor));
									pc.setGigsOfRamInSlot0(mb);
								} else if(pc.getGigsOfRamInSlot1() == 0) {
									removeStack(packetContext.getPlayer().getInventory(), new ItemStack(lookingFor));
									pc.setGigsOfRamInSlot1(mb);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.cpu_not_present").formatted(Formatting.RED), false);
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ADD_HARD_DRIVE, (packetContext, attachedData) -> {
			String vhdname = attachedData.readString(32767);
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				ItemStack lookingFor = ItemHarddrive.createHardDrive(vhdname);
				if(packetContext.getPlayer().getInventory().contains(lookingFor)) {
					Entity e = packetContext.getPlayer().world.getEntityById(entityId);
					if(e != null) {
						if (e instanceof EntityPC pc) {
							if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
								if(pc.getHardDriveFileName().isEmpty()) {
									removeStack(packetContext.getPlayer().getInventory(), lookingFor);
									pc.setHardDriveFileName(vhdname);
								}
							}
						}
					}
				}else {
					packetContext.getPlayer().sendMessage(new TranslatableText("mcvmcomputers.cpu_not_present").formatted(Formatting.RED), false);
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_REMOVE_MOBO, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
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
								removeHdd(pc);
								removeRam(pc, 0);
								removeRam(pc, 1);
							}
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_REMOVE_GPU, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
                        if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeGpu(pc);
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_REMOVE_HARD_DRIVE, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
                        if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeHdd(pc);
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_REMOVE_CPU, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
                        if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeCpu(pc);
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_REMOVE_RAM, (packetContext, attachedData) -> {
			int slot = attachedData.readInt();
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
                        if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							removeRam(pc, slot);
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_ADD_ISO, (packetContext, attachedData) -> {
			String isoName = attachedData.readString(32767);
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
                        if(pc.getOwner().equals(packetContext.getPlayer().getUuid().toString())) {
							if(pc.getIsoFileName().isEmpty()) {
								pc.setIsoFileName(isoName);
							}
						}
					}
				}
			});
		});

		ServerSidePacketRegistryImpl.INSTANCE.register(C2S_REMOVE_ISO, (packetContext, attachedData) -> {
			int entityId = attachedData.readInt();

			packetContext.getTaskQueue().execute(() -> {
				Entity e = packetContext.getPlayer().world.getEntityById(entityId);
				if(e != null) {
					if (e instanceof EntityPC pc) {
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

	private static void removeStack(PlayerInventory inv, ItemStack is) {
        for (DefaultedList<ItemStack> itemStacks : ImmutableList.of(inv.main, inv.armor, inv.offHand)) {
            for (ItemStack itemStack : itemStacks) {
                if (!itemStack.isEmpty() && itemStack.isItemEqualIgnoreDamage(is)) {
                    itemStack.decrement(1);
                    return;
                }
            }
        }
		throw new RuntimeException("Doesn't contain item!");
	}
}
