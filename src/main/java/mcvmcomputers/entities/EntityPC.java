package mcvmcomputers.entities;

import mcvmcomputers.MainMod;
import mcvmcomputers.client.gui.GuiPCEditing;
import mcvmcomputers.item.ItemHarddrive;
import mcvmcomputers.item.ItemList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPC extends Entity{
	private static final TrackedData<String> ISO_FILE_NAME =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> HARD_DRIVE_FILE_NAME =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);
	
	private static final TrackedData<Float> LOOK_AT_POS_X =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> LOOK_AT_POS_Y =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> LOOK_AT_POS_Z =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.FLOAT);
	
	private static final TrackedData<Integer> CPU_DIVIDED_BY =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> GB_OF_RAM_IN_SLOT_0 =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> GB_OF_RAM_IN_SLOT_1 =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.INTEGER);
	
	private static final TrackedData<Boolean> SIXTY_FOUR_BIT =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> GPU_IN_PCI_SLOT =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> GLASS_SIDEPANEL =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> MOTHERBOARD_INSTALLED =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.BOOLEAN);
	
	public EntityPC(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public EntityPC(World world, double x, double y, double z) {
		this(EntityList.PC, world);
		this.updatePosition(x, y, z);
	}
	
	public EntityPC(World world, double x, double y, double z, Vec3d lookAt) {
		this(EntityList.PC, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(LOOK_AT_POS_X, (float)lookAt.x);
		this.getDataTracker().set(LOOK_AT_POS_Y, (float)lookAt.y);
		this.getDataTracker().set(LOOK_AT_POS_Z, (float)lookAt.z);
	}
	
	public EntityPC(World world, double x, double y, double z, Vec3d lookAt, boolean glassSidepanel) {
		this(EntityList.PC, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(LOOK_AT_POS_X, (float)lookAt.x);
		this.getDataTracker().set(LOOK_AT_POS_Y, (float)lookAt.y);
		this.getDataTracker().set(LOOK_AT_POS_Z, (float)lookAt.z);
		this.getDataTracker().set(GLASS_SIDEPANEL, glassSidepanel);
	}
	
	public Vec3d getLookAtPos() {
		return new Vec3d(this.getDataTracker().get(LOOK_AT_POS_X), this.getDataTracker().get(LOOK_AT_POS_Y), this.getDataTracker().get(LOOK_AT_POS_Z));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(HARD_DRIVE_FILE_NAME, "");
		this.getDataTracker().startTracking(ISO_FILE_NAME, "");
		this.getDataTracker().startTracking(LOOK_AT_POS_X, 0f);
		this.getDataTracker().startTracking(LOOK_AT_POS_Y, 0f);
		this.getDataTracker().startTracking(LOOK_AT_POS_Z, 0f);
		this.getDataTracker().startTracking(GB_OF_RAM_IN_SLOT_0, 0);
		this.getDataTracker().startTracking(GB_OF_RAM_IN_SLOT_1, 0);
		this.getDataTracker().startTracking(CPU_DIVIDED_BY, 0);
		this.getDataTracker().startTracking(GPU_IN_PCI_SLOT, false);
		this.getDataTracker().startTracking(MOTHERBOARD_INSTALLED, false);
		this.getDataTracker().startTracking(GLASS_SIDEPANEL, false);
		this.getDataTracker().startTracking(SIXTY_FOUR_BIT, false);
	}
	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.getDataTracker().set(LOOK_AT_POS_X, tag.getFloat("LookAtX"));
		this.getDataTracker().set(LOOK_AT_POS_Y, tag.getFloat("LookAtY"));
		this.getDataTracker().set(LOOK_AT_POS_Z, tag.getFloat("LookAtZ"));
		
		if(tag.contains("X64")) {
			this.getDataTracker().set(SIXTY_FOUR_BIT, tag.getBoolean("X64"));
		}
		
		if(tag.contains("CpuDividedBy")) {
			this.getDataTracker().set(CPU_DIVIDED_BY, tag.getInt("CpuDividedBy"));
		}
		
		if(tag.contains("IsoFileName")) {
			this.getDataTracker().set(ISO_FILE_NAME, tag.getString("IsoFileName"));
		}
		
		if(tag.contains("GbRamSlot0")) {
			this.getDataTracker().set(GB_OF_RAM_IN_SLOT_0, tag.getInt("GbRamSlot0"));
		}
		
		if(tag.contains("GbRamSlot1")) {
			this.getDataTracker().set(GB_OF_RAM_IN_SLOT_1, tag.getInt("GbRamSlot1"));
		}
		
		if(tag.contains("GpuInstalled")) {
			this.getDataTracker().set(GPU_IN_PCI_SLOT, tag.getBoolean("GpuInstalled"));
		}
		
		if(tag.contains("HardDriveFileName")) {
			this.getDataTracker().set(HARD_DRIVE_FILE_NAME, tag.getString("HardDriveFileName"));
		}
		
		if(tag.contains("MotherboardInstalled")) {
			this.getDataTracker().set(MOTHERBOARD_INSTALLED, tag.getBoolean("MotherboardInstalled"));
		}
	}
	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putBoolean("X64", this.getDataTracker().get(SIXTY_FOUR_BIT));
		tag.putFloat("LookAtX", this.getDataTracker().get(LOOK_AT_POS_X));
		tag.putFloat("LookAtY", this.getDataTracker().get(LOOK_AT_POS_Y));
		tag.putFloat("LookAtZ", this.getDataTracker().get(LOOK_AT_POS_Z));
		tag.putInt("CpuDividedBy", this.getDataTracker().get(CPU_DIVIDED_BY));
		tag.putString("IsoFileName", this.getDataTracker().get(ISO_FILE_NAME));
		tag.putInt("GbRamSlot0", this.getDataTracker().get(GB_OF_RAM_IN_SLOT_0));
		tag.putInt("GbRamSlot1", this.getDataTracker().get(GB_OF_RAM_IN_SLOT_1));
		tag.putBoolean("GpuInstalled", this.getDataTracker().get(GPU_IN_PCI_SLOT));
		tag.putString("HardDriveFileName", this.getDataTracker().get(HARD_DRIVE_FILE_NAME));
		tag.putBoolean("MotherboardInstalled", this.getDataTracker().get(MOTHERBOARD_INSTALLED));
	}
	
	public String getHardDriveFileName() { return this.getDataTracker().get(HARD_DRIVE_FILE_NAME); }
	public String getIsoFileName() { return this.getDataTracker().get(ISO_FILE_NAME); }
	public int getGigsOfRamInSlot0() { return this.getDataTracker().get(GB_OF_RAM_IN_SLOT_0); }
	public int getGigsOfRamInSlot1() { return this.getDataTracker().get(GB_OF_RAM_IN_SLOT_1); }
	public int getCpuDividedBy() { return this.getDataTracker().get(CPU_DIVIDED_BY); }
	public boolean getGpuInstalled() { return this.getDataTracker().get(GPU_IN_PCI_SLOT); }
	public boolean getMotherboardInstalled() { return this.getDataTracker().get(MOTHERBOARD_INSTALLED); }
	public boolean getGlassSidepanel() { return this.getDataTracker().get(GLASS_SIDEPANEL); }
	public boolean get64Bit() { return this.getDataTracker().get(SIXTY_FOUR_BIT); }
	
	public void setGigsOfRamInSlot0(int gb) { this.getDataTracker().set(GB_OF_RAM_IN_SLOT_0, gb); }
	public void setGigsOfRamInSlot1(int gb) { this.getDataTracker().set(GB_OF_RAM_IN_SLOT_1, gb); }
	public void setGpuInstalled(boolean installed) { this.getDataTracker().set(GPU_IN_PCI_SLOT, installed); }
	public void setCpuDividedBy(int dividedBy) { this.getDataTracker().set(CPU_DIVIDED_BY, dividedBy); }
	public void setHardDriveFileName(String fileName) { this.getDataTracker().set(HARD_DRIVE_FILE_NAME, fileName); }
	public void setIsoFileName(String fileName) { this.getDataTracker().set(ISO_FILE_NAME, fileName); }
	public void setMotherboardInstalled(boolean installed) { this.getDataTracker().set(MOTHERBOARD_INSTALLED, installed); }
	public void set64Bit(boolean sixtyFourBit) { this.getDataTracker().set(SIXTY_FOUR_BIT, sixtyFourBit); }
	
	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if(!player.world.isClient) {
			if(player.isSneaking() && MainMod.vmEntityID != this.getEntityId()) {
				this.kill();
				if(this.getGlassSidepanel()) {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(ItemList.PC_CASE_SIDEPANEL)));
				}else {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(ItemList.PC_CASE)));
				}
				if(this.getCpuDividedBy() > 0) {
					Item i = null;
					switch(this.getGigsOfRamInSlot0()) {
					case 2:
						i = ItemList.ITEM_CPU2;
						break;
					case 4:
						i = ItemList.ITEM_CPU4;
						break;
					case 6:
						i = ItemList.ITEM_CPU6;
						break;
					default:
						i = Items.AIR;
						break;
					}
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(i)));
				}
				if(this.getGpuInstalled()) {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(ItemList.ITEM_GPU)));
				}
				if(!this.getHardDriveFileName().isEmpty()) {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							ItemHarddrive.createHardDrive(this.getHardDriveFileName())));
				}
				if(this.getMotherboardInstalled()) {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(ItemList.ITEM_MOTHERBOARD)));
				}
				if(this.getGigsOfRamInSlot0() > 0) {
					Item i = null;
					switch(this.getGigsOfRamInSlot0()) {
					case 1:
						i = ItemList.ITEM_RAM1G;
						break;
					case 2:
						i = ItemList.ITEM_RAM2G;
						break;
					case 4:
						i = ItemList.ITEM_RAM4G;
						break;
					default:
						i = Items.AIR;
						break;
					}
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(i)));
				}
				if(this.getGigsOfRamInSlot1() > 0) {
					Item i = null;
					switch(this.getGigsOfRamInSlot1()) {
					case 1:
						i = ItemList.ITEM_RAM1G;
						break;
					case 2:
						i = ItemList.ITEM_RAM2G;
						break;
					case 4:
						i = ItemList.ITEM_RAM4G;
						break;
					default:
						i = Items.AIR;
						break;
					}
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
							new ItemStack(i)));
				}
			}
		}else {
			if(!player.isSneaking())
				MinecraftClient.getInstance().openScreen(new GuiPCEditing(this));
		}
		return true;
	}
	
	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}