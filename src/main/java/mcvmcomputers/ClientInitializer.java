package mcvmcomputers;

import org.lwjgl.glfw.GLFW;

import mcvmcomputers.entities.EntityList;
import mcvmcomputers.entities.render.CRTScreenRender;
import mcvmcomputers.entities.render.DeliveryChestRender;
import mcvmcomputers.entities.render.FlatScreenRender;
import mcvmcomputers.entities.render.ItemPreviewRender;
import mcvmcomputers.entities.render.KeyboardRender;
import mcvmcomputers.entities.render.MouseRender;
import mcvmcomputers.entities.render.PCRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;

public class ClientInitializer implements ClientModInitializer{
	public static FabricKeyBinding unfocusKey1 = FabricKeyBinding.Builder.create(new Identifier("mcvmcomputers", "unfocuskey1"), Type.KEYSYM, GLFW.GLFW_KEY_LEFT_CONTROL, "VM Computers").build();
	public static FabricKeyBinding unfocusKey2 = FabricKeyBinding.Builder.create(new Identifier("mcvmcomputers", "unfocuskey2"), Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_CONTROL, "VM Computers").build();
	public static FabricKeyBinding unfocusKey3 = FabricKeyBinding.Builder.create(new Identifier("mcvmcomputers", "unfocuskey3"), Type.KEYSYM, GLFW.GLFW_KEY_BACKSPACE, "VM Computers").build();
	public static FabricKeyBinding unfocusKey4 = FabricKeyBinding.Builder.create(new Identifier("mcvmcomputers", "unfocuskey4"), Type.KEYSYM, -1, "VM Computers").build();
	
	@Override
	public void onInitializeClient() {
		KeyBindingRegistry.INSTANCE.addCategory("VM Computers");
		KeyBindingRegistry.INSTANCE.register(unfocusKey1);
		KeyBindingRegistry.INSTANCE.register(unfocusKey2);
		KeyBindingRegistry.INSTANCE.register(unfocusKey3);
		KeyBindingRegistry.INSTANCE.register(unfocusKey4);
		
		EntityRendererRegistry.INSTANCE.register(EntityList.ITEM_PREVIEW,
				(entityRenderDispatcher, context) -> new ItemPreviewRender(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(EntityList.KEYBOARD,
				(entityRenderDispatcher, context) -> new KeyboardRender(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(EntityList.MOUSE,
				(entityRenderDispatcher, context) -> new MouseRender(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(EntityList.CRT_SCREEN,
				(entityRenderDispatcher, context) -> new CRTScreenRender(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(EntityList.FLATSCREEN,
				(entityRenderDispatcher, context) -> new FlatScreenRender(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(EntityList.PC,
				(entityRenderDispatcher, context) -> new PCRender(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(EntityList.DELIVERY_CHEST,
				(entityRenderDispatcher, context) -> new DeliveryChestRender(entityRenderDispatcher));
	}

}
