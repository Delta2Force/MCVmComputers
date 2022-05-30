package mcvmcomputers.item;

import mcvmcomputers.MainMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemHarddrive extends OrderableItem{
	public ItemHarddrive(Settings settings) {
		super(settings, 6);
	}
	
	@Override
	public boolean shouldSyncTagToClient() {
		return true;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(world.isClient) {
			MainMod.hardDriveClick.run();
		}
		return super.use(world, user, hand);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		if(stack.getTag() != null) {
			if(stack.getTag().contains("vhdfile")) {
				return new TranslatableText("mcvmcomputers.hdd_item_name", stack.getTag().getString("vhdfile")).formatted(Formatting.WHITE);
			}
		}
		return new TranslatableText("mcvmcomputers.hdd_item_name", new TranslatableText("mcvmcomputers.hdd_right_click").asString()).formatted(Formatting.WHITE);
	}
	
	public static ItemStack createHardDrive(String fileName) {
		ItemStack is = new ItemStack(ItemList.ITEM_HARDDRIVE);
		CompoundTag ct = is.getOrCreateTag();
		ct.putString("vhdfile", fileName);
		is.setTag(ct);
		return is;
	}

}
