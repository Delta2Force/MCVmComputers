package mcvmcomputers.item;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ItemPackage extends Item{

	public ItemPackage(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(!world.isClient){
			ItemStack is = user.getStackInHand(hand);
			if(is.getTag() != null) {
				if(is.getTag().contains("packaged_item")) {
					is.decrement(1);
					user.giveItemStack(new ItemStack(Registry.ITEM.get(new Identifier(is.getTag().getString("packaged_item")))));
				}
			}
		}
		return super.use(world, user, hand);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		if(stack.getTag() != null) {
			if(stack.getTag().contains("packaged_item")) {
				return new TranslatableText("mcvmcomputers.packaged").formatted(Formatting.GRAY).append(new TranslatableText(Registry.ITEM.get(new Identifier(stack.getTag().getString("packaged_item"))).getTranslationKey()).formatted(Formatting.GREEN));
			}
		}
		return new TranslatableText("mcvmcomputers.invalid_package").formatted(Formatting.RED);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(new TranslatableText("mcvmcomputers.open_with_right_click").formatted(Formatting.GRAY));
	}
	
	public static ItemStack createPackage(Identifier id) {
		ItemStack is = new ItemStack(ItemList.ITEM_PACKAGE);
		CompoundTag ct = is.getOrCreateTag();
		ct.putString("packaged_item", id.toString());
		is.setTag(ct);
		return is;
	}

}
