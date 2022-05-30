package mcvmcomputers.entities;

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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityKeyboard extends Entity{
	private static final TrackedData<Float> LOOK_AT_POS_X =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> LOOK_AT_POS_Y =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> LOOK_AT_POS_Z =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	
	public EntityKeyboard(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public EntityKeyboard(World world, double x, double y, double z) {
		this(EntityList.KEYBOARD, world);
		this.updatePosition(x, y, z);
	}
	
	public EntityKeyboard(World world, Double x, Double y, Double z, Vec3d lookAt, String uuid) {
		this(EntityList.KEYBOARD, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(LOOK_AT_POS_X, (float)lookAt.x);
		this.getDataTracker().set(LOOK_AT_POS_Y, (float)lookAt.y);
		this.getDataTracker().set(LOOK_AT_POS_Z, (float)lookAt.z);
	}
	
	public Vec3d getLookAtPos() {
		return new Vec3d(this.getDataTracker().get(LOOK_AT_POS_X), this.getDataTracker().get(LOOK_AT_POS_Y), this.getDataTracker().get(LOOK_AT_POS_Z));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(LOOK_AT_POS_X, 0f);
		this.getDataTracker().startTracking(LOOK_AT_POS_Y, 0f);
		this.getDataTracker().startTracking(LOOK_AT_POS_Z, 0f);
	}
	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.getDataTracker().set(LOOK_AT_POS_X, tag.getFloat("LookAtX"));
		this.getDataTracker().set(LOOK_AT_POS_Y, tag.getFloat("LookAtY"));
		this.getDataTracker().set(LOOK_AT_POS_Z, tag.getFloat("LookAtZ"));
	}
	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putFloat("LookAtX", this.getDataTracker().get(LOOK_AT_POS_X));
		tag.putFloat("LookAtY", this.getDataTracker().get(LOOK_AT_POS_Y));
		tag.putFloat("LookAtZ", this.getDataTracker().get(LOOK_AT_POS_Z));
	}
	
	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if(!player.world.isClient) {
			if(player.isSneaking()) {
				this.kill();
				player.world.spawnEntity(new ItemEntity(player.world,
						this.getPos().x, this.getPos().y, this.getPos().z,
						new ItemStack(ItemList.ITEM_KEYBOARD)));
			}
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
