package mcvmcomputers.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.item.ItemOrderingTablet;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Mixin(HeldItemRenderer.class)
public class HeldItemMixin {
	@Shadow
	private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {}
	
	@Inject(at = @At("HEAD"), method = "renderFirstPersonItem")
	private void renderItemHead(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if(!item.isEmpty()) {
			if(item.getItem() instanceof ItemOrderingTablet) {
				matrices.push();
				matrices.translate(0, -equipProgress*2, 0);
					matrices.push();
						matrices.translate(0, 0.1, 0.38);
						matrices.multiply(new Quaternion(-90, 0, 0, true));
						MCVmComputersMod.tabletOS.orderingTabletModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(new Identifier("mcvmcomputers", "textures/entity/tablet.png"))), light, OverlayTexture.DEFAULT_UV, 0, 0, 0, 1);
						MCVmComputersMod.renderDisplay(player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
					matrices.pop();
					matrices.push();
						boolean b = hand == Hand.MAIN_HAND;
						Arm arm = b ? Arm.RIGHT : Arm.LEFT;
						matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
						matrices.translate(1.03, 0.20, 0);
						this.renderArm(matrices, vertexConsumers, light, arm);
					matrices.pop();
				matrices.pop();
			}
		}
		matrices.push();
	}
	
	@Inject(at = @At("TAIL"), method = "renderFirstPersonItem")
	private void renderItemTail(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		matrices.pop();
	}
}
