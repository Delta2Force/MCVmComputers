package mcvmcomputers.item;

import java.lang.reflect.Constructor;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PlacableOrderableItem extends OrderableItem{
	private Constructor<? extends Entity> constructor;
	private SoundEvent placeSound;
	public final boolean wallTV;
	
	public PlacableOrderableItem(Settings settings, Class<? extends Entity> entityPlaced, SoundEvent placeSound, int price, boolean wallTV) {
		super(settings, price);
		this.wallTV = wallTV;
		this.placeSound = placeSound;
		try {
			constructor = entityPlaced.getConstructor(World.class, Double.class, Double.class, Double.class, Quaternion.class, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PlacableOrderableItem(Settings settings, Class<? extends Entity> entityPlaced, SoundEvent placeSound, int price) {
		this(settings, entityPlaced, placeSound, price, false);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient && hand == Hand.MAIN_HAND) {
			user.getStackInHand(hand).decrement(1);
			HitResult hr = user.raycast(5, 0f, false);
			Entity ek;
			try {
				ek = constructor.newInstance(world, 
											hr.getPos().getX(),
											hr.getPos().getY(),
											hr.getPos().getZ(),
											MVCUtils.lookAt(hr.getPos(), new Vec3d(user.getPos().x, hr.getPos().y, user.getPos().z)),
											user.getUuid().toString());
				world.spawnEntity(ek);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(world.isClient) {
			world.playSound(ClientMod.thePreviewEntity.getX(),
							ClientMod.thePreviewEntity.getY(),
							ClientMod.thePreviewEntity.getZ(),
							placeSound,
							SoundCategory.BLOCKS, 1, 1, true);
		}
		
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}

	@Override
	public boolean shouldSyncTagToClient() {
		return false;
	}
}