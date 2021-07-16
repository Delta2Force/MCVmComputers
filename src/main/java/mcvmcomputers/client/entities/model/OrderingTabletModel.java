package mcvmcomputers.client.entities.model;

import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class OrderingTabletModel extends EntityModel<Entity> {
	private final ModelPart tablet;
	private final ModelPart buttons;
	private final ModelPart up;
	private final ModelPart down;
	private final ModelPart left;
	private final ModelPart right;
	private final ModelPart enter;

	public OrderingTabletModel(ModelPart modelPart) {
		tablet = modelPart.getChild("tablet");
		buttons = tablet.getChild("buttons");
		up = buttons.getChild("up");
		down = buttons.getChild("down");
		left = buttons.getChild("left");
		right = buttons.getChild("right");
		enter = buttons.getChild("enter");

		setRotationAngle(buttons, -0.7854F, 0.0F, 0.0F);
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
	
	public void setButtons(boolean up, boolean down, boolean left, boolean right, boolean enter, float deltaTime) {
		if(up) {
			this.up.pivotY = MVCUtils.lerp(this.up.pivotY, -0.5F, deltaTime);
		}else {
			this.up.pivotY = MVCUtils.lerp(this.up.pivotY, -0.9F, deltaTime);
		}
		
		if(down) {
			this.down.pivotY = MVCUtils.lerp(this.down.pivotY, -0.4393F, deltaTime);
		}else {
			this.down.pivotY = MVCUtils.lerp(this.down.pivotY, -0.8393F, deltaTime);
		}
		
		if(left) {
			this.left.pivotY = MVCUtils.lerp(this.left.pivotY, -0.4393F, deltaTime);
		}else {
			this.left.pivotY = MVCUtils.lerp(this.left.pivotY, -0.8393F, deltaTime);
		}
		
		if(right) {
			this.right.pivotY = MVCUtils.lerp(this.right.pivotY, -0.4393F, deltaTime);
		}else {
			this.right.pivotY = MVCUtils.lerp(this.right.pivotY, -0.8393F, deltaTime);
		}
		
		if(enter) {
			this.enter.pivotY = MVCUtils.lerp(this.enter.pivotY, -0.5F, deltaTime);
		}else {
			this.enter.pivotY = MVCUtils.lerp(this.enter.pivotY, -1.1222F, deltaTime);
		}
	}
	
	public void rotateButtons(float rotX, float deltaTime) {
		this.buttons.pitch = MVCUtils.lerp(this.buttons.pitch, rotX, deltaTime);
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float customAngle, float headYaw,
			float headPitch) {
		// TODO Auto-generated method stub
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData t = modelPartData.addChild("tablet", ModelPartBuilder.create().uv(24, 19).cuboid(-6.0F, -2.0F, -6.0F, 12.0F, 1.0F, 1.0F).uv(24, 24).cuboid(-6.0F, -2.0F, 5.0F, 12.0F, 1.0F, 1.0F).uv(12, 20).cuboid(5.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F).uv(0, 19).cuboid(-6.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F).uv(0, 0).cuboid(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData b = t.addChild("buttons", ModelPartBuilder.create().uv(0, 13).cuboid(-6.0F, -0.6979F, -4.3F, 12.0F, 1.0F, 5.0F), ModelTransform.pivot(0.0F, -2.0F, -6.0F));
		b.addChild("up", ModelPartBuilder.create().uv(4, 6).cuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(-2.5F, -0.9F, -0.8243F));
		b.addChild("down", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(-2.5F, -0.8393F, -2.792F));
		b.addChild("left", ModelPartBuilder.create().uv(4, 4).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(-3.5F, -0.8393F, -1.8021F));
		b.addChild("right", ModelPartBuilder.create().uv(0, 4).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F), ModelTransform.pivot(-1.5F, -0.8393F, -1.8021F));
		b.addChild("enter", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -0.5F, -1.5F, 2.0F, 1.0F, 3.0F), ModelTransform.pivot(3.0F, -1.1222F, -1.8021F));

		return TexturedModelData.of(modelData, 64, 64);
	}
}