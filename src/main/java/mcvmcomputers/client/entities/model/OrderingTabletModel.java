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
	private ModelPart builtModel;

	private static final int TEXTURE_WIDTH = 64;
	private static final int TEXTURE_HEIGHT = 64;

	public OrderingTabletModel() {
		ModelPartData model = new ModelData().getRoot();

		ModelPartBuilder tablet = createMPB(new MPB[]{
			new MPB(-6f, -2f, -6f, 12f, 1f, 1f, false, 24, 19),
			new MPB(-6f, -2f, 5f, 12f, 1f, 1f, false, 24, 24),
			new MPB(5f, 2f, -5f, 1f, 1f, 10f, false, 12, 20),
			new MPB(-6f, -2f, -5f, 1f, 1f, 10f, false, 0, 19),
			new MPB(-6f, -1f, -6f, 12f, 1f, 12f, false, 0, 0)
		});
		model.addChild("tablet", tablet, ModelTransform.pivot(0f, 24f, 0f));

		ModelPartBuilder buttons = createMPB(-6f, -0.6979f, -4.3f, 12f, 1f, 5f, false, 0, 13);
		model.getChild("tablet").addChild("buttons", buttons, ModelTransform.of(0f, -2f, -6f, -0.7854f, 0f, 0f));

		ModelPartBuilder up = createMPB(-0.5f, -0.4f, -0.5f, 1f, 1f, 1f, false, 4, 6);
		model.getChild("tablet").getChild("buttons").addChild("up", up, ModelTransform.pivot(-2.5f, -0.9f, -0.8243f));

		ModelPartBuilder down = createMPB(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, false, 0, 6);
		model.getChild("tablet").getChild("buttons").addChild("down", down, ModelTransform.pivot(-2.5f, -0.8393f, -2.792f));

		ModelPartBuilder left = createMPB(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, false, 4, 4);
		model.getChild("tablet").getChild("buttons").addChild("left", left, ModelTransform.pivot(-3.5f, -0.8393f, -1.8021f));

		ModelPartBuilder right = createMPB(-0.5f, -0.5f, -0.5f, 1f, 1f, 1f, false, 0, 4);
		model.getChild("tablet").getChild("buttons").addChild("right", right, ModelTransform.pivot(-1.5f, -0.8393f, -1.8021f));

		ModelPartBuilder enter = createMPB(-1f, -0.5f, -1.5f, 2f, 1f, 3f, false, 0, 0);
		model.getChild("tablet").getChild("buttons").addChild("enter", enter, ModelTransform.pivot(3f, -1.1222f, -1.8021f));

		builtModel = model.createPart(TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		builtModel.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setButtons(boolean up, boolean down, boolean left, boolean right, boolean enter, float deltaTime) {
		ModelPart upPart = builtModel.getChild("tablet").getChild("buttons").getChild("up"),
				downPart = builtModel.getChild("tablet").getChild("buttons").getChild("down"),
				leftPart = builtModel.getChild("tablet").getChild("buttons").getChild("left"),
				rightPart = builtModel.getChild("tablet").getChild("buttons").getChild("right"),
				enterPart = builtModel.getChild("tablet").getChild("buttons").getChild("enter");

		upPart.pivotY = MVCUtils.lerp(upPart.pivotY, up ? -0.5F : -0.9F, deltaTime);
		downPart.pivotY = MVCUtils.lerp(downPart.pivotY, down ? -0.5F : -0.9F, deltaTime);
		leftPart.pivotY = MVCUtils.lerp(leftPart.pivotY, left ? -0.5F : -0.9F, deltaTime);
		rightPart.pivotY = MVCUtils.lerp(rightPart.pivotY, right ? -0.5F : -0.9F, deltaTime);
		enterPart.pivotY = MVCUtils.lerp(enterPart.pivotY, enter ? -0.5F : -1.12F, deltaTime);
	}
	
	public void rotateButtons(float rotX, float deltaTime) {
		ModelPart buttonsPart = builtModel.getChild("tablet").getChild("buttons");
		buttonsPart.pitch = MVCUtils.lerp(buttonsPart.pitch, rotX, deltaTime);
	}

	@Override
	public void setAngles(Entity entity, float limbAngle, float limbDistance, float customAngle, float headYaw,
			float headPitch) {
		// TODO Auto-generated method stub
	}
}