package mcvmcomputers.entities;

import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityList {
	public static EntityType<Entity> ITEM_PREVIEW;
	public static EntityType<Entity> KEYBOARD;
	public static EntityType<Entity> MOUSE;
	public static EntityType<Entity> CRT_SCREEN;
	public static EntityType<Entity> FLATSCREEN;
	public static EntityType<Entity> WALLTV;
	public static EntityType<Entity> PC;
	public static EntityType<Entity> DELIVERY_CHEST;

	public static void init() {
		ITEM_PREVIEW = Registry.register(Registry.ENTITY_TYPE,
						new Identifier("mcvmcomputers", "item_preview"),
						FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityItemPreview::new)
						.size(new EntityDimensions(1,1, true)).trackable(60, 2,true).build());
		KEYBOARD = Registry.register(Registry.ENTITY_TYPE, 
					new Identifier("mcvmcomputers", "keyboard"),
					FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityKeyboard::new)
					.size(new EntityDimensions(0.5f, 0.0625f, true)).trackable(60, 2,true).build());
		MOUSE = Registry.register(Registry.ENTITY_TYPE, 
				new Identifier("mcvmcomputers", "mouse"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityMouse::new)
				.size(new EntityDimensions(0.25f, 0.0625f, true)).trackable(60, 2,true).build());
		CRT_SCREEN = Registry.register(Registry.ENTITY_TYPE, 
						new Identifier("mcvmcomputers", "crt_screen"),
						FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityCRTScreen::new)
						.size(new EntityDimensions(0.8f, 0.8f, true)).trackable(60, 2,true).build());
		FLATSCREEN = Registry.register(Registry.ENTITY_TYPE, 
						new Identifier("mcvmcomputers", "flat_screen"),
						FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityFlatScreen::new)
						.size(new EntityDimensions(0.8f, 0.8f, true)).trackable(60, 2,true).build());
		WALLTV = Registry.register(Registry.ENTITY_TYPE, 
								new Identifier("mcvmcomputers", "walltv"),
								FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityWallTV::new)
								.size(new EntityDimensions(1f, 1.2f, true)).trackable(60, 2,true).build());
		PC = Registry.register(Registry.ENTITY_TYPE, 
				new Identifier("mcvmcomputers", "pc"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityPC::new)
				.size(new EntityDimensions(0.375f, 0.6875f, true)).trackable(60, 2,true).build());
		DELIVERY_CHEST = Registry.register(Registry.ENTITY_TYPE, 
							new Identifier("mcvmcomputers", "delivery_chest"),
							FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityDeliveryChest::new)
							.size(new EntityDimensions(1f, 2f, true)).trackable(600, 40,true).build());
	}
}
