package mcvmcomputers.client.entities.model;

import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import static mcvmcomputers.client.utils.ModelPartBuilderEx.createMPB;
import mcvmcomputers.client.utils.ModelPartBuilderEx.*;

public class OrderingTabletModel extends EntityModel<Entity> {
	private ModelPartData model;
	private ModelPartBuilder tablet, buttons, up, down, left, right, enter;

	private final int textureWidth = 64;
	private final int textureHeight = 64;

	public OrderingTabletModel() {
		model = new ModelData().getRoot();

		tablet = createMPB(new MPB[]{
			new MPB(-6f, -2f, -6f, 12f, 1f, 1f, false, 24, 19),
			new MPB(-6f, -2f, 5f, 12f, 1f, 1f, false, 24, 24),
			new MPB(5f, 2f, -5f, 1f, 1f, 10f, false, 12, 20),
			new MPB(-6f, -2f, -5f, 1f, 1f, 10f, false, 0, 19),
			new MPB(-6f, -1f, -6f, 12f, 1f, 12f, false, 0, 0)
		});
		model.addChild("tablet", tablet, ModelTransform.pivot(0f, 24f, 0f));

		buttons = createMPB(-6f, -0.6979f, -4.3f, 12f, 1f, 5f, false, 0, 13);
		model.getChild("tablet").addChild("buttons", buttons, ModelTransform.of(0f, -2f, -6f, -0.7854f, 0f, 0f));

		up = createMPB(-0.5f, -0.4f, -0.5f, 1f, 1f, 1f, false, 4, 6);
		model.getChild("buttons").addChild("up", up, ModelTransform.pivot(-2.5f, -0.9f, -0.8243f));

		down = createMPB(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, false, 0, 6);
		model.getChild("buttons").addChild("down", down, ModelTransform.pivot(-2.5f, -0.8393f, -2.792f));

		left = createMPB(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, false, 4, 4);
		model.getChild("buttons").addChild("left", left, ModelTransform.pivot(-3.5f, -0.8393f, -1.8021f));

		right = createMPB(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, false, 0, 4);
		model.getChild("buttons").addChild("right", right, ModelTransform.pivot(-1.5f, -0.8393f, -1.8021f));

		enter = createMPB(-1f, -0.5f, -1.5f, 2f, 1f, 3f, false, 0, 0);
		model.getChild("buttons").addChild("enter", enter, ModelTransform.pivot(3f, -1.1222f, -1.8021f));
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		model.createPart(textureWidth, textureHeight).render(matrixStack, buffer, packedLight, packedOverlay);
	}
	
	public void setButtons(boolean up, boolean down, boolean left, boolean right, boolean enter, float deltaTime) {
		// TODO this doesn't work because of how I rewrote the model class
		//  	- Y2K4
		/*if(up) {
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
		}*/
	}
	
	public void rotateButtons(float rotX, float deltaTime) {
		/*this.buttons.pitch = MVCUtils.lerp(this.buttons.pitch, rotX, deltaTime);*/
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float customAngle, float headYaw,
			float headPitch) {
		// TODO Auto-generated method stub
	}
}