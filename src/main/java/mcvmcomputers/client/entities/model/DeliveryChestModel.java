package mcvmcomputers.client.entities.model;

import java.awt.Color;
import java.io.IOException;
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

	public DeliveryChestModel(ModelPart modelPart) throws IOException {
		this.mcc = MinecraftClient.getInstance();
		this.baseTexture = NativeImage.read(mcc.getResourceManager().getResource(new Identifier("mcvmcomputers", "textures/entity/delivery_chest.png")).getInputStream());

		model = modelPart.getChild("model");
		opening = model.getChild("opening");
		upleg0 = model.getChild("upleg0");
		uleg0 = upleg0.getChild("uleg0");
		upleg1 = model.getChild("upleg1");
		uleg1 = upleg1.getChild("uleg1");
		upleg2 = model.getChild("upleg2");
		uleg2 = upleg2.getChild("uleg2");
		upleg3 = model.getChild("upleg3");
		uleg3 = upleg3.getChild("uleg3");
		engine = model.getChild("engine");
		fire = engine.getChild("fire");

		setRotationAngle(opening, -1.1345F, 0.0F, 0.0F);
		setRotationAngle(upleg0, 0.0F, 0.7854F, 0.0F);
		setRotationAngle(upleg1, 0.0F, -0.7854F, 0.0F);
		setRotationAngle(upleg2, 0.0F, -2.3562F, 0.0F);
		setRotationAngle(upleg3, 0.0F, 2.3562F, 0.0F);
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
			//the randomized value is bit shifted because the alpha bits are the last few ones
			//0xFFFFFFFF
			//  AARRGGBB
			int[] sel = new int[] {0x0000FF + (TEX_RANDOM.nextInt(0xFF)<<6),
								   0x0080FF + (TEX_RANDOM.nextInt(0xFF)<<6),
								   0x00FFFF + (TEX_RANDOM.nextInt(0xFF)<<6)};
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

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData m = modelPartData.addChild("model", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -5.0F, -6.0F, 12.0F, 8.0F, 12.0F), ModelTransform.pivot(0.0F, 7.0F, 0.0F));
		m.addChild("opening", ModelPartBuilder.create().uv(0, 20).cuboid(-6.0F, -2.0F, -12.0F, 12.0F, 2.0F, 12.0F), ModelTransform.pivot(0.0F, -5.0F, 6.0F));
		ModelPartData upl0 = m.addChild("upleg0", ModelPartBuilder.create().uv(24, 34).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F), ModelTransform.pivot(-6.0F, 3.0F, 6.0F));
		upl0.addChild("uleg0", ModelPartBuilder.create().uv(0, 46).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F).uv(0, 20).cuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F), ModelTransform.pivot(-0.9828F, 7.0F, -0.0071F));
		ModelPartData upl1 = m.addChild("upleg1", ModelPartBuilder.create().uv(0, 34).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F), ModelTransform.pivot(-6.0F, 3.0F, -6.0F));
		upl1.addChild("uleg1", ModelPartBuilder.create().uv(44, 44).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F).uv(0, 8).cuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F), ModelTransform.pivot(-0.9828F, 7.0F, -0.0071F));
		ModelPartData upl2 = m.addChild("upleg2", ModelPartBuilder.create().uv(6, 24).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F), ModelTransform.pivot(6.0F, 3.0F, -6.0F));
		upl2.addChild("uleg2", ModelPartBuilder.create().uv(38, 43).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F).uv(0, 4).cuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F), ModelTransform.pivot(-0.9828F, 7.0F, -0.0071F));
		ModelPartData upl3 = m.addChild("upleg3", ModelPartBuilder.create().uv(0, 24).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 7.0F, 1.0F), ModelTransform.pivot(6.0F, 3.0F, 6.0F));
		upl3.addChild("uleg3", ModelPartBuilder.create().uv(32, 43).cuboid(-1.0F, 0.0F, -0.5F, 2.0F, 6.0F, 1.0F).uv(0, 0).cuboid(-1.4868F, 6.0F, -1.5232F, 3.0F, 1.0F, 3.0F), ModelTransform.pivot(-0.9828F, 7.0F, -0.0071F));
		ModelPartData ngn = m.addChild("engine", ModelPartBuilder.create().uv(0, 34).cuboid(-4.0F, 8.0F, -4.0F, 8.0F, 4.0F, 8.0F).uv(36, 0).cuboid(-3.0F, 5.0F, -3.0F, 6.0F, 3.0F, 6.0F).uv(36, 20).cuboid(-2.0F, 3.0F, -2.0F, 4.0F, 2.0F, 4.0F).uv(36, 26).cuboid(-4.0F, 3.0F, 3.0F, 1.0F, 5.0F, 1.0F).uv(30, 34).cuboid(3.0F, 3.0F, 3.0F, 1.0F, 5.0F, 1.0F).uv(36, 0).cuboid(-4.0F, 3.0F, -4.0F, 1.0F, 5.0F, 1.0F).uv(34, 34).cuboid(3.0F, 3.0F, -4.0F, 1.0F, 5.0F, 1.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		ngn.addChild("fire", ModelPartBuilder.create().uv(32, 34).cuboid(-3.0F, -1.0F, -3.0F, 6.0F, 3.0F, 6.0F), ModelTransform.pivot(0.0F, 13.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}
}