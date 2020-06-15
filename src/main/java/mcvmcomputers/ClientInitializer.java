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
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class ClientInitializer implements ClientModInitializer{
	public static int glfwUnfocusKey1 = GLFW.GLFW_KEY_LEFT_CONTROL;
	public static int glfwUnfocusKey2 = GLFW.GLFW_KEY_RIGHT_CONTROL;
	public static int glfwUnfocusKey3 = GLFW.GLFW_KEY_BACKSPACE;
	public static int glfwUnfocusKey4 = -1;
	
	@Override
	public void onInitializeClient() {
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
