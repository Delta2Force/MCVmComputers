package mcvmcomputers.entities;


import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityList {
	public static final EntityType<EntityItemPreview> ITEM_PREVIEW = Registry.register(Registry.ENTITY_TYPE,
													new Identifier("mcvmcomputers", "item_preview"),
													FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityItemPreview>)EntityItemPreview::new)
													.dimensions(EntityDimensions.fixed(1,1)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());;
	public static final EntityType<EntityKeyboard> KEYBOARD = Registry.register(Registry.ENTITY_TYPE,
												new Identifier("mcvmcomputers", "keyboard"),
												FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityKeyboard>)EntityKeyboard::new)
												.dimensions(EntityDimensions.fixed(0.5f, 0.0625f)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());
	public static final EntityType<EntityMouse> MOUSE = Registry.register(Registry.ENTITY_TYPE,
											 new Identifier("mcvmcomputers", "mouse"),
											 FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityMouse>)EntityMouse::new)
											 .dimensions(EntityDimensions.fixed(0.25f, 0.0625f)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());
	public static final EntityType<EntityCRTScreen> CRT_SCREEN = Registry.register(Registry.ENTITY_TYPE,
												  new Identifier("mcvmcomputers", "crt_screen"),
												  FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityCRTScreen>)EntityCRTScreen::new)
												  .dimensions(EntityDimensions.fixed(0.8f, 0.8f)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());
	public static final EntityType<EntityFlatScreen> FLATSCREEN = Registry.register(Registry.ENTITY_TYPE,
												  new Identifier("mcvmcomputers", "flat_screen"),
												  FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityFlatScreen>)EntityFlatScreen::new)
												  .dimensions(EntityDimensions.fixed(0.8f, 0.8f)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());
	public static final EntityType<EntityWallTV> WALLTV = Registry.register(Registry.ENTITY_TYPE,
											  new Identifier("mcvmcomputers", "walltv"),
											  FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityWallTV>)EntityWallTV::new)
											  .dimensions(EntityDimensions.fixed(1f, 1.2f)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());
	public static final EntityType<EntityPC> PC = Registry.register(Registry.ENTITY_TYPE,
										  new Identifier("mcvmcomputers", "pc"),
										  FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityPC>)EntityPC::new)
										  .dimensions(EntityDimensions.fixed(0.375f, 0.6875f)).trackRangeBlocks(60).trackedUpdateRate(2).forceTrackedVelocityUpdates(true).build());
	public static final EntityType<EntityDeliveryChest> DELIVERY_CHEST = Registry.register(Registry.ENTITY_TYPE,
													  new Identifier("mcvmcomputers", "delivery_chest"),
													  FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<EntityDeliveryChest>)EntityDeliveryChest::new)
													  .dimensions(EntityDimensions.fixed(1f, 2f)).trackRangeBlocks(600).trackedUpdateRate(40).forceTrackedVelocityUpdates(true).build());
	
}
