package mcvmcomputers.item;

import mcvmcomputers.gui.GuiCreateHarddrive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemNewHarddrive extends Item{

	public ItemNewHarddrive(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(world.isClient) {
			MinecraftClient.getInstance().openScreen(new GuiCreateHarddrive());
		}
		return super.use(world, user, hand);
	}
}
