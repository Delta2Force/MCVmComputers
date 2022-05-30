package mcvmcomputers.client.entities.model;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class DeliveryChestModel extends EntityModel<Entity> {
	public final ModelPart model;
	public final ModelPart opening;
	public final ModelPart upleg0;
	public final ModelPart uleg0;
	public final ModelPart upleg1;
	public final ModelPart uleg1;
	public final ModelPart upleg2;
	public final ModelPart uleg2;
	public final ModelPart upleg3;
	public final ModelPart uleg3;
	public final ModelPart engine;
	public final ModelPart fire;
	
	private final NativeImage baseTexture;
	private final MinecraftClient mcc;
	
	public static final Random TEX_RANDOM = new Random();
	
	private NativeImage ni;
	private NativeImageBackedTexture nibt;
	private Identifier texId;
	
	public boolean fireYes = true;

	public DeliveryChestModel() throws IOException {
		this.mcc = MinecraftClient.getInstance();
		this.baseTexture = NativeImage.read(mcc.getResourceManager().getResource(new Identifier("mcvmcomputers", "textures/entity/delivery_chest.png")).getInputStream());
		
		textureWidth = 64;
		textureHeight = 64;

		model = new ModelPart(this);
		model.setPivot(0.0F, 7.0F, 0.0F);
		model.setTextureOffset(0, 0).addCuboid(-6.0F, -5.0F, -6.0F, 12.0F, 8.0F, 12.0F, 0.0F, false);

		opening = new ModelPart(this);
		opening.setPivot(0.0F, -5.0F, 6.0F);
		model.addChild(opening);
		setRotationAngle(opening, -1.1345F, 0.0F, 0.0F);
		opening.setTextureOffset(0, 20).addCuboid(-6.0F, -2.0F, -12.0F, 12.0F, 2.0F, 12.0F, 0.0F, false);

		upleg0 = new ModelPart(this);
		upleg0.setPivot(-6.0F, 3.0F, 6.0F);
		model.addChild(upleg0);
		setRotationAngle(upleg0, 0.0F, 0.7854F, 0.0F);
		upleg0.setTextureOffset(24, 34).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F, 0.0F, false);

		uleg0 = new ModelPart(this);
		uleg0.setPivot(-0.9828F, 7.0F, -0.0071F);
		upleg0.addChild(uleg0);
		uleg0.setTextureOffset(0, 46).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F, 0.0F, false);
		uleg0.setTextureOffset(0, 20).addCuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		upleg1 = new ModelPart(this);
		upleg1.setPivot(-6.0F, 3.0F, -6.0F);
		model.addChild(upleg1);
		setRotationAngle(upleg1, 0.0F, -0.7854F, 0.0F);
		upleg1.setTextureOffset(0, 34).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F, 0.0F, false);

		uleg1 = new ModelPart(this);
		uleg1.setPivot(-0.9828F, 7.0F, -0.0071F);
		upleg1.addChild(uleg1);
		uleg1.setTextureOffset(44, 44).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F, 0.0F, false);
		uleg1.setTextureOffset(0, 8).addCuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		upleg2 = new ModelPart(this);
		upleg2.setPivot(6.0F, 3.0F, -6.0F);
		model.addChild(upleg2);
		setRotationAngle(upleg2, 0.0F, -2.3562F, 0.0F);
		upleg2.setTextureOffset(6, 24).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F, 0.0F, false);

		uleg2 = new ModelPart(this);
		uleg2.setPivot(-0.9828F, 7.0F, -0.0071F);
		upleg2.addChild(uleg2);
		uleg2.setTextureOffset(38, 43).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F, 0.0F, false);
		uleg2.setTextureOffset(0, 4).addCuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		upleg3 = new ModelPart(this);
		upleg3.setPivot(6.0F, 3.0F, 6.0F);
		model.addChild(upleg3);
		setRotationAngle(upleg3, 0.0F, 2.3562F, 0.0F);
		upleg3.setTextureOffset(0, 24).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F, 0.0F, false);

		uleg3 = new ModelPart(this);
		uleg3.setPivot(-0.9828F, 7.0F, -0.0071F);
		upleg3.addChild(uleg3);
		uleg3.setTextureOffset(32, 43).addCuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F, 0.0F, false);
		uleg3.setTextureOffset(0, 0).addCuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F, 0.0F, false);

		engine = new ModelPart(this);
		engine.setPivot(0.0F, 0.0F, 0.0F);
		model.addChild(engine);
		engine.setTextureOffset(0, 34).addCuboid(-4.0F, 8.0F, -4.0F, 8.0F, 4.0F, 8.0F, 0.0F, false);
		engine.setTextureOffset(36, 0).addCuboid(-3.0F, 5.0F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
		engine.setTextureOffset(36, 20).addCuboid(-2.0F, 3.0F, -2.0F, 4.0F, 2.0F, 4.0F, 0.0F, false);
		engine.setTextureOffset(36, 26).addCuboid(-4.0F, 3.0F, 3.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
		engine.setTextureOffset(30, 34).addCuboid(3.0F, 3.0F, 3.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
		engine.setTextureOffset(36, 0).addCuboid(-4.0F, 3.0F, -4.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);
		engine.setTextureOffset(34, 34).addCuboid(3.0F, 3.0F, -4.0F, 1.0F, 5.0F, 1.0F, 0.0F, false);

		fire = new ModelPart(this);
		fire.setPivot(0.0F, 13.0F, 0.0F);
		engine.addChild(fire);
		fire.setTextureOffset(32, 34).addCuboid(-3.0F, -1.0F, -3.0F, 6.0F, 3.0F, 6.0F, 0.0F, false);
	}
	
	private void generateTexture() {
		if(ni != null) {ni.close(); ni = null;}
		if(nibt != null) {nibt.close(); nibt = null;}
		if(texId != null) {mcc.getTextureManager().destroyTexture(texId); texId = null;};
		
		ni = new NativeImage(64, 64, true);
		ni.copyFrom(baseTexture);
		for(int x = 38;x<50;x++) {
			for(int y = 34;y<40;y++) {
				ni.setPixelColor(x, y, randomColor());
			}
		}
		for(int x = 32;x<56;x++) {
			for(int y = 40;y<43;y++) {
				ni.setPixelColor(x, y, randomColor());
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
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		model.render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
	public void render(MatrixStack matrixStack, VertexConsumerProvider provider, int packedLight, int packedOverlay){
		this.generateTexture();
		model.render(matrixStack, provider.getBuffer(RenderLayer.getText(texId)), packedLight, packedOverlay);
	}
	
	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.pitch = x;
		modelRenderer.yaw = y;
		modelRenderer.roll = z;
	}
}