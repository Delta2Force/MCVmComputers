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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityKeyboard extends Entity{
	private static final TrackedData<Float> ORIENTATION_X =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ORIENTATION_Y =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ORIENTATION_Z =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> ORIENTATION_W =
			DataTracker.registerData(EntityKeyboard.class, TrackedDataHandlerRegistry.FLOAT);
	
	public EntityKeyboard(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public EntityKeyboard(World world, double x, double y, double z) {
		this(EntityList.KEYBOARD, world);
		this.updatePosition(x, y, z);
	}
	
	public EntityKeyboard(World world, Double x, Double y, Double z, Quaternion quaternion, String uuid) {
		this(EntityList.KEYBOARD, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(ORIENTATION_X, quaternion.getX());
		this.getDataTracker().set(ORIENTATION_Y, quaternion.getY());
		this.getDataTracker().set(ORIENTATION_Z, quaternion.getZ());
		this.getDataTracker().set(ORIENTATION_W, quaternion.getW());
	}

	public Quaternion getOrientation() {
		return new Quaternion(this.getDataTracker().get(ORIENTATION_X),
				this.getDataTracker().get(ORIENTATION_Y),
				this.getDataTracker().get(ORIENTATION_Z),
				this.getDataTracker().get(ORIENTATION_W));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ORIENTATION_X, 0f);
		this.getDataTracker().startTracking(ORIENTATION_Y, 0f);
		this.getDataTracker().startTracking(ORIENTATION_Z, 0f);
		this.getDataTracker().startTracking(ORIENTATION_W, 0f);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.getDataTracker().set(ORIENTATION_X, nbt.getFloat("OrientationX"));
		this.getDataTracker().set(ORIENTATION_Y, nbt.getFloat("OrientationY"));
		this.getDataTracker().set(ORIENTATION_Z, nbt.getFloat("OrientationZ"));
		this.getDataTracker().set(ORIENTATION_W, nbt.getFloat("OrientationW"));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putFloat("OrientationX", this.getDataTracker().get(ORIENTATION_X));
		nbt.putFloat("OrientationY", this.getDataTracker().get(ORIENTATION_Y));
		nbt.putFloat("OrientationZ", this.getDataTracker().get(ORIENTATION_Z));
		nbt.putFloat("OrientationW", this.getDataTracker().get(ORIENTATION_W));
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
	public boolean collidesWith(Entity other) {
		return true;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}
