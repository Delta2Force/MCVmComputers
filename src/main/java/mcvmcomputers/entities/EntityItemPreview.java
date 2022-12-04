package mcvmcomputers.entities;

import mcvmcomputers.client.ClientMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

public class EntityItemPreview extends Entity{
	private static final TrackedData<ItemStack> PREVIEWED_STACK =
				DataTracker.registerData(EntityItemPreview.class, TrackedDataHandlerRegistry.ITEM_STACK);
	
	public EntityItemPreview(EntityType<?> type, World world) {
		super(type, world);
	}
	
	public EntityItemPreview(World world, double x, double y, double z, ItemStack stack) {
		this(EntityList.ITEM_PREVIEW, world);
		this.updatePosition(x, y, z);
		this.getDataTracker().set(PREVIEWED_STACK, stack);
	}
	
	public EntityItemPreview(World world, double x, double y, double z) {
		this(EntityList.ITEM_PREVIEW, world);
		this.updatePosition(x, y, z);
	}
	
	@Override
	public void tick() {
		if(this.world.isClient) {
			if(this != ClientMod.thePreviewEntity) {
				this.kill();
			}
		}else {
			this.kill();
		}
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if(nbt.contains("Item")) {
			this.getDataTracker().set(PREVIEWED_STACK, ItemStack.fromNbt(nbt.getCompound("Item")));
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("Item", this.getDataTracker().get(PREVIEWED_STACK).getOrCreateNbt());
	}

	public void setItem(ItemStack is) {
		this.getDataTracker().set(PREVIEWED_STACK, is);
	}
	
	public ItemStack getPreviewedItemStack() {
		return this.getDataTracker().get(PREVIEWED_STACK);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(PREVIEWED_STACK, new ItemStack(Items.REDSTONE_BLOCK));
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

}
