package mcvmcomputers.entities.render;

import java.io.IOException;

import com.mojang.blaze3d.systems.RenderSystem;

import mcvmcomputers.MCVmComputersMod;
import mcvmcomputers.entities.EntityDeliveryChest;
import mcvmcomputers.entities.model.DeliveryChestModel;
import mcvmcomputers.utils.MVCUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class DeliveryChestRender extends EntityRenderer<EntityDeliveryChest>{
	private DeliveryChestModel deliveryChestModel;
	
	public DeliveryChestRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
		
	}

	@Override
	public Identifier getTexture(EntityDeliveryChest entity) {
		return null;
	}
	
	private void checkModel() {
		if(deliveryChestModel == null) {
			try {
				deliveryChestModel = new DeliveryChestModel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void applyRotations(EntityDeliveryChest entity) {
		deliveryChestModel.setRotationAngle(deliveryChestModel.upleg0, 0, 0, entity.upLeg01Rot);
		deliveryChestModel.setRotationAngle(deliveryChestModel.upleg1, 0, 0, entity.upLeg01Rot);
		deliveryChestModel.setRotationAngle(deliveryChestModel.upleg2, 0, 0, entity.upLeg23Rot);
		deliveryChestModel.setRotationAngle(deliveryChestModel.upleg3, 0, 0, entity.upLeg23Rot);
		
		deliveryChestModel.setRotationAngle(deliveryChestModel.uleg0, 0, 0, entity.uLeg01Rot);
		deliveryChestModel.setRotationAngle(deliveryChestModel.uleg1, 0, 0, entity.uLeg01Rot);
		deliveryChestModel.setRotationAngle(deliveryChestModel.uleg2, 0, 0, entity.uLeg23Rot);
		deliveryChestModel.setRotationAngle(deliveryChestModel.uleg3, 0, 0, entity.uLeg23Rot);
		
		deliveryChestModel.setRotationAngle(deliveryChestModel.opening, entity.openingRot, 0, 0);
	}
	
	@Override
	public void render(EntityDeliveryChest entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		this.checkModel();
		this.applyRotations(entity);
		Vec3d v = new Vec3d(MVCUtils.lerp(entity.getX(), entity.getTargetX(), MCVmComputersMod.deltaTime), MVCUtils.lerp(entity.getY(), entity.getTargetY(), MCVmComputersMod.deltaTime), MVCUtils.lerp(entity.getZ(), entity.getTargetZ(), MCVmComputersMod.deltaTime));
		
		entity.setPos(v.x,v.y,v.z);
		EntityDeliveryChest serverEntity = null;
		for(ServerWorld sw : MinecraftClient.getInstance().getServer().getWorlds()) {
			if(sw.getEntityById(entity.getEntityId()) != null) {
				serverEntity = (EntityDeliveryChest) sw.getEntityById(entity.getEntityId());
				serverEntity.setPos(v.x, v.y, v.z);
			}
		}
		
		double dist = entity.getPos().distanceTo(new Vec3d(entity.getTargetX(),entity.getTargetY(),entity.getTargetZ()));
		if(dist < 0) {
			dist = -dist;
		}
		System.out.println(dist); // <- Do rotation, it rotates the delivery chest based on distance -> renderRot
		
		if(dist < 10) {
			entity.upLeg01Rot = MVCUtils.lerp(entity.upLeg01Rot, 3f, MCVmComputersMod.deltaTime);
			entity.upLeg23Rot = MVCUtils.lerp(entity.upLeg23Rot, 3.3f, MCVmComputersMod.deltaTime);
			entity.uLeg01Rot = MVCUtils.lerp(entity.uLeg01Rot, -2.4f, MCVmComputersMod.deltaTime);
			entity.uLeg01Rot = MVCUtils.lerp(entity.uLeg01Rot, 2.7f, MCVmComputersMod.deltaTime);
		}
		
		matrices.push();
			matrices.multiply(new Quaternion(entity.renderRot, 0, 0, true));
			matrices.translate(0, -1.5, 0);
			deliveryChestModel.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
		matrices.pop();
		
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

}
