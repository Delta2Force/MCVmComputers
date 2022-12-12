package mcvmcomputers.mixins;

import java.util.List;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.buffer.Unpooled;
import mcvmcomputers.MainMod;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.networking.PacketList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
	@Shadow public abstract List<ServerPlayerEntity> getPlayerList();

	@Inject(at = @At("HEAD"), method = "remove")
	public void remove(ServerPlayerEntity player, CallbackInfo ci) {
		MainMod.orders.remove(player.getUuid());
		EntityPC pc = MainMod.computers.remove(player.getUuid());
		if(pc != null){
			Stream<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking((Entity)pc).stream();
			PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
			b.writeUuid(player.getUuid());
			watchingPlayers.forEach((p) -> {
				ServerPlayNetworking.send(p, PacketList.S2C_STOP_SCREEN, b);
			});
		}
	}
}
