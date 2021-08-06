package mcvmcomputers.client.entities.render;

import mcvmcomputers.entities.EntityMouse;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.utils.MVCUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class MouseRender extends EntityRenderer<EntityMouse>{
	public MouseRender(EntityRendererFactory.Context dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(EntityMouse entity) {
		return null;
	}
	
	@Override
	public void render(EntityMouse entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(0, 0.5, 0);
		Quaternion look = MVCUtils.lookAt(entity.getPos(), entity.getLookAtPos());
		matrices.multiply(look);
		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_MOUSE), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
		matrices.pop();
	}

}
