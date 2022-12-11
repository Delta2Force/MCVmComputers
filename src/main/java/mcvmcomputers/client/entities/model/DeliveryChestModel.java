package mcvmcomputers.client.entities.model;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import static mcvmcomputers.client.utils.ModelPartBuilderEx.createMPB;
import mcvmcomputers.client.utils.ModelPartBuilderEx.*;

public class DeliveryChestModel extends EntityModel<Entity> {
	private final ModelPart modelRender;

	private final static int textureWidth = 64;
	private final static int textureHeight = 64;

	private final NativeImage baseTexture;
	private final MinecraftClient mcc;

	private static final Random TEX_RANDOM = new Random();

	private NativeImage ni;
	private NativeImageBackedTexture nibt;
	private Identifier texId;

	public boolean fireYes = true;

	public DeliveryChestModel() throws IOException {
		mcc = MinecraftClient.getInstance();

		baseTexture = NativeImage.read((InputStream) mcc.getResourceManager().getResource(new Identifier("mcvmcomputers", "textures/entity/delivery_chest.png")).stream());
		ModelPartData model = new ModelData().getRoot();

		ModelPartBuilder base = createMPB(-6f, -5f, -6f, 12f, 8f, 12f, false);
		model.addChild("model", base, ModelTransform.pivot(0f, 7f, 0f));

		ModelPartBuilder opening = createMPB(-6f, -2f, -12f, 12f, 2f, 12f, false, 0, 20);
		model.getChild("model").addChild("opening", opening, ModelTransform.of(0f, -5f, 6f, -1.1345f, 0f, 0f));

		ModelPartBuilder upleg0 = createMPB(-6f, 3f, 6f, 2f, 7f, 1f, false, 24, 34);
		model.addChild("upleg0", upleg0, ModelTransform.rotation(0f, 0.7854f, 0f));

		ModelPartBuilder uleg0 = createMPB(new MPB[] {
				new MPB(-1.9828f, 7f, -0.0571f, 2f, 6f, 1f, false, 0, 46),
				new MPB(-2.4696f, 13f, -1.5303f, 3f, 1f, 3f, false, 0, 20)
		});
		model.getChild("upleg0").addChild("uleg0", uleg0, ModelTransform.pivot(-0.9828f, 7f, -0.0071f));

		ModelPartBuilder upleg1 = createMPB(-1f, 0f, -0.5f, 2f, 7f, 1f, false, 0, 34);
		model.addChild("upleg1", upleg1, ModelTransform.of(-6f, 3f, -6f, 0f, -0.7854f, 0f));

		ModelPartBuilder uleg1 = createMPB(new MPB[] {
				new MPB(-1f, 0f, -0.5f, 2f, 6f, 1f, false, 44, 44),
				new MPB(-1.4868f, 6f, -1.5232f, 3f, 1f, 3f, false, 0, 8)
		});
		model.getChild("upleg1").addChild("uleg1", uleg1, ModelTransform.pivot(-0.9828f, 7f, -0.0071f));

		ModelPartBuilder upleg2 = createMPB(-1f, 0f, -0.5f, 2f, 7f, 1f, false, 6, 24);
		model.addChild("upleg2", upleg2, ModelTransform.of(6f, 3f, -6f, 0f, -2.3562f, 0f));

		ModelPartBuilder uleg2 = createMPB(new MPB[] {
				new MPB(-1f, 0f, -0.5f, 2f, 6f, 1f, false, 38, 43),
				new MPB(-1.4868f, 6f, -1.5232f, 3f, 1f, 3f, false, 0, 4)
		});
		model.getChild("upleg2").addChild("uleg2", uleg2, ModelTransform.pivot(-0.9828f, 7f, -0.0071f));

		ModelPartBuilder upleg3 = createMPB(-1f, 0f, -0.5f, 2f, 7f, 1f, false, 0, 24);
		model.addChild("upleg3", upleg3, ModelTransform.of(6f, 3f, 6f, 0f, 2.3562f, 0f));

		ModelPartBuilder uleg3 = createMPB(new MPB[] {
				new MPB(-1f, 0f, -0.5f, 2f, 6f, 1f, false, 32, 43),
				new MPB(-1.4868f, 6f, -1.5232f, 3f, 1f, 3f, false, 0, 0)
		});
		model.getChild("upleg3").addChild("uleg3", uleg3, ModelTransform.pivot(-0.9828f, 7f, -0.0071f));

		ModelPartBuilder engine = createMPB(new MPB[] {
				new MPB(-4f, 8f, -4f, 8f, 4f, 8f, false, 0, 34),
				new MPB(-3f, 5f, -3f, 6f, 3f, 6f, false, 36, 0),
				new MPB(-2f, 3f, -2f, 4f, 2f, 4f, false, 36, 20),
				new MPB(-4f, 3f, 3f, 1f, 5f, 1f, false, 36, 26),
				new MPB(3f, 3f, 3f, 1f, 5f, 1f, false, 30, 34),
				new MPB(-4f, 3f, -4f, 1f, 5f, 1f, false, 36, 0),
				new MPB(3f, 3f, -4f, 1f, 5f, 1f, false, 34, 34)
		});
		model.addChild("engine", engine, ModelTransform.NONE);

		ModelPartBuilder fire = createMPB(-3f, -1f, -3f, 6f, 3f, 6f, false, 32, 32);
		model.getChild("engine").addChild("fire", fire, ModelTransform.pivot(0f, 13f, 0f));

		modelRender = model.createPart(textureWidth, textureHeight);
	}

