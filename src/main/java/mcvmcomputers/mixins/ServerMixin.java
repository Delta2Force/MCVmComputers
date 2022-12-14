package mcvmcomputers.mixins;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import mcvmcomputers.MainMod;
import mcvmcomputers.entities.EntityDeliveryChest;
import mcvmcomputers.item.OrderableItem;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.utils.TabletOrder;
import mcvmcomputers.utils.TabletOrder.OrderStatus;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(MinecraftServer.class)
public class ServerMixin {
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Shadow
	private float tickTime;
	
	@Shadow
	private PlayerManager playerManager;
	
	@Inject(at = @At("HEAD"), method = "shutdown")
	protected void shutdown(CallbackInfo ci) {
		LOGGER.info("Stopping VM Computers");
		MainMod.computers.clear();
		MainMod.orders.clear();
	}
	
	@Inject(at = @At("HEAD"), method = "tick")
	protected void tick(CallbackInfo ci) {
		for(TabletOrder order : MainMod.orders.values()) {
			switch (order.currentStatus) {
				case ORDER_CHEST_ARRIVAL_SOON, PAYMENT_CHEST_ARRIVAL_SOON -> {
					order.tickCount += tickTime / 1000f;
					if (order.tickCount > 5) {
						order.currentStatus = OrderStatus.values()[order.currentStatus.ordinal() + 1];
						order.tickCount = 0;
					}
				}
				case ORDER_CHEST_RECEIVED -> {
					order.tickCount += tickTime / 1000f;
					if (order.tickCount > 0.25) {
						MainMod.orders.remove(UUID.fromString(order.orderUUID));
					}
				}
				case ORDER_CHEST_ARRIVED, PAYMENT_CHEST_ARRIVED -> {
					if (!order.entitySpawned) {
						PlayerEntity p = playerManager.getPlayer(UUID.fromString(order.orderUUID));
						World w = p.world;
						w.spawnEntity(new EntityDeliveryChest(w, new Vec3d(p.getX(), p.getY(), p.getZ()), p.getUuid()));
						order.entitySpawned = true;
					}
				}
			}
			
			PacketByteBuf pb = new PacketByteBuf(Unpooled.buffer());
			pb.writeInt(order.items.size());
			for(OrderableItem oi : order.items) {
				pb.writeItemStack(new ItemStack(oi));
			}
			pb.writeInt(order.price);
			pb.writeInt(order.currentStatus.ordinal());
			ServerPlayNetworking.send(playerManager.getPlayer(UUID.fromString(order.orderUUID)), PacketList.S2C_SYNC_ORDER, pb);
		}
	}
}
