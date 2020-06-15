package mcvmcomputers;

import org.lwjgl.glfw.GLFW;

import mcvmcomputers.client.entities.render.CRTScreenRender;
import mcvmcomputers.client.entities.render.DeliveryChestRender;
import mcvmcomputers.client.entities.render.FlatScreenRender;
import mcvmcomputers.client.entities.render.ItemPreviewRender;
import mcvmcomputers.client.entities.render.KeyboardRender;
import mcvmcomputers.client.entities.render.MouseRender;
import mcvmcomputers.client.entities.render.PCRender;
import mcvmcomputers.entities.EntityList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

public class ClientInitializer implements ClientModInitializer{
	public static int glfwUnfocusKey1 = GLFW.GLFW_KEY_LEFT_CONTROL;
	public static int glfwUnfocusKey2 = GLFW.GLFW_KEY_RIGHT_CONTROL;
	public static int glfwUnfocusKey3 = GLFW.GLFW_KEY_BACKSPACE;
	public static int glfwUnfocusKey4 = -1;
	
	public static String getKeyName(int key) {
		if (key < 0) {
			return "None";
		}else {
			return glfwKey(key);
		}
	}
	
	private static String glfwKey(int key) {
		switch(key) {
		case GLFW.GLFW_KEY_LEFT_CONTROL:
			return "L Control";
		case GLFW.GLFW_KEY_RIGHT_CONTROL:
			return "R Control";
		case GLFW.GLFW_KEY_RIGHT_ALT:
			return "R Alt";
		case GLFW.GLFW_KEY_LEFT_ALT:
			return "L Alt";
		case GLFW.GLFW_KEY_LEFT_SHIFT:
			return "L Shift";
		case GLFW.GLFW_KEY_RIGHT_SHIFT:
			return "R Shift";
		case GLFW.GLFW_KEY_ENTER:
			return "Enter";
		case GLFW.GLFW_KEY_BACKSPACE:
			return "Backspace";
		case GLFW.GLFW_KEY_CAPS_LOCK:
			return "Caps Lock";
		case GLFW.GLFW_KEY_TAB:
			return "Tab";
		default:
			return GLFW.glfwGetKeyName(key, 0);
		}
	}
	
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
