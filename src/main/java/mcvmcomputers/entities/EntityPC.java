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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPC extends Entity{
	private static final TrackedData<String> ISO_FILE_NAME =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> HARD_DRIVE_FILE_NAME =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> OWNER_UUID =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.STRING);

	private static final TrackedData<Float> ORIENTATION_X =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ORIENTATION_Y =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ORIENTATION_Z =
			DataTracker.registerData(EntityPC.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ORIENTATION_W =
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
	
	public EntityPC(World world, Double x, Double y, Double z, Quaternion quaternion, UUID owner, NbtCompound nbt) {
		this(EntityList.PC, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(ORIENTATION_X, quaternion.getX());
		this.getDataTracker().set(ORIENTATION_Y, quaternion.getY());
		this.getDataTracker().set(ORIENTATION_Z, quaternion.getZ());
		this.getDataTracker().set(ORIENTATION_W, quaternion.getW());
		this.getDataTracker().set(OWNER_UUID, owner.toString());
		
		if(nbt != null) {
			if(nbt.contains("x64"))
				this.getDataTracker().set(SIXTY_FOUR_BIT, nbt.getBoolean("x64"));
			if(nbt.contains("MoboInstalled"))
				this.getDataTracker().set(MOTHERBOARD_INSTALLED, nbt.getBoolean("MoboInstalled"));
			if(nbt.contains("GPUInstalled"))
				this.getDataTracker().set(GPU_IN_PCI_SLOT, nbt.getBoolean("GPUInstalled"));
			if(nbt.contains("CPUDividedBy"))
				this.getDataTracker().set(CPU_DIVIDED_BY, nbt.getInt("CPUDividedBy"));
			if(nbt.contains("RAMSlot0"))
				this.getDataTracker().set(GB_OF_RAM_IN_SLOT_0, nbt.getInt("RAMSlot0"));
			if(nbt.contains("RAMSlot1"))
				this.getDataTracker().set(GB_OF_RAM_IN_SLOT_1, nbt.getInt("RAMSlot1"));
			if(nbt.contains("VHDName"))
				this.getDataTracker().set(HARD_DRIVE_FILE_NAME, nbt.getString("VHDName"));
			if(nbt.contains("ISOName"))
				this.getDataTracker().set(ISO_FILE_NAME, nbt.getString("ISOName"));
		}
	}
	
	public EntityPC(World world, double x, double y, double z, Quaternion quaternion, UUID owner, boolean glassSidepanel, NbtCompound nbt) {
		this(world, x, y, z, quaternion, owner, nbt);
		this.getDataTracker().set(GLASS_SIDEPANEL, glassSidepanel);
	}

	public Quaternion getOrientation() {
		return new Quaternion(this.getDataTracker().get(ORIENTATION_X),
				this.getDataTracker().get(ORIENTATION_Y),
				this.getDataTracker().get(ORIENTATION_Z),
				this.getDataTracker().get(ORIENTATION_W));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(HARD_DRIVE_FILE_NAME, "");
		this.getDataTracker().startTracking(ISO_FILE_NAME, "");
		this.getDataTracker().startTracking(OWNER_UUID, "");
		this.getDataTracker().startTracking(ORIENTATION_X, 0f);
		this.getDataTracker().startTracking(ORIENTATION_Y, 0f);
		this.getDataTracker().startTracking(ORIENTATION_Z, 0f);
		this.getDataTracker().startTracking(ORIENTATION_W, 0f);
		this.getDataTracker().startTracking(GB_OF_RAM_IN_SLOT_0, 0);
		this.getDataTracker().startTracking(GB_OF_RAM_IN_SLOT_1, 0);
		this.getDataTracker().startTracking(CPU_DIVIDED_BY, 0);
		this.getDataTracker().startTracking(GPU_IN_PCI_SLOT, false);
		this.getDataTracker().startTracking(MOTHERBOARD_INSTALLED, false);
		this.getDataTracker().startTracking(GLASS_SIDEPANEL, false);
		this.getDataTracker().startTracking(SIXTY_FOUR_BIT, false);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.getDataTracker().set(ORIENTATION_X, nbt.getFloat("OrientationX"));
		this.getDataTracker().set(ORIENTATION_Y, nbt.getFloat("OrientationY"));
		this.getDataTracker().set(ORIENTATION_Z, nbt.getFloat("OrientationZ"));
		this.getDataTracker().set(ORIENTATION_W, nbt.getFloat("OrientationW"));

		if(nbt.contains("Owner")){
			this.getDataTracker().set(OWNER_UUID, nbt.getString("Owner"));
		}

		if(nbt.contains("X64")) {
			this.getDataTracker().set(SIXTY_FOUR_BIT, nbt.getBoolean("X64"));
		}

		if(nbt.contains("CpuDividedBy")) {
			this.getDataTracker().set(CPU_DIVIDED_BY, nbt.getInt("CpuDividedBy"));
		}

		if(nbt.contains("IsoFileName")) {
			this.getDataTracker().set(ISO_FILE_NAME, nbt.getString("IsoFileName"));
		}

		if(nbt.contains("GbRamSlot0")) {
			this.getDataTracker().set(GB_OF_RAM_IN_SLOT_0, nbt.getInt("GbRamSlot0"));
		}

		if(nbt.contains("GbRamSlot1")) {
			this.getDataTracker().set(GB_OF_RAM_IN_SLOT_1, nbt.getInt("GbRamSlot1"));
		}

		if(nbt.contains("GpuInstalled")) {
			this.getDataTracker().set(GPU_IN_PCI_SLOT, nbt.getBoolean("GpuInstalled"));
		}

		if(nbt.contains("HardDriveFileName")) {
			this.getDataTracker().set(HARD_DRIVE_FILE_NAME, nbt.getString("HardDriveFileName"));
		}

		if(nbt.contains("MotherboardInstalled")) {
			this.getDataTracker().set(MOTHERBOARD_INSTALLED, nbt.getBoolean("MotherboardInstalled"));
		}

		if(nbt.contains("GlassSidepanel")) {
			this.getDataTracker().set(GLASS_SIDEPANEL, nbt.getBoolean("GlassSidepanel"));
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putBoolean("X64", this.getDataTracker().get(SIXTY_FOUR_BIT));
		nbt.putFloat("OrientationX", this.getDataTracker().get(ORIENTATION_X));
		nbt.putFloat("OrientationY", this.getDataTracker().get(ORIENTATION_Y));
		nbt.putFloat("OrientationZ", this.getDataTracker().get(ORIENTATION_Z));
		nbt.putFloat("OrientationW", this.getDataTracker().get(ORIENTATION_W));
		nbt.putInt("CpuDividedBy", this.getDataTracker().get(CPU_DIVIDED_BY));
		nbt.putString("IsoFileName", this.getDataTracker().get(ISO_FILE_NAME));
		nbt.putInt("GbRamSlot0", this.getDataTracker().get(GB_OF_RAM_IN_SLOT_0));
		nbt.putInt("GbRamSlot1", this.getDataTracker().get(GB_OF_RAM_IN_SLOT_1));
		nbt.putBoolean("GpuInstalled", this.getDataTracker().get(GPU_IN_PCI_SLOT));
		nbt.putString("HardDriveFileName", this.getDataTracker().get(HARD_DRIVE_FILE_NAME));
		nbt.putBoolean("MotherboardInstalled", this.getDataTracker().get(MOTHERBOARD_INSTALLED));
		nbt.putBoolean("GlassSidepanel", this.getDataTracker().get(GLASS_SIDEPANEL));
		nbt.putString("Owner", this.getDataTracker().get(OWNER_UUID));
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
					player.sendMessage(Text.translatable("mcvmcomputers.not_your_computer").formatted(Formatting.RED),false);
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public boolean collidesWith(Entity other) {
		return true;
	}

	@Override
	public boolean isCollidable() {
		return true;
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}