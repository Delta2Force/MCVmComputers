package mcvmcomputers.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcvmcomputers.entities.EntityCRTScreen;
import mcvmcomputers.entities.EntityDeliveryChest;
import mcvmcomputers.entities.EntityFlatScreen;
import mcvmcomputers.entities.EntityWallTV;
import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.entities.EntityKeyboard;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.entities.EntityMouse;
import mcvmcomputers.entities.EntityPC;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.thread.ThreadExecutor;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkMixin {
	@Shadow
	private ClientWorld world;
	
	@Shadow
	private MinecraftClient client;
	
	@Inject(at = @At("TAIL"), method = "onEntitySpawn")
	public void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
		EntityType<?> entityType = packet.getEntityTypeId();
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
	    Object entity15 = null;
	    if (entityType == EntityList.ITEM_PREVIEW) {
	    	entity15 = new EntityItemPreview(this.world, d, e, f);
	    }else if (entityType == EntityList.KEYBOARD) {
	    	entity15 = new EntityKeyboard(this.world, d, e, f);
	    }else if (entityType == EntityList.MOUSE) {
	    	entity15 = new EntityMouse(this.world, d, e, f);
	    }else if (entityType == EntityList.CRT_SCREEN) {
	    	entity15 = new EntityCRTScreen(this.world, d, e, f);
	    }else if (entityType == EntityList.FLATSCREEN) {
	    	entity15 = new EntityFlatScreen(this.world, d, e, f);
	    }else if (entityType == EntityList.PC) {
	    	entity15 = new EntityPC(this.world, d, e, f);
	    }else if (entityType == EntityList.DELIVERY_CHEST) {
	    	entity15 = new EntityDeliveryChest(this.world, d, e, f);
	    }else if (entityType == EntityList.WALLTV) {
	    	entity15 = new EntityWallTV(this.world, d, e, f);
	    }
	    
	    if (entity15 != null) {
	         int i = packet.getId();
	         ((Entity)entity15).updateTrackedPosition(d, e, f);
	         ((Entity)entity15).pitch = (float)(packet.getPitch() * 360) / 256.0F;
	         ((Entity)entity15).yaw = (float)(packet.getYaw() * 360) / 256.0F;
	         ((Entity)entity15).setEntityId(i);
	         ((Entity)entity15).setUuid(packet.getUuid());
	         this.world.addEntity(i, (Entity)entity15);
	      }
	}
	
	public void onEntityPosition(EntityPositionS2CPacket packet) {
	      NetworkThreadUtils.forceMainThread(packet, this.client.getNetworkHandler(), (ThreadExecutor<?>)this.client);
	      Entity entity = this.world.getEntityById(packet.getId());
	      if(entity instanceof EntityDeliveryChest) {
	    	  return;
	      }
	      if (entity != null) {
	         double d = packet.getX();
	         double e = packet.getY();
	         double f = packet.getZ();
	         entity.updateTrackedPosition(d, e, f);
	         if (!entity.isLogicalSideForUpdatingMovement()) {
	            float g = (float)(packet.getYaw() * 360) / 256.0F;
	            float h = (float)(packet.getPitch() * 360) / 256.0F;
	            if (Math.abs(entity.getX() - d) < 0.03125D && Math.abs(entity.getY() - e) < 0.015625D && Math.abs(entity.getZ() - f) < 0.03125D) {
	               entity.updateTrackedPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), g, h, 3, true);
	            } else {
	               entity.updateTrackedPositionAndAngles(d, e, f, g, h, 3, true);
	            }

	            entity.setOnGround(packet.isOnGround());
	         }

	      }
	   }
}
