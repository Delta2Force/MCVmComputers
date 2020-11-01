package mcvmcomputers.client.entities.render;

import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.entities.EntityWallTV;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

public class WallTVRender extends EntityRenderer<EntityWallTV>{
	protected static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () -> {
	      RenderSystem.enableBlend();
	      RenderSystem.defaultBlendFunc();
	   }, () -> {
	      RenderSystem.disableBlend();
	   });
	protected static final RenderPhase.Alpha ONE_TENTH_ALPHA = new RenderPhase.Alpha(0.003921569F);
	
	public WallTVRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(EntityWallTV entity) {
		return null;
	}
	
	@Override
	public void render(EntityWallTV entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		if(entity.getOwnerUUID().isEmpty()) {
			return;
		}
		
		matrices.push();
		matrices.translate(0, 0.5, 0);
		Quaternion look = MVCUtils.lookAt(entity.getPos(), entity.getLookAtPos());
		matrices.multiply(look);
		matrices.push();
		matrices.translate(0, 0, -0.1);
		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_WALLTV), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
		matrices.pop();
		if(ClientMod.vmScreenTextures.containsKey(UUID.fromString(entity.getOwnerUUID()))) {
			matrices.push();
			matrices.scale(0.0198f, 0.014f, 0.006f);
			matrices.multiply(new Quaternion(0, 0, 180, true));
			matrices.translate(-63.1f, -45.7f, -24.4f);
			matrices.scale(0.736f, 0.597f, 1f);
			matrices.translate(22, 1.6f, 7.6f);
			Matrix4f matrix4f = matrices.peek().getModel();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.of("vmscreen", VertexFormats.POSITION_COLOR_TEXTURE, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ClientMod.vmScreenTextures.get(UUID.fromString(entity.getOwnerUUID())), false, false)).alpha(ONE_TENTH_ALPHA).transparency(TRANSLUCENT_TRANSPARENCY).build(false)));
			vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).next();
	        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).next();
			matrices.pop();
		}
		matrices.pop();
	}

}
