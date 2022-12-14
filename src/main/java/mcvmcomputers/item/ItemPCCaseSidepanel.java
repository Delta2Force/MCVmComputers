package mcvmcomputers.item;

import java.util.List;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
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
			HitResult hr = user.raycast(10, 0f, false);
			EntityPC ek = new EntityPC(world, 
									hr.getPos().getX(),
									hr.getPos().getY(),
									hr.getPos().getZ(),
									MVCUtils.lookAt(hr.getPos(), new Vec3d(user.getPos().x, hr.getPos().y, user.getPos().z)),
									user.getUuid(), true, user.getStackInHand(hand).getNbt());
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
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if(stack.getNbt() != null) {
			if (stack.getNbt().contains("MoboInstalled")) {
				if(stack.getNbt().getBoolean("MoboInstalled")) {
					tooltip.add(Text.translatable(stack.getNbt().getBoolean("x64") ? "item.mcvmcomputers.motherboard64" : "item.mcvmcomputers.motherboard").formatted(Formatting.GRAY));
					if(stack.getNbt().getBoolean("GPUInstalled"))
						tooltip.add(Text.translatable("mcvmcomputers.pc_item_gpu").formatted(Formatting.GRAY));
					if(stack.getNbt().getInt("CPUDividedBy") > 0)
						tooltip.add(Text.translatable("mcvmcomputers.pc_item_cpu", stack.getNbt().getInt("CPUDividedBy")).formatted(Formatting.GRAY));
					if(stack.getNbt().getInt("RAMSlot0") > 0) {
						if((stack.getNbt().getInt("RAMSlot0") / 1024) < 1) {
							tooltip.add(Text.translatable("mcvmcomputers.pc_item_ramSlot0Mb", stack.getNbt().getInt("RAMSlot0")).formatted(Formatting.GRAY));
						} else if((stack.getNbt().getInt("RAMSlot0") / 1024) >= 1) {
							tooltip.add(Text.translatable("mcvmcomputers.pc_item_ramSlot0", (stack.getNbt().getInt("RAMSlot0") / 1024)).formatted(Formatting.GRAY));
					}}
					if(stack.getNbt().getInt("RAMSlot1") > 0) {
						if((stack.getNbt().getInt("RAMSlot1") / 1024) < 1) {
							tooltip.add(Text.translatable("mcvmcomputers.pc_item_ramSlot1Mb", stack.getNbt().getInt("RAMSlot1")).formatted(Formatting.GRAY));
						} else if((stack.getNbt().getInt("RAMSlot1") / 1024) >= 1) {
							tooltip.add(Text.translatable("mcvmcomputers.pc_item_ramSlot1", (stack.getNbt().getInt("RAMSlot1") / 1024)).formatted(Formatting.GRAY));
					}}
					if(!stack.getNbt().getString("VHDName").isEmpty())
						tooltip.add(Text.translatable("mcvmcomputers.pc_item_hdd", stack.getNbt().getString("VHDName")).formatted(Formatting.GRAY));
					if(!stack.getNbt().getString("ISOName").isEmpty())
						tooltip.add(Text.translatable("mcvmcomputers.pc_item_iso", stack.getNbt().getString("ISOName")).formatted(Formatting.GRAY));
				}
			}
		}
	}
	
	@Override
	public Text getName(ItemStack stack) {
		if(stack.getNbt() != null) {
			if (stack.getNbt().contains("MoboInstalled")) {
				if(stack.getNbt().getBoolean("MoboInstalled")) {
					return Text.translatable("mcvmcomputers.pc_item_built");
				}
			}
		}
		return Text.translatable("item.mcvmcomputers.pc_case_sidepanel");
	}
	
	public static ItemStack createPCStackByEntity(EntityPC pc) {
		ItemStack is = new ItemStack(ItemList.PC_CASE_SIDEPANEL);
		if(pc.getMotherboardInstalled()) {
			NbtCompound ct = is.getOrCreateNbt();
			ct.putBoolean("x64", pc.get64Bit());
			ct.putBoolean("MoboInstalled", pc.getMotherboardInstalled());
			ct.putBoolean("GPUInstalled", pc.getGpuInstalled());
			ct.putInt("CPUDividedBy", pc.getCpuDividedBy());
			ct.putInt("RAMSlot0", pc.getGigsOfRamInSlot0());
			ct.putInt("RAMSlot1", pc.getGigsOfRamInSlot1());
			ct.putString("VHDName", pc.getHardDriveFileName());
			ct.putString("ISOName", pc.getIsoFileName());
		}
		return is;
	}
}
