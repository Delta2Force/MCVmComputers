package mcvmcomputers.client.entities.render;

import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.item.PlacableOrderableItem;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class ItemPreviewRender extends EntityRenderer<EntityItemPreview>{
	public ItemPreviewRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(EntityItemPreview entity) {
		return null;
	}
	
	@Override
	public void render(EntityItemPreview entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
		MinecraftClient mcc = MinecraftClient.getInstance();
		
		matrices.push();
		
		matrices.translate(0, 0.5, 0);
		Vec3d v = mcc.player.getPos();
		Quaternion look = MVCUtils.lookAt(entity.getPos(), new Vec3d(v.x, entity.getY(), v.z));
		matrices.multiply(look);
		matrices.push();
		if(entity.getPreviewedItemStack().getItem() instanceof PlacableOrderableItem) {
			if(((PlacableOrderableItem)entity.getPreviewedItemStack().getItem()).wallTV) {
				matrices.translate(0, 0, -0.1);
			}
		}
		MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getPreviewedItemStack(), Mode.NONE, 200, 0, matrices, vertexConsumers);
		matrices.pop();
		matrices.pop();
	}
}
