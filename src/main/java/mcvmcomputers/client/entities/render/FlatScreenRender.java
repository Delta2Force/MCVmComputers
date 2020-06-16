package mcvmcomputers.client.entities.render;

import mcvmcomputers.ClientMod;
import mcvmcomputers.MainMod;
import mcvmcomputers.entities.EntityFlatScreen;
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

public class FlatScreenRender extends EntityRenderer<EntityFlatScreen>{
	public FlatScreenRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(EntityFlatScreen entity) {
		return null;
	}
	
	@Override
	public void render(EntityFlatScreen entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(0, 0.5, 0);
		Quaternion look = MVCUtils.lookAt(entity.getPosVector(), entity.getLookAtPos());
		matrices.multiply(look);
		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_FLATSCREEN), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
		if(ClientMod.vmScreenTextures.containsKey(entity.getUuid())) {
			matrices.push();
			matrices.scale(0.006f, 0.006f, 0.006f);
			matrices.multiply(new Quaternion(0f, 0f, 0f, true));
			matrices.multiply(new Quaternion(0, 0, 180, true));
			matrices.translate(-59.4f, -18.3f, -13f);
			matrices.scale(0.8079f, 0.488f, 1f);
			matrices.translate(10, 5.4f, 7.76f);
			Matrix4f matrix4f = matrices.peek().getModel();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(ClientMod.vmScreenTextures.get(entity.getUuid())));
			vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(light).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(light).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(light).next();
	        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(light).next();
			matrices.pop();
		}
		matrices.pop();
	}

}