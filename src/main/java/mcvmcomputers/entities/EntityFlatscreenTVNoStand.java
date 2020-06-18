package mcvmcomputers.entities;

import mcvmcomputers.MainMod;
import mcvmcomputers.item.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFlatscreenTVNoStand extends Entity{
	private static final TrackedData<Float> LOOK_AT_POS_X =
			DataTracker.registerData(EntityFlatscreenTVNoStand.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> LOOK_AT_POS_Y =
			DataTracker.registerData(EntityFlatscreenTVNoStand.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> LOOK_AT_POS_Z =
			DataTracker.registerData(EntityFlatscreenTVNoStand.class, TrackedDataHandlerRegistry.FLOAT);
	
	private static final TrackedData<String> OWNER_UUID =
			DataTracker.registerData(EntityFlatscreenTVNoStand.class, TrackedDataHandlerRegistry.STRING);
	
	public EntityFlatscreenTVNoStand(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public EntityFlatscreenTVNoStand(World world, double x, double y, double z) {
		this(EntityList.FLATSCREENTV_NOSTAND, world);
		this.updatePosition(x, y, z);
	}
	
	public EntityFlatscreenTVNoStand(World world, double x, double y, double z, Vec3d lookAt, String uuid) {
		this(EntityList.FLATSCREENTV_NOSTAND, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(LOOK_AT_POS_X, (float)lookAt.x);
		this.getDataTracker().set(LOOK_AT_POS_Y, (float)lookAt.y);
		this.getDataTracker().set(LOOK_AT_POS_Z, (float)lookAt.z);
		this.getDataTracker().set(OWNER_UUID, uuid);
	}
	
	public Vec3d getLookAtPos() {
		return new Vec3d(this.getDataTracker().get(LOOK_AT_POS_X),
						 this.getDataTracker().get(LOOK_AT_POS_Y),
						 this.getDataTracker().get(LOOK_AT_POS_Z));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(LOOK_AT_POS_X, 0f);
		this.getDataTracker().startTracking(LOOK_AT_POS_Y, 0f);
		this.getDataTracker().startTracking(LOOK_AT_POS_Z, 0f);
		this.getDataTracker().startTracking(OWNER_UUID, "");
	}
	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.getDataTracker().set(LOOK_AT_POS_X, tag.getFloat("LookAtX"));
		this.getDataTracker().set(LOOK_AT_POS_Y, tag.getFloat("LookAtY"));
		this.getDataTracker().set(LOOK_AT_POS_Z, tag.getFloat("LookAtZ"));
		this.getDataTracker().set(OWNER_UUID, tag.getString("Owner"));
	}
	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putFloat("LookAtX", this.getDataTracker().get(LOOK_AT_POS_X));
		tag.putFloat("LookAtY", this.getDataTracker().get(LOOK_AT_POS_Y));
		tag.putFloat("LookAtZ", this.getDataTracker().get(LOOK_AT_POS_Z));
		tag.putString("Owner", this.getDataTracker().get(OWNER_UUID));
	}
	
	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if(!player.world.isClient) {
			if(player.isSneaking()) {
				this.kill();
				player.world.spawnEntity(new ItemEntity(player.world,
						this.getPosVector().x, this.getPosVector().y, this.getPosVector().z,
						new ItemStack(ItemList.ITEM_FLATSCREENTV_NOSTAND)));
			}
		}else {
			if(!player.isSneaking()) {
				if(this.getOwnerUUID().equals(player.getUuid().toString())) {
					MainMod.focus.run();
				}
			}
		}
		return true;
	}
	
	@Override
	public void tick() {
		if(getOwnerUUID().isEmpty()) {
			this.kill();
		}
	}
	
	@Override
	public boolean collides() {
		return true;
	}
	
	public String getOwnerUUID() {
		return this.getDataTracker().get(OWNER_UUID);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}