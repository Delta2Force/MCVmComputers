package mcvmcomputers.client.entities.render;

import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class PCRender extends EntityRenderer<EntityPC>{
	public PCRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(EntityPC entity) {
		return null;
	}
	
	@Override
	public void render(EntityPC entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.translate(0, 0.5, 0);
		Quaternion look = MVCUtils.lookAt(entity.getPos(), entity.getLookAtPos());
		matrices.multiply(look);
		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.PC_CASE_NO_PANEL), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
		
		matrices.push();
		matrices.multiply(new Quaternion(0f, 0f, -90f, true));
		matrices.multiply(new Quaternion(0f, -90f, 0f, true));
		matrices.scale(0.55f, 0.55f, 0.55f);
		matrices.translate(0.06f, 0.28f, -0.29f);
		if(entity.getMotherboardInstalled()) {
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_MOTHERBOARD), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
		}
		if(entity.getGpuInstalled()) {
			matrices.push();
			matrices.translate(0.24, 0.07f, -0.28f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_GPU), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
		if(entity.getCpuDividedBy() > 0) {
			matrices.push();
			matrices.translate(0.06, 0.12f, 0.06f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_CPU2), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
		if(entity.getGigsOfRamInSlot0() > 0) {
			matrices.push();
			matrices.multiply(new Quaternion(0f, 90f, 0f, true));
			matrices.translate(-0.22f, 0.1f, -0.285f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_RAM1G), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
		if(entity.getGigsOfRamInSlot1() > 0) {
			matrices.push();
			matrices.multiply(new Quaternion(0f, 90f, 0f, true));
			matrices.translate(-0.22f, 0.1f, -0.404f);
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_RAM1G), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
		if(!entity.getHardDriveFileName().isEmpty()) {
			matrices.push();
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 90, 0, true));
			matrices.translate(-0.2f, 0, -0.6);
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.ITEM_HARDDRIVE), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
		matrices.pop();
		
		if(entity.getGlassSidepanel()) {
			matrices.push();
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.PC_CASE_GLASS_PANEL), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}else {
			matrices.push();
			MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ItemList.PC_CASE_ONLY_PANEL), Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
			matrices.pop();
		}
		matrices.pop();
	}

}
