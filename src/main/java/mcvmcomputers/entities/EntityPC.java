package mcvmcomputers.entities;

import java.util.UUID;

import mcvmcomputers.MainMod;
import mcvmcomputers.client.ClientMod;
import mcvmcomputers.item.ItemPCCase;
import mcvmcomputers.item.ItemPCCaseSidepanel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPC extends Entity{
	private static final TrackedData<String> ISO_FILE_NAME =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> HARD_DRIVE_FILE_NAME =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> OWNER_UUID =
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
	
	public EntityPC(World world, double x, double y, double z, Vec3d lookAt, UUID owner, CompoundTag tag) {
		this(EntityList.PC, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(LOOK_AT_POS_X, (float)lookAt.x);
		this.getDataTracker().set(LOOK_AT_POS_Y, (float)lookAt.y);
		this.getDataTracker().set(LOOK_AT_POS_Z, (float)lookAt.z);
		this.getDataTracker().set(OWNER_UUID, owner.toString());
		
		if(tag != null) {
			if(tag.contains("x64"))
				this.getDataTracker().set(SIXTY_FOUR_BIT, tag.getBoolean("x64"));
			if(tag.contains("MoboInstalled"))
				this.getDataTracker().set(MOTHERBOARD_INSTALLED, tag.getBoolean("MoboInstalled"));
			if(tag.contains("GPUInstalled"))
				this.getDataTracker().set(GPU_IN_PCI_SLOT, tag.getBoolean("GPUInstalled"));
			if(tag.contains("CPUDividedBy"))
				this.getDataTracker().set(CPU_DIVIDED_BY, tag.getInt("CPUDividedBy"));
			if(tag.contains("RAMSlot0"))
				this.getDataTracker().set(GB_OF_RAM_IN_SLOT_0, tag.getInt("RAMSlot0"));
			if(tag.contains("RAMSlot1"))
				this.getDataTracker().set(GB_OF_RAM_IN_SLOT_1, tag.getInt("RAMSlot1"));
			if(tag.contains("VHDName"))
				this.getDataTracker().set(HARD_DRIVE_FILE_NAME, tag.getString("VHDName"));
			if(tag.contains("ISOName"))
				this.getDataTracker().set(ISO_FILE_NAME, tag.getString("ISOName"));
		}
	}
	
	public EntityPC(World world, double x, double y, double z, Vec3d lookAt, UUID owner, boolean glassSidepanel, CompoundTag tag) {
		this(world, x, y, z, lookAt, owner, tag);
		this.getDataTracker().set(GLASS_SIDEPANEL, glassSidepanel);
	}
	
	public Vec3d getLookAtPos() {
		return new Vec3d(this.getDataTracker().get(LOOK_AT_POS_X), this.getDataTracker().get(LOOK_AT_POS_Y), this.getDataTracker().get(LOOK_AT_POS_Z));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(HARD_DRIVE_FILE_NAME, "");
		this.getDataTracker().startTracking(ISO_FILE_NAME, "");
		this.getDataTracker().startTracking(OWNER_UUID, "");
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
		
		if(tag.contains("Owner")){
			this.getDataTracker().set(OWNER_UUID, tag.getString("Owner"));
		}
		
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
		
		if(tag.contains("GlassSidepanel")) {
			this.getDataTracker().set(GLASS_SIDEPANEL, tag.getBoolean("GlassSidepanel"));
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
		tag.putBoolean("GlassSidepanel", this.getDataTracker().get(GLASS_SIDEPANEL));
		tag.putString("Owner", this.getDataTracker().get(OWNER_UUID));
	}
	
	public String getHardDriveFileName() { return this.getDataTracker().get(HARD_DRIVE_FILE_NAME); }
	public String getIsoFileName() { return this.getDataTracker().get(ISO_FILE_NAME); }
	public String getOwner() { return this.getDataTracker().get(OWNER_UUID); }
	public int getGigsOfRamInSlot0() { return this.getDataTracker().get(GB_OF_RAM_IN_SLOT_0); }
	public int getGigsOfRamInSlot1() { return this.getDataTracker().get(GB_OF_RAM_IN_SLOT_1); }
	public int getCpuDividedBy() { return this.getDataTracker().get(CPU_DIVIDED_BY); }
	public boolean getGpuInstalled() { return this.getDataTracker().get(GPU_IN_PCI_SLOT); }
	public boolean getMotherboardInstalled() { return this.getDataTracker().get(MOTHERBOARD_INSTALLED); }
	public boolean getGlassSidepanel() { return this.getDataTracker().get(GLASS_SIDEPANEL); }
	public boolean get64Bit() { return this.getDataTracker().get(SIXTY_FOUR_BIT); }
	
	public void setOwner(String uid) { this.getDataTracker().set(OWNER_UUID, uid); }
	public void setGigsOfRamInSlot0(int gb) { this.getDataTracker().set(GB_OF_RAM_IN_SLOT_0, gb); }
	public void setGigsOfRamInSlot1(int gb) { this.getDataTracker().set(GB_OF_RAM_IN_SLOT_1, gb); }
	public void setGpuInstalled(boolean installed) { this.getDataTracker().set(GPU_IN_PCI_SLOT, installed); }
	public void setCpuDividedBy(int dividedBy) { this.getDataTracker().set(CPU_DIVIDED_BY, dividedBy); }
	public void setHardDriveFileName(String fileName) { this.getDataTracker().set(HARD_DRIVE_FILE_NAME, fileName); }
	public void setIsoFileName(String fileName) { this.getDataTracker().set(ISO_FILE_NAME, fileName); }
	public void setMotherboardInstalled(boolean installed) { this.getDataTracker().set(MOTHERBOARD_INSTALLED, installed); }
	public void set64Bit(boolean sixtyFourBit) { this.getDataTracker().set(SIXTY_FOUR_BIT, sixtyFourBit); }
	
	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if(!player.world.isClient) {
			if(player.isSneaking() && player.getUuid().toString().equals(this.getOwner())) {
				if(this.getGlassSidepanel()) {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPos().x, this.getPos().y, this.getPos().z,
							ItemPCCaseSidepanel.createPCStackByEntity(this)));
				}else {
					player.world.spawnEntity(new ItemEntity(player.world,
							this.getPos().x, this.getPos().y, this.getPos().z,
							ItemPCCase.createPCStackByEntity(this)));
				}
				this.kill();
			}
		}else {
			if(!player.isSneaking())
				if(this.getOwner().equals(player.getUuid().toString())) {
					ClientMod.currentPC = this;
					MainMod.pcOpenGui.run();
				}else
					player.sendMessage(new TranslatableText("mcvmcomputers.not_your_computer").formatted(Formatting.RED),false);
		}
		return ActionResult.SUCCESS;
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