package mcvmcomputers.item;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.entities.EntityKeyboard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemKeyboard extends OrderableItem{
	public ItemKeyboard(Settings settings) {
		super(settings, 4);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient && hand == Hand.MAIN_HAND) {
			user.getStackInHand(hand).decrement(1);
			EntityKeyboard ek = new EntityKeyboard(world, 
									MCVmComputersMod.thePreviewEntity.getX(),
									MCVmComputersMod.thePreviewEntity.getY(),
									MCVmComputersMod.thePreviewEntity.getZ(),
									new Vec3d(user.getPosVector().x,
												MCVmComputersMod.thePreviewEntity.getY(),
												user.getPosVector().z));
			world.spawnEntity(ek);
		}
		
		if(world.isClient) {
			world.playSound(MCVmComputersMod.thePreviewEntity.getX(),
					MCVmComputersMod.thePreviewEntity.getY(),
					MCVmComputersMod.thePreviewEntity.getZ(),
					SoundEvents.BLOCK_METAL_HIT,
					SoundCategory.BLOCKS, 1, 1, true);
		}
		
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}
}
