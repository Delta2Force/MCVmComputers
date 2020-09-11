package mcvmcomputers.networking;

import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemHarddrive;
import mcvmcomputers.item.ItemList;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

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
	
	public static void removeGpu(EntityPC pc) {
		if(pc.getGpuInstalled()) {
			pc.setGpuInstalled(false);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ItemList.ITEM_GPU)));
		}
	}
	
	public static void removeHdd(EntityPC pc, String uuid) {
		if(!pc.getHardDriveFileName().isEmpty()) {
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), ItemHarddrive.createHardDrive(pc.getHardDriveFileName())));
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
			if(pc.getGigsOfRamInSlot0() == 1024) {
				ramStickItem = ItemList.ITEM_RAM1G;
			}else if(pc.getGigsOfRamInSlot0() == 2048) {
				ramStickItem = ItemList.ITEM_RAM2G;
			}else if(pc.getGigsOfRamInSlot0() == 4096) {
				ramStickItem = ItemList.ITEM_RAM4G;
			}else if(pc.getGigsOfRamInSlot0() == 256) {
				ramStickItem = ItemList.ITEM_RAM256M;
			}else if(pc.getGigsOfRamInSlot0() == 512) {
				ramStickItem = ItemList.ITEM_RAM512M;
			}else if(pc.getGigsOfRamInSlot0() == 128) {
				ramStickItem = ItemList.ITEM_RAM128M;
			}else if(pc.getGigsOfRamInSlot0() == 64) {
				ramStickItem = ItemList.ITEM_RAM64M;
			}else {
				return;
			}
			pc.setGigsOfRamInSlot0(0);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ramStickItem)));
		}else {
			if(pc.getGigsOfRamInSlot1() == 1024) {
				ramStickItem = ItemList.ITEM_RAM1G;
			}else if(pc.getGigsOfRamInSlot1() == 2048) {
				ramStickItem = ItemList.ITEM_RAM2G;
			}else if(pc.getGigsOfRamInSlot1() == 4096) {
				ramStickItem = ItemList.ITEM_RAM4G;
			}else if(pc.getGigsOfRamInSlot1() == 256) {
				ramStickItem = ItemList.ITEM_RAM256M;
			}else if(pc.getGigsOfRamInSlot1() == 512) {
				ramStickItem = ItemList.ITEM_RAM512M;
			}else if(pc.getGigsOfRamInSlot1() == 128) {
				ramStickItem = ItemList.ITEM_RAM128M;
			}else if(pc.getGigsOfRamInSlot1() == 64) {
				ramStickItem = ItemList.ITEM_RAM64M;
			}else {
				return;
			}
			pc.setGigsOfRamInSlot1(0);
			pc.world.spawnEntity(new ItemEntity(pc.world, pc.getX(), pc.getY(), pc.getZ(), new ItemStack(ramStickItem)));
		}
	}
}
