package mcvmcomputers.client.entities.render;

import java.awt.Color;
import java.io.IOException;

import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.entities.model.DeliveryChestModel;
import mcvmcomputers.entities.EntityDeliveryChest;
import mcvmcomputers.sound.SoundList;
import mcvmcomputers.utils.TabletOrder.OrderStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.World;

import static mcvmcomputers.client.ClientMod.*;
import static mcvmcomputers.utils.MVCUtils.*;

public class DeliveryChestRender extends EntityRenderer<EntityDeliveryChest>{
	private DeliveryChestModel deliveryChestModel;
	private MinecraftClient mcc;
	
	public DeliveryChestRender(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
		mcc = MinecraftClient.getInstance();
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
		
		deliveryChestModel.fireYes = entity.fire;
	}
	
	private Vec3d renderPos(EntityDeliveryChest entity) {
		return new Vec3d(entity.getX(), entity.getY()+entity.renderOffY, entity.getZ()+entity.renderOffZ);
	}
	
	private void changeRotations(EntityDeliveryChest entity) {
		if(entity.fire) {
			if(entity.rocketSound == null) {
				entity.rocketSound = new MovingSoundInstance(SoundList.ROCKET_SOUND, SoundCategory.MASTER) {
					@Override
					public void tick() {
						Vec3d v = new Vec3d(entity.getX(), entity.getY() + entity.renderOffY, entity.getZ() + entity.renderOffZ);
						double dist = v.distanceTo(mcc.player.getPos());
						if(dist < 0) {
							dist = -dist;
						}
						dist = Math.min(dist, 40)/40.0;
						dist = 1 - dist;
						
						this.volume = (float) dist;
					}
					@Override
					public boolean shouldAlwaysPlay() {
						return false;
					}
					@Override
					public AttenuationType getAttenuationType() {
						return AttenuationType.LINEAR;
					}
					
					@Override
					public boolean isLooping() {
						return true;
					}
					
					@Override
					public boolean isRepeatable() {
						return true;
					}
					
					@Override
					public int getRepeatDelay() {
						return 0;
					}
				};
				mcc.getSoundManager().play(entity.rocketSound);
			}
		}else {
			if(entity.rocketSound != null) {
				mcc.getSoundManager().stop(entity.rocketSound);
				entity.rocketSound = null;
			}
		}
		if(!entity.getTakingOff()) {
			Vec3d curPos = renderPos(entity);
			Vec3d v = new Vec3d(lerp(curPos.getX(), entity.getTargetX(), deltaTime/2f), lerp(curPos.getY(), entity.getTargetY(), deltaTime/2f), lerp(curPos.getZ(), entity.getTargetZ(), deltaTime/2f));
			entity.updateRenderPos(v.x, v.y, v.z);
			double dist = renderPos(entity).distanceTo(new Vec3d(entity.getTargetX(),entity.getTargetY(),entity.getTargetZ()));
			if(dist < 0) {
				dist = -dist;
			}
			double prog = Math.min(dist / 40f, 1f);
			
			prog -= 2;
			prog = -prog;
			
			entity.renderRot = (float) (90f + (45f * prog));
			
			if(dist > 25) {
				entity.fire = false;
			}else {
				entity.fire = true;
			}
			
			if(dist < 3) {
				entity.upLeg01Rot = lerp(entity.upLeg01Rot, 0f, deltaTime);
				entity.upLeg23Rot = lerp(entity.upLeg23Rot, 0f, deltaTime);
				entity.uLeg01Rot = lerp(entity.uLeg01Rot, 0f, deltaTime);
				entity.uLeg23Rot = lerp(entity.uLeg23Rot, 0f, deltaTime);
			}
			if (dist < 0.1) {
				entity.openingRot = lerp(entity.openingRot, -2F, deltaTime);
				entity.fire = false;
				entity.updateRenderPos(entity.getTargetX(), entity.getTargetY(), entity.getTargetZ());
			}
		}else {
			entity.takeOffSpeed = lerp(entity.takeOffSpeed, 5f, deltaTime/180f);
			Vec3d curPos = renderPos(entity);
			Vec3d v = new Vec3d(curPos.getX(),curPos.getY()+entity.takeOffSpeed, curPos.getZ());
			entity.updateRenderPos(v.x, v.y, v.z);
			
			entity.upLeg01Rot = lerp(entity.upLeg01Rot, 3f, deltaTime);
			entity.upLeg23Rot = lerp(entity.upLeg23Rot, 3.3f, deltaTime);
			entity.uLeg01Rot = lerp(entity.uLeg01Rot, -2.7f, deltaTime);
			entity.uLeg23Rot = lerp(entity.uLeg23Rot, -2.7f, deltaTime);
			entity.openingRot = lerp(entity.openingRot, 0f, deltaTime);
			entity.fire = true;
			
			if(curPos.getY() > 250) {
				if(entity.rocketSound != null) {
					if(mcc.getSoundManager().isPlaying(entity.rocketSound)) {
						mcc.getSoundManager().stop(entity.rocketSound);
						entity.rocketSound = null;
					}
				}
			}
		}
	}
	
