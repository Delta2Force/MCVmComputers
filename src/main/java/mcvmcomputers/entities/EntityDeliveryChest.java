package mcvmcomputers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDeliveryChest extends Entity{
	private static final TrackedData<Float> TARGET_X =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> TARGET_Y =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> TARGET_Z =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.FLOAT);
	
	public float renderRot = 90f; //target: 180
	public float upLeg01Rot = 0f; //target: 3
	public float uLeg01Rot = 0f; //target: -2.4
	public float upLeg23Rot = 0f; //target: 3.3
	public float uLeg23Rot = 0f; //target: 2.7
	public float openingRot = 0f; //target; -2
	
	//z -80 y 80 starting position from target
	
	public EntityDeliveryChest(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public EntityDeliveryChest(World world, Vec3d target) {
		super(EntityList.DELIVERY_CHEST, world);
		this.getDataTracker().set(TARGET_X, (float)target.x);
		this.getDataTracker().set(TARGET_Y, (float)target.y);
		this.getDataTracker().set(TARGET_Z, (float)target.z);
		this.updatePosition(target.x, target.y + 80, target.z - 80);
	}
	
	public EntityDeliveryChest(World world, double x, double y, double z) {
		super(EntityList.DELIVERY_CHEST, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(TARGET_X, (float)x);
		this.getDataTracker().set(TARGET_Y, (float)(y-80));
		this.getDataTracker().set(TARGET_Z, (float)(z+80));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(TARGET_X, 0f);
		this.getDataTracker().startTracking(TARGET_Y, 0f);
		this.getDataTracker().startTracking(TARGET_Z, 0f);
	}
	
	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.getDataTracker().set(TARGET_X, tag.getFloat("TargetX"));
		this.getDataTracker().set(TARGET_Y, tag.getFloat("TargetY"));
		this.getDataTracker().set(TARGET_Z, tag.getFloat("TargetZ"));
	}
	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putFloat("TargetX", this.getDataTracker().get(TARGET_X));
		tag.putFloat("TargetY", this.getDataTracker().get(TARGET_Y));
		tag.putFloat("TargetZ", this.getDataTracker().get(TARGET_Z));
	}
	
	public float getTargetX() {
		return this.getDataTracker().get(TARGET_X);
	}
	public float getTargetY() {
		return this.getDataTracker().get(TARGET_Y);
	}
	public float getTargetZ() {
		return this.getDataTracker().get(TARGET_Z);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}
