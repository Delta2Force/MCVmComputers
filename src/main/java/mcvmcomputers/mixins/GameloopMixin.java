package mcvmcomputers.mixins;

import static mcvmcomputers.client.ClientMod.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ConcurrentModificationException;
import java.util.List;

import mcvmcomputers.client.ClientMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcvmcomputers.client.gui.setup.GuiSetup;
import mcvmcomputers.client.tablet.TabletOS;
import mcvmcomputers.client.utils.VMRunnable;
import mcvmcomputers.entities.EntityItemPreview;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.item.ItemOrderingTablet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import vbhook.VBHook;

@Mixin(MinecraftClient.class)
public class GameloopMixin {
	@Shadow
	public ClientPlayerEntity player;
	
	@Shadow
	public HitResult crosshairTarget;
	
	@Shadow
	public ClientWorld world;
	
	@Shadow
	public Screen currentScreen;
	
	@Shadow
	private boolean paused;
	
	@Shadow
	private float pausedTickDelta;
	
	@Shadow
	private RenderTickCounter renderTickCounter;
	
	@Inject(at = @At("HEAD"), method = "run")
	private void run(CallbackInfo info) {
		MinecraftClient mcc = MinecraftClient.getInstance();
		mcc.setScreen(new GuiSetup());
		vhdDirectory = new File(mcc.runDirectory, "vm_computers/vhds");
		vhdDirectory.mkdirs();
		isoDirectory = new File(mcc.runDirectory, "vm_computers/isos");
		isoDirectory.mkdirs();
		
		File num = new File(vhdDirectory.getParentFile(), "vhdnum");
		if(num.exists()) {
			try {
				List<String> lines = Files.readAllLines(num.toPath());
				latestVHDNum = Integer.parseInt(lines.get(0));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Inject(at = @At("HEAD"), method = "render")
	private void render(CallbackInfo info) {
		if(lastDeltaTimeTime == 0) {
			lastDeltaTimeTime = System.currentTimeMillis();
		}else {
			long now = System.currentTimeMillis();
			long diff = now - lastDeltaTimeTime;
			lastDeltaTimeTime = now;
			
			deltaTime = (float) diff / 1000f;
		}
		
		if(tabletOS != null) {
			tabletOS.generateTexture();
		}
		
		if(vmShouldBeOn) {
			if(player == null) {
				vmUpdateThread.interrupt();

				synchronized (ClientMod.vbHookLock) {
					vmShouldBeOn = false;
				}
			}else {
				if(vmUpdateThread == null) {
					vmUpdateThread = new Thread(new VMRunnable(), "VM Update Thread");
					vmUpdateThread.start();
				}
				
				generatePCScreen();
			}
		}
		
		if(player != null) {
			if(player.getActiveItem() != null) {
				boolean tabletOut = false;
				for(ItemStack is : player.getItemsHand()) {
					if(is.getItem() != null) {
						if(is.getItem() instanceof ItemOrderingTablet) {
							tabletOut = true;
							break;
						}
					}
				}
				
				if(tabletOut != tabletOS.tabletOn) {
					tabletOS.tabletOn = tabletOut;
					
					if(tabletOut) {
						tabletOS.tabletTakenOut();
					}else {
						tabletOS.tabletUnequipped();
					}
				}
				
				for(ItemStack is : player.getItemsHand()) {
					if(is.getItem() != null) {
						if(ItemList.PLACABLE_ITEMS.contains(is.getItem())) {
							if(thePreviewEntity != null) {
								thePreviewEntity.setItem(is);
								if(crosshairTarget != null) { 
									Vec3d hit = crosshairTarget.getPos();
									thePreviewEntity.updatePosition(hit.x, hit.y, hit.z);
								}else {
									break;
								}
							}else {
								if(crosshairTarget != null) {
									Vec3d hit = crosshairTarget.getPos();
									thePreviewEntity = new EntityItemPreview(world, hit.x, hit.y, hit.z, is);
									this.world.addEntity(Integer.MAX_VALUE-20,thePreviewEntity);
								}
							}
						}else {
							break;
						}
					}else {
						break;
					}
					return;
				}
				if(thePreviewEntity != null) {
					thePreviewEntity.kill();
					thePreviewEntity = null;
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "cleanUpAfterCrash")
	private void cleanUpAfterCrash(CallbackInfo info) {
		synchronized (ClientMod.vbHookLock) {
			vmShouldBeOn = false;
		}
		while(vmIsOn);
	}

	@Inject(at = @At("HEAD"), method = "close")
	private void close(CallbackInfo info) {
		System.out.println("Stopping VM Computers Mod...");
		synchronized (vbHookLock) {
			vmShouldBeOn = false;
		}
		while(vmIsOn);

		for(NativeImageBackedTexture nibt : vmScreenTextureNIBT.values()) {
			nibt.close();
		}
		for(NativeImage ni : vmScreenTextureNI.values()) {
			ni.close();
		}
		for(Identifier i : vmScreenTextures.values()) {
			MinecraftClient.getInstance().getTextureManager().destroyTexture(i);
		}
		if(tabletThread != null) {
			tabletThread.interrupt();
		}

		synchronized (ClientMod.vbHookLock) {
			if (vmSession != 0L && vbMachine != 0L) {
				if (VB_HOOK.vm_powered_on(vbMachine)) {
					VB_HOOK.stop_vm(ClientMod.vmSession);
				}
			}
			VB_HOOK.free_vm(vbMachine);
			VB_HOOK.free_vb(vb);
			VB_HOOK.free_session(vmSession);
			VB_HOOK.free_vb_client(vbClient);
			VB_HOOK.terminate_glue();
		}
		System.out.println("Stopped VM Computers Mod.");
	}
}
