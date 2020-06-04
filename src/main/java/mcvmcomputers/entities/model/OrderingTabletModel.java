package mcvmcomputers.entities.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class OrderingTabletModel extends EntityModel<Entity> {
	private final ModelPart tablet;

	public OrderingTabletModel() {
		textureWidth = 64;
		textureHeight = 64;

		tablet = new ModelPart(this);
		tablet.setPivot(0.0F, 24.0F, 0.0F);
		tablet.setTextureOffset(24, 15).addCuboid(-6.0F, -2.0F, -6.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		tablet.setTextureOffset(24, 13).addCuboid(-6.0F, -2.0F, 5.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		tablet.setTextureOffset(12, 14).addCuboid(5.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F, 0.0F, false);
		tablet.setTextureOffset(0, 13).addCuboid(-6.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F, 0.0F, false);
		tablet.setTextureOffset(0, 0).addCuboid(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, false);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		tablet.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.pitch = x;
		modelRenderer.yaw = y;
		modelRenderer.roll = z;
	}
}