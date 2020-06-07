package mcvmcomputers.item;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.entities.EntityPC;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPCCase extends OrderableItem{
	public ItemPCCase(Settings settings) {
		super(settings, 2);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient && hand == Hand.MAIN_HAND) {
			user.getStackInHand(hand).decrement(1);
			EntityPC ek = new EntityPC(world, 
								MCVmComputersMod.thePreviewEntity.getX(),
								MCVmComputersMod.thePreviewEntity.getY(),
								MCVmComputersMod.thePreviewEntity.getZ(),
								new Vec3d(user.getPosVector().x,
											MCVmComputersMod.thePreviewEntity.getY(),
											user.getPosVector().z));
			world.spawnEntity(ek);
		}
		
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}
}
