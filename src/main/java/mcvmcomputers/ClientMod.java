package mcvmcomputers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Deflater;

import org.lwjgl.glfw.GLFW;
import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.IVirtualBox;
import org.virtualbox_6_1.VirtualBoxManager;

import io.netty.buffer.Unpooled;
import mcvmcomputers.client.entities.render.CRTScreenRender;
import mcvmcomputers.client.entities.render.DeliveryChestRender;
import mcvmcomputers.client.entities.render.FlatScreenRender;
import mcvmcomputers.client.entities.render.ItemPreviewRender;
import mcvmcomputers.client.entities.render.KeyboardRender;
import mcvmcomputers.client.entities.render.MouseRender;
import mcvmcomputers.client.entities.render.PCRender;
import mcvmcomputers.client.tablet.TabletOS;
import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.utils.TabletOrder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class ClientMod implements ClientModInitializer{
	public static final OutputStream discardAllBytes = new OutputStream() { @Override public void write(int b) throws IOException {} };
	public static Map<UUID, Identifier> vmScreenTextures;
	public static Map<UUID, NativeImage> vmScreenTextureNI;
	public static Map<UUID, NativeImageBackedTexture> vmScreenTextureNIBT;
	public static EntityItemPreview thePreviewEntity;
	public static boolean vmTurnedOn;
	public static boolean vmTurningOff;
	public static boolean vmTurningOn;
	public static ISession vmSession;
	
	public static int maxRam;
	public static int videoMem;
	
	public static VirtualBoxManager vbManager;
	public static IVirtualBox vb;
	
	public static Process vboxWebSrv;
	public static Thread vmUpdateThread;
	public static byte[] vmTextureBytes;
	public static int vmTextureBytesSize;
	
	public static double mouseLastX = 0;
	public static double mouseLastY = 0;
	public static double mouseCurX = 0;
	public static double mouseCurY = 0;
	public static int mouseDeltaScroll;
	public static boolean leftMouseButton;
	public static boolean middleMouseButton;
	public static boolean rightMouseButton;
	public static List<Integer> vmKeyboardScancodes = new ArrayList<>();
	public static boolean releaseKeys = false;
	public static File vhdDirectory;
	public static File isoDirectory;
	public static int latestVHDNum = 0;
	public static TabletOS tabletOS;
	public static TabletOrder myOrder;
	public static int vmEntityID = -1;
	
	public static Thread tabletThread;
	
	public static float deltaTime;
	public static long lastDeltaTimeTime;
	
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
	
	public static void getVHDNum() throws NumberFormatException, IOException {
		File f = new File(vhdDirectory.getParentFile(), "vhdnum");
		if(f.exists()) {
			latestVHDNum = Integer.parseInt(Files.readAllLines(f.toPath()).get(0));
		}
	}
	
	public static void increaseVHDNum() throws IOException {
		latestVHDNum++;
		File f = new File(vhdDirectory.getParentFile(), "vhdnum");
		if(f.exists()) {
			f.delete();
		}
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.append(""+latestVHDNum);
		fw.flush();
		fw.close();
	}
	
	public static void generatePCScreen() {
		MinecraftClient mcc = MinecraftClient.getInstance();
		if(mcc.player == null) {
			return;
		}
		if(vmTextureBytes != null) {
			if(vmScreenTextures.containsKey(mcc.player.getUuid())) {
				MinecraftClient.getInstance().getTextureManager().destroyTexture(vmScreenTextures.get(mcc.player.getUuid()));
			}
			
			Deflater def = new Deflater();
			def.setInput(vmTextureBytes);
			def.finish();
			byte[] deflated = new byte[vmTextureBytesSize];
			int sz = def.deflate(deflated);
			def.end();
			
			PacketByteBuf p = new PacketByteBuf(Unpooled.buffer());
			p.writeByteArray(deflated);
			p.writeInt(sz);
			p.writeInt(vmTextureBytesSize);
			ClientSidePacketRegistry.INSTANCE.sendToServer(PacketList.C2S_SCREEN, p);
			
			NativeImage ni = null;
			try {
				ni = NativeImage.read(new ByteArrayInputStream(vmTextureBytes));
			} catch (IOException e) {
			}
			if(ni != null) {
				if(vmScreenTextureNI.containsKey(mcc.player.getUuid())) {
					vmScreenTextureNI.get(mcc.player.getUuid()).close();
				}
				if(vmScreenTextureNIBT.containsKey(mcc.player.getUuid())) {
					vmScreenTextureNIBT.get(mcc.player.getUuid()).close();
				}
				vmScreenTextureNI.put(mcc.player.getUuid(), ni);
				NativeImageBackedTexture nibt = new NativeImageBackedTexture(ni);
				vmScreenTextureNIBT.put(mcc.player.getUuid(), nibt);
				vmScreenTextures.put(mcc.player.getUuid(), MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("vm_texture", nibt));
			}
			vmTextureBytes = null;
		}
	}
	
	@Override
	public void onInitializeClient() {
		PacketList.registerClientPackets();
		
		vmScreenTextures = new HashMap<UUID, Identifier>();
		vmScreenTextureNI = new HashMap<UUID, NativeImage>();
		vmScreenTextureNIBT = new HashMap<UUID, NativeImageBackedTexture>();
		
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
