package mcvmcomputers.entities.model;

import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.model.ModelPart;
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

	public OrderingTabletModel() {
		textureWidth = 64;
		textureHeight = 64;

		tablet = new ModelPart(this);
		tablet.setPivot(0.0F, 24.0F, 0.0F);
		tablet.setTextureOffset(24, 19).addCuboid(-6.0F, -2.0F, -6.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		tablet.setTextureOffset(24, 24).addCuboid(-6.0F, -2.0F, 5.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		tablet.setTextureOffset(12, 20).addCuboid(5.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F, 0.0F, false);
		tablet.setTextureOffset(0, 19).addCuboid(-6.0F, -2.0F, -5.0F, 1.0F, 1.0F, 10.0F, 0.0F, false);
		tablet.setTextureOffset(0, 0).addCuboid(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F, 0.0F, false);
		
		/*
		buttons = new ModelPart(this);
		buttons.setPivot(0.0F, -3.4F, -7.1F);
		tablet.addChild(buttons);
		setRotationAngle(buttons, -0.7854F, 0.0F, 0.0F);
		buttons.setTextureOffset(0, 13).addCuboid(-6.0F, -0.5F, -2.5F, 12.0F, 1.0F, 5.0F, 0.0F, false);

		up = new ModelPart(this);
		up.setPivot(-2.0F, -0.5F, 1.0657F);
		buttons.addChild(up);
		up.setTextureOffset(4, 6).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		down = new ModelPart(this);
		down.setPivot(-2.0F, -0.5F, -1.1971F);
		buttons.addChild(down);
		down.setTextureOffset(0, 6).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		left = new ModelPart(this);
		left.setPivot(-3.1F, -0.5F, -0.0657F);
		buttons.addChild(left);
		left.setTextureOffset(4, 4).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		right = new ModelPart(this);
		right.setPivot(-0.9F, -0.5F, -0.0657F);
		buttons.addChild(right);
		right.setTextureOffset(0, 4).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		enter = new ModelPart(this);
		enter.setPivot(2.4F, -0.5F, -0.0657F);
		buttons.addChild(enter);
		enter.setTextureOffset(0, 0).addCuboid(-1.0F, -0.5F, -1.5F, 2.0F, 1.0F, 3.0F, 0.0F, false);
		*/
		
		buttons = new ModelPart(this);
		buttons.setPivot(0.0F, -2.0F, -6.0F);
		tablet.addChild(buttons);
		setRotationAngle(buttons, -0.7854F, 0.0F, 0.0F);
		buttons.setTextureOffset(0, 13).addCuboid(-6.0F, -0.6979F, -4.3F, 12.0F, 1.0F, 5.0F, 0.0F, false);

		up = new ModelPart(this);
		up.setPivot(-2.5F, -0.9F, -0.8243F);
		buttons.addChild(up);
		up.setTextureOffset(4, 6).addCuboid(-0.5F, -0.4F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		down = new ModelPart(this);
		down.setPivot(-2.5F, -0.8393F, -2.792F);
		buttons.addChild(down);
		down.setTextureOffset(0, 6).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		left = new ModelPart(this);
		left.setPivot(-3.5F, -0.8393F, -1.8021F);
		buttons.addChild(left);
		left.setTextureOffset(4, 4).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		right = new ModelPart(this);
		right.setPivot(-1.5F, -0.8393F, -1.8021F);
		buttons.addChild(right);
		right.setTextureOffset(0, 4).addCuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		enter = new ModelPart(this);
		enter.setPivot(3.0F, -1.1222F, -1.8021F);
		buttons.addChild(enter);
		enter.setTextureOffset(0, 0).addCuboid(-1.0F, -0.5F, -1.5F, 2.0F, 1.0F, 3.0F, 0.0F, false);
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
}