package mcvmcomputers.entities;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.item.ItemPackage;
import mcvmcomputers.tablet.TabletOrder.OrderStatus;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EntityDeliveryChest extends Entity{
	private static final TrackedData<Float> TARGET_X =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> TARGET_Y =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Float> TARGET_Z =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<String> DELIVERY_UUID =
			DataTracker.registerData(EntityFlatScreen.class, TrackedDataHandlerRegistry.STRING);
	
	public float renderRot = 90f;
	public float upLeg01Rot = 3f;
	public float uLeg01Rot = -2.7f;
	public float upLeg23Rot = 3.3f;
	public float uLeg23Rot = -2.7f;
	public float openingRot = 0f;
	public float takeOffSpeed = 0f;
	public boolean fire = false;
	public SoundInstance rocketSound;
	
	private boolean trackedOnce = false;
	
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
		
		if(MCVmComputersMod.currentOrder == null) {
			this.kill();
		}else {
			this.getDataTracker().set(DELIVERY_UUID, MCVmComputersMod.currentOrder.orderUUID);
		}
	}
	
	public EntityDeliveryChest(World world, double x, double y, double z) {
		this(world, new Vec3d(x, y-80, z+80));
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(TARGET_X, 0f);
		this.getDataTracker().startTracking(TARGET_Y, 0f);
		this.getDataTracker().startTracking(TARGET_Z, 0f);
		this.getDataTracker().startTracking(DELIVERY_UUID, "Hello there!");
	}
	
	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.getDataTracker().set(TARGET_X, tag.getFloat("TargetX"));
		this.getDataTracker().set(TARGET_Y, tag.getFloat("TargetY"));
		this.getDataTracker().set(TARGET_Z, tag.getFloat("TargetZ"));
		this.getDataTracker().set(DELIVERY_UUID, tag.getString("DeliveryUUID"));
	}
	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putFloat("TargetX", this.getDataTracker().get(TARGET_X));
		tag.putFloat("TargetY", this.getDataTracker().get(TARGET_Y));
		tag.putFloat("TargetZ", this.getDataTracker().get(TARGET_Z));
		tag.putString("DeliveryUUID", this.getDataTracker().get(DELIVERY_UUID));
	}
	
	@Override
	public void tick() {
		super.tick();
		if(MCVmComputersMod.currentOrder == null || !MCVmComputersMod.currentOrder.orderUUID.equals(getDeliveryUUID())) {
			this.kill();
		}
	}
	
	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if(player.world.isClient) {
			return false;
		}
		if(hand == Hand.OFF_HAND) {
			return false;
		}
		if(MCVmComputersMod.currentOrder != null) {
			if(MCVmComputersMod.currentOrder.currentStatus == OrderStatus.PAYMENT_CHEST_ARRIVED) {
				ItemStack is = player.getMainHandStack();
				
				boolean flag = false;
				
				if(is != null) {
					if(is.getItem().equals(Items.IRON_INGOT)) {
						MCVmComputersMod.currentOrder.price -= is.getCount();
						is.decrement(is.getCount());
						flag = true;
					}
				}
				
				if(!flag) {
					player.sendMessage(new LiteralText("You need to click the chest with ingots in your hand!").formatted(Formatting.RED));
				}else {
					if(MCVmComputersMod.currentOrder.price < 0) {
						is.increment(MCVmComputersMod.currentOrder.price * -1);
						MCVmComputersMod.currentOrder.currentStatus = OrderStatus.PAYMENT_CHEST_RECEIVING;
					}
				}
				
				return flag;
			}else if(MCVmComputersMod.currentOrder.currentStatus == OrderStatus.ORDER_CHEST_ARRIVED) {
				player.world.spawnEntity(new ItemEntity(player.world, this.getX(), this.getY()+1.5, this.getZ(), ItemPackage.createPackage(Registry.ITEM.getId(MCVmComputersMod.currentOrder.items.get(0)))));
				MCVmComputersMod.currentOrder.items.remove(0);
				if(MCVmComputersMod.currentOrder.items.size() == 0) {
					MCVmComputersMod.currentOrder.currentStatus = OrderStatus.ORDER_CHEST_RECEIVED;
				}
			}
		}
		
		return super.interact(player, hand);
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
	public String getDeliveryUUID() {
		return this.getDataTracker().get(DELIVERY_UUID);
	}
	

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
	
	@Override
	public boolean collides() {
		return true;
	}

}
