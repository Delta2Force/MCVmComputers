package mcvmcomputers.client.entities.render;

import java.awt.*;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.entities.EntityCRTScreen;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.utils.MVCUtils;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;

public class CRTScreenRender extends EntityRenderer<EntityCRTScreen> {
	protected static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () -> {
	      RenderSystem.enableBlend();
	      RenderSystem.defaultBlendFunc();
	   }, RenderSystem::disableBlend);

	public CRTScreenRender(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public Identifier getTexture(EntityCRTScreen entity) {
		return null;
	}
	
	@Override
	public void render(EntityCRTScreen entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		if(entity.getOwnerUUID().isEmpty()) {
			return;
		}
		
		matrices.push();
		matrices.translate(0, 0.5, 0);
		matrices.multiply(entity.getOrientation());
		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_CRTSCREEN), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, (int)RandomSeed.getSeed());
		if(ClientMod.vmScreenTextures.containsKey(UUID.fromString(entity.getOwnerUUID()))) {
			matrices.push();
			matrices.scale(0.006f, 0.006f, 0.006f);
			matrices.multiply(new Quaternion(22.5f, 0f, 0f, true));
			matrices.multiply(new Quaternion(0, 0, 180, true));
			matrices.translate(-63.1f, -27.7f, -20f);
			matrices.scale(0.736f, 0.597f, 1f);
			matrices.translate(22, 1.6f, 7.6f);
			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.of("vmscreen", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.values()[7], 256, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ClientMod.vmScreenTextures.get(UUID.fromString(entity.getOwnerUUID())), false, false)).transparency(TRANSLUCENT_TRANSPARENCY).build(false)));
			vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 1.0F).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 1.0F).next();
	        vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(1.0F, 0.0F).next();
	        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).texture(0.0F, 0.0F).next();
			matrices.pop();
		}
		matrices.pop();
	}

}
