package mcvmcomputers;

import mcvmcomputers.client.tablet.TabletOrder;
import mcvmcomputers.entities.EntityList;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.sound.SoundList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.fabricmc.fabric.mixin.resource.loader.MixinKeyedResourceReloadListener.Server;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class MainInitializer implements ModInitializer{
	public static TabletOrder currentOrder;
	
	public void onInitialize() {
		ItemList.init();
		EntityList.init();
		SoundList.init();
		ServerSidePacketRegistry s;
	}

}
