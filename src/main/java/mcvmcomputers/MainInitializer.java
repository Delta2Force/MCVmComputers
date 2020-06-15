package mcvmcomputers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.virtualbox_6_1.ISession;
import org.virtualbox_6_1.IVirtualBox;
import org.virtualbox_6_1.VirtualBoxManager;

import mcvmcomputers.client.entities.render.CRTScreenRender;
import mcvmcomputers.client.entities.render.DeliveryChestRender;
import mcvmcomputers.client.entities.render.FlatScreenRender;
import mcvmcomputers.client.entities.render.ItemPreviewRender;
import mcvmcomputers.client.entities.render.KeyboardRender;
import mcvmcomputers.client.entities.render.MouseRender;
import mcvmcomputers.client.entities.render.PCRender;
import mcvmcomputers.client.tablet.TabletOS;
import mcvmcomputers.client.tablet.TabletOrder;
import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.sound.SoundList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class MainInitializer implements ModInitializer{
	public static final OutputStream discardAllBytes = new OutputStream() { @Override public void write(int b) throws IOException {} };
	public static Identifier vmTextureIdentifier;
	public static NativeImage vmTextureNativeImage;
	public static NativeImageBackedTexture vmTextureNIBT;
	public static EntityItemPreview thePreviewEntity;
	public static boolean vmTurnedOn;
	public static boolean vmTurningOff;
	public static boolean vmTurningOn;
	public static ISession vmSession;
	public static int vmEntityID = -1;
	
	public static int maxRam;
	public static int videoMem;
	
	public static VirtualBoxManager vbManager;
	public static IVirtualBox vb;
	
	public static Process vboxWebSrv;
	public static Thread vmUpdateThread;
	public static ByteArrayInputStream vmTextureBytes;
	
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
	public static TabletOrder currentOrder;
	public static Thread tabletThread;
	
	public static float deltaTime;
	public static long lastDeltaTimeTime;
	
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
		if(MainInitializer.vmTextureBytes != null) {
			if(vmTextureIdentifier != null) {
				MinecraftClient.getInstance().getTextureManager().destroyTexture(vmTextureIdentifier);
			}
			
			NativeImage ni = null;
			try {
				ni = NativeImage.read(MainInitializer.vmTextureBytes);
			} catch (IOException e) {
			}
			if(ni != null) {
				MainInitializer.vmTextureNativeImage.close();
				MainInitializer.vmTextureNIBT.close();
				MainInitializer.vmTextureNativeImage = ni;
				MainInitializer.vmTextureNIBT = new NativeImageBackedTexture(MainInitializer.vmTextureNativeImage);
				MainInitializer.vmTextureIdentifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("vm_texture", MainInitializer.vmTextureNIBT);
			}
			MainInitializer.vmTextureBytes = null;
		}
	}
	
	public static void renderDisplay(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if(MainInitializer.tabletOS.textureIdentifier != null) {
			matrices.push();
			matrices.scale(0.19f, 0.19f, 0.19f);
			matrices.multiply(new Quaternion(-90, 0, 0, true));
			matrices.translate(-1.65, -1.65, 7.57);
			Matrix4f matrix4f = matrices.peek().getModel();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(MainInitializer.tabletOS.textureIdentifier));
			vertexConsumer.vertex(matrix4f, 0.0F, 3.3F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(15728640).next();
	        vertexConsumer.vertex(matrix4f, 3.3F, 3.3F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(15728640).next();
	        vertexConsumer.vertex(matrix4f, 3.3F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(15728640).next();
	        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(15728640).next();
	        matrices.pop();
		}
	}
	
	public void onInitialize() {
		ItemList.init();
		EntityList.init();
		SoundList.init();
	}

}