	private void generateTexture() {
		if (ni != null) { ni.close(); ni = null; }
		if(nibt != null) { nibt.close(); nibt = null; }
		if(texId != null) { mcc.getTextureManager().destroyTexture(texId); texId = null; }

		ni = new NativeImage(64, 64, true);
		ni.copyFrom(baseTexture);
		for(int x = 38; x < 50; x++) {
			for(int y = 34; y < 40; y++) {
				ni.setColor(x, y, randomColor());
			}
		}
		for(int x = 32; x < 56; x++) {
			for(int y = 40; y < 43; y++) {
				ni.setColor(x, y, randomColor());
			}
		}
		nibt = new NativeImageBackedTexture(ni);
		texId = mcc.getTextureManager().registerDynamicTexture("delivery_chest_fire", nibt);
	}

	private int randomColor() {
		if(fireYes) {
			Color r = new Color(0,0,255);
			Color y = new Color(0,128,255);
			Color o = new Color(0, 255, 255);
			r = new Color(r.getRed(), r.getGreen(), r.getBlue(), TEX_RANDOM.nextInt(256));
			y = new Color(y.getRed(), y.getGreen(), y.getBlue(), TEX_RANDOM.nextInt(256));
			o = new Color(o.getRed(), o.getGreen(), o.getBlue(), TEX_RANDOM.nextInt(256));
			int[] sel = new int[] {r.getRGB(), y.getRGB(), o.getRGB()};
			return sel[TEX_RANDOM.nextInt(sel.length)];
		}else {
			return new Color(0f,0f,0f,0f).getRGB();
		}
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		modelRender.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider provider, int packedLight, int packedOverlay){
		this.generateTexture();
		modelRender.render(matrixStack, provider.getBuffer(RenderLayer.getText(texId)), packedLight, packedOverlay);
	}

	public void setRotationAngle(String identifier, float pitch, float yaw, float roll) {
		ModelPart part = modelRender;

		if(!identifier.isEmpty()) {
			String[] pathElements = identifier.split("\\.");
			for (String element : pathElements) {
				part = part.getChild(element);
			}
		}

		part.pitch = pitch;
		part.yaw = yaw;
		part.roll = roll;
	}
}