	private void smokeParticle(World w, Vec3d pos, int amount) {
		for(int i = 0;i<amount;i++) {
			if(amount == 3) {
				w.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, (DeliveryChestModel.TEX_RANDOM.nextFloat()*0.5f)-.25f, DeliveryChestModel.TEX_RANDOM.nextFloat()*-.3F, (DeliveryChestModel.TEX_RANDOM.nextFloat()*.5f)-.25f);
			}else if(amount==6){
				w.addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, (DeliveryChestModel.TEX_RANDOM.nextFloat()*0.5f)-.25f, DeliveryChestModel.TEX_RANDOM.nextFloat()*-.3F, (DeliveryChestModel.TEX_RANDOM.nextFloat()*.5f)-.25f);
			}else {
				w.addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, (DeliveryChestModel.TEX_RANDOM.nextFloat()*2f)-1f, DeliveryChestModel.TEX_RANDOM.nextFloat()*.3F, (DeliveryChestModel.TEX_RANDOM.nextFloat()*2f)-1f);
			}
		}
	}
	
	private void doParticlesForFire(EntityDeliveryChest entity) {
		Vec3d curPos = renderPos(entity);
		
		smokeParticle(entity.world, curPos, 3);
		smokeParticle(entity.world, curPos, 6);
		
		Vec3d ground = new Vec3d(curPos.getX(), entity.world.getTopY(Type.MOTION_BLOCKING, (int)curPos.getX(), (int)curPos.getZ()), curPos.getZ());
		double dist = ground.distanceTo(curPos);
		if(dist < 0) {
			dist = -dist;
		}
		
		if(dist < 5) {
			if(dist > 4 && dist < 5) {
				smokeParticle(entity.world, ground, 1);
			}
			else if(dist > 3 && dist < 4) {
				smokeParticle(entity.world, ground, 2);
			}
			else if(dist > 2 && dist < 3) {
				smokeParticle(entity.world, ground, 4);
			}
			else if(dist > 1 && dist < 2) {
				smokeParticle(entity.world, ground, 8);
			}
			else if(dist > 0 && dist < 1) {
				smokeParticle(entity.world, ground, 16);
			}
		}
	}
	
	@Override
	public boolean shouldRender(EntityDeliveryChest entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
		return true;
	}
	
	@Override
	public void render(EntityDeliveryChest entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		this.checkModel();
		this.changeRotations(entity);
		this.applyRotations(entity);
		if(entity.fire) {
			this.doParticlesForFire(entity);
		}
		
		matrices.push();
		matrices.translate(0, entity.renderOffY, entity.renderOffZ);
		
		matrices.push();
			matrices.multiply(new Quaternion(entity.renderRot, 0, 0, true));
			matrices.translate(0, -1.5, 0);
			deliveryChestModel.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
			matrices.push();
				matrices.multiply(new Quaternion(-90, 0, 0, true));
				matrices.scale(0.02f, 0.02f, 0.02f);
				matrices.translate(-15.63, -15.63, 6.22);
				matrices.push();   
					matrices.scale(0.4f, 0.4f, 0.4f);
					if(ClientMod.myOrder != null) {
						if(entity.getDeliveryUUID().equals(ClientMod.myOrder.orderUUID)) {
							if(ClientMod.myOrder.currentStatus == OrderStatus.PAYMENT_CHEST_ARRIVED || ClientMod.myOrder.currentStatus == OrderStatus.PAYMENT_CHEST_RECEIVING) {
								matrices.translate(0, -5, 0);
								this.getFontRenderer().draw("Please insert", 6, 25, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
								String s = ""+ClientMod.myOrder.price;
								this.getFontRenderer().draw(s, (39) - this.getFontRenderer().getWidth(s)/2, 33, new Color(0.4f,0.4f,1f,1f).getRGB(), false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
								this.getFontRenderer().draw("Iron Ingots", 10, 41, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
								this.getFontRenderer().draw("by clicking", 13, 50, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
								this.getFontRenderer().draw("this chest", 14, 59, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
							}else if(ClientMod.myOrder.currentStatus == OrderStatus.ORDER_CHEST_ARRIVED || ClientMod.myOrder.currentStatus == OrderStatus.ORDER_CHEST_RECEIVED) {
								String s = ClientMod.myOrder.items.size() + " items";
								this.getFontRenderer().draw(s, (39) - this.getFontRenderer().getWidth(s)/2, 20, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,1f,0.2f).getRGB(), light);
								matrices.push();
									matrices.translate(0.5, 0, 0);
									this.getFontRenderer().draw("in chest", 19, 30, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
								matrices.pop();
								this.getFontRenderer().draw("Collect by", 14, 44, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
								this.getFontRenderer().draw("clicking", 21, 52, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(0f,0f,0f,0f).getRGB(), light);
							}
						}else {
							this.getFontRenderer().draw("This is not", 13, 30, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(1f,0f,0f,1f).getRGB(), light);
							this.getFontRenderer().draw("your chest!", 10, 40, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(1f,0f,0f,1f).getRGB(), light);
						}
					}else {
						this.getFontRenderer().draw("This is not", 13, 30, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(1f,0f,0f,1f).getRGB(), light);
						this.getFontRenderer().draw("your chest!", 10, 40, -1, false, matrices.peek().getModel(), vertexConsumers, false, new Color(1f,0f,0f,1f).getRGB(), light);
					}
				matrices.pop();
			matrices.pop();
		matrices.pop();
		
		matrices.pop();
		
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}

}
