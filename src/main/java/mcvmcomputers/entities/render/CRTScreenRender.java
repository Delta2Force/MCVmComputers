package mcvmcomputers.entities.render;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.entities.EntityCRTScreen;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class CRTScreenRender extends EntityRenderer<EntityCRTScreen>{
	public CRTScreenRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(EntityCRTScreen entity) {
		return null;
	}
	
	@Override
	public void render(EntityCRTScreen entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(0, 0.5, 0);
		Quaternion look = MVCUtils.lookAt(entity.getPosVector(), entity.getLookAtPos());
		matrices.multiply(look);
		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_CRTSCREEN), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
		if(MCVmComputersMod.vmTextureIdentifier != null) {
			matrices.push();
			matrices.scale(0.006f, 0.006f, 0.006f);
			matrices.multiply(new Quaternion(22.5f, 0f, 0f, true));
			matrices.multiply(new Quaternion(0, 0, 180, true));
			matrices.translate(-63.1f, -27.7f, -20f);
			matrices.scale(0.736f, 0.597f, 1f);
			matrices.translate(22, 1.6f, 7.6f);
			Matrix4f matrix4f = matrices.peek().getModel();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(MCVmComputersMod.vmTextureIdentifier));
			vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
	        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
			matrices.pop();
		}
		matrices.pop();
	}

}
