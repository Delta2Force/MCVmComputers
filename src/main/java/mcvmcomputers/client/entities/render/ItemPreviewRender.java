package mcvmcomputers.client.entities.render;

import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.entities.EntityMouse;
import mcvmcomputers.item.PlacableOrderableItem;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.RandomSeed;

public class ItemPreviewRender extends EntityRenderer<EntityItemPreview>{

	public ItemPreviewRender(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public Identifier getTexture(EntityItemPreview entity) {
		return null;
	}

	public void render(EntityItemPreview entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int i) {
		MinecraftClient mcc = MinecraftClient.getInstance();

		matrixStack.push();
		matrixStack.translate(0, 0.5, 0);

		Vec3d v = mcc.player.getPos();
		Quaternion look = MVCUtils.lookAt(entity.getPos(), new Vec3d(v.x, entity.getY(), v.z));
		matrixStack.multiply(look);
		matrixStack.push();

		if(entity.getPreviewedItemStack().getItem() instanceof PlacableOrderableItem) {
			if(((PlacableOrderableItem)entity.getPreviewedItemStack().getItem()).wallTV) {
				matrixStack.translate(0, 0, -0.1);
			}
		}

		mcc.getItemRenderer().renderItem(entity.getPreviewedItemStack(), Mode.NONE, 200, 0, matrixStack, vertexConsumers, (int)RandomSeed.getSeed());
		matrixStack.pop();
		matrixStack.pop();
	}
}
