package mcvmcomputers.item;

import mcvmcomputers.ClientMod;
import mcvmcomputers.entities.EntityPC;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPCCaseSidepanel extends OrderableItem{
	public ItemPCCaseSidepanel(Settings settings) {
		super(settings, 6);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient && hand == Hand.MAIN_HAND) {
			user.getStackInHand(hand).decrement(1);
			HitResult hr = user.rayTrace(10, 0f, false);
			EntityPC ek = new EntityPC(world, 
									hr.getPos().getX() + 1,
									hr.getPos().getY(),
									hr.getPos().getZ(),
									new Vec3d(user.getPosVector().x,
												hr.getPos().getY(),
												user.getPosVector().z), user.getUuid(), true, user.getStackInHand(hand).getTag());
			world.spawnEntity(ek);
		}
		
		if(world.isClient) {
			world.playSound(ClientMod.thePreviewEntity.getX(),
							ClientMod.thePreviewEntity.getY(),
							ClientMod.thePreviewEntity.getZ(),
							SoundEvents.BLOCK_METAL_PLACE,
							SoundCategory.BLOCKS, 1, 1, true);
		}
		
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, user.getStackInHand(hand));
	}
	
	public static ItemStack createPCStackByEntity(EntityPC pc) {
		ItemStack is = new ItemStack(ItemList.PC_CASE_SIDEPANEL);
		CompoundTag ct = is.getOrCreateTag();
		ct.putBoolean("x64", pc.get64Bit());
		ct.putBoolean("MoboInstalled", pc.getMotherboardInstalled());
		ct.putBoolean("GPUInstalled", pc.getGpuInstalled());
		ct.putInt("CPUDividedBy", pc.getCpuDividedBy());
		ct.putInt("RAMSlot0", pc.getGigsOfRamInSlot0());
		ct.putInt("RAMSlot1", pc.getGigsOfRamInSlot1());
		ct.putString("VHDName", pc.getHardDriveFileName());
		ct.putString("ISOName", pc.getIsoFileName());
		return is;
	}
}
