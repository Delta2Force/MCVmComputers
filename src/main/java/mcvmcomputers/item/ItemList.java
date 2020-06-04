package mcvmcomputers.item;

import java.util.Arrays;
import java.util.List;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ItemList {
	public static final ItemGroup MOD_ITEM_GROUP_PARTS = FabricItemGroupBuilder.build(new Identifier("mcvmcomputers", "parts"), () -> new ItemStack(Blocks.WHITE_STAINED_GLASS));
	public static final ItemGroup MOD_ITEM_GROUP_SCREENS = FabricItemGroupBuilder.build(new Identifier("mcvmcomputers", "screens"), () -> new ItemStack(Blocks.WHITE_STAINED_GLASS));
	public static final ItemGroup MOD_ITEM_GROUP_PERIPHERALS = FabricItemGroupBuilder.build(new Identifier("mcvmcomputers", "peripherals"), () -> new ItemStack(Blocks.WHITE_STAINED_GLASS));
	public static final ItemGroup MOD_ITEM_GROUP_OTHERS = FabricItemGroupBuilder.build(new Identifier("mcvmcomputers", "others"), () -> new ItemStack(Blocks.WHITE_STAINED_GLASS));
	public static final Item PC_CASE_SIDEPANEL = new ItemPCCaseSidepanel(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_MOTHERBOARD = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_MOTHERBOARD64 = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_FLATSCREEN = new ItemFlatScreen(new Settings().group(MOD_ITEM_GROUP_SCREENS));
	public static final Item ITEM_CRTSCREEN = new ItemCRTScreen(new Settings().group(MOD_ITEM_GROUP_SCREENS));
	public static final Item ITEM_NEW_HARDDRIVE = new ItemNewHarddrive(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_HARDDRIVE = new ItemHarddrive(new Settings().rarity(Rarity.EPIC));
	public static final Item ITEM_KEYBOARD = new ItemKeyboard(new Settings().group(MOD_ITEM_GROUP_PERIPHERALS));
	public static final Item ITEM_MOUSE = new ItemMouse(new Settings().group(MOD_ITEM_GROUP_PERIPHERALS));
	public static final Item ITEM_RAM1G = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_RAM2G = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_RAM4G = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_CPU2 = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_CPU4 = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_CPU6 = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_GPU = new Item(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item ITEM_TABLET = new ItemOrderingTablet(new Settings().group(MOD_ITEM_GROUP_OTHERS));
	public static final Item ITEM_PACKAGE = new ItemPackage(new Settings().rarity(Rarity.EPIC));
	public static final Item PC_CASE = new ItemPCCase(new Settings().group(MOD_ITEM_GROUP_PARTS));
	public static final Item PC_CASE_NO_PANEL = new Item(new Settings().rarity(Rarity.EPIC));
	public static final Item PC_CASE_ONLY_PANEL = new Item(new Settings().rarity(Rarity.EPIC));
	public static final Item PC_CASE_GLASS_PANEL = new Item(new Settings().rarity(Rarity.EPIC));
	
	public static final List<Item> placableItems = Arrays.asList(PC_CASE, PC_CASE_SIDEPANEL, ITEM_KEYBOARD, ITEM_MOUSE, ITEM_CRTSCREEN, ITEM_FLATSCREEN);
	
	public static void init() {
		registerItem("pc_case_sidepanel", PC_CASE_SIDEPANEL);
		registerItem("pc_case", PC_CASE);
		registerItem("motherboard", ITEM_MOTHERBOARD);
		registerItem("motherboard64", ITEM_MOTHERBOARD64);
		registerItem("flatscreen", ITEM_FLATSCREEN);
		registerItem("crtscreen", ITEM_CRTSCREEN);
		registerItem("new_harddrive", ITEM_NEW_HARDDRIVE);
		registerItem("harddrive", ITEM_HARDDRIVE);
		registerItem("keyboard", ITEM_KEYBOARD);
		registerItem("mouse", ITEM_MOUSE);
		registerItem("ram1g", ITEM_RAM1G);
		registerItem("ram2g", ITEM_RAM2G);
		registerItem("ram4g", ITEM_RAM4G);
		registerItem("cpu_divided_by_2", ITEM_CPU2);
		registerItem("cpu_divided_by_4", ITEM_CPU4);
		registerItem("cpu_divided_by_6", ITEM_CPU6);
		registerItem("gpu", ITEM_GPU);
		registerItem("ordering_tablet", ITEM_TABLET);
		registerItem("package", ITEM_PACKAGE);
		
		//Visual items - used for rendering only!
		registerItem("pc_case_no_panel", PC_CASE_NO_PANEL);
		registerItem("pc_case_only_panel", PC_CASE_ONLY_PANEL);
		registerItem("pc_case_only_glass_sidepanel", PC_CASE_GLASS_PANEL);
	}
	
	private static Item registerItem(String id, Item it) {
		Registry.register(Registry.ITEM, new Identifier("mcvmcomputers", id), it);
		return it;
	}
}
