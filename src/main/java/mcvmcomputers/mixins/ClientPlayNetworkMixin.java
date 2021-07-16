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
