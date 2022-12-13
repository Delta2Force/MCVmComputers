package mcvmcomputers.client.gui;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.glfw.GLFW;
import org.virtualbox_7_0.*;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import io.netty.buffer.Unpooled;
import mcvmcomputers.client.ClientMod;
import mcvmcomputers.entities.EntityPC;
import mcvmcomputers.item.ItemHardDrive;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.utils.MVCUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

public class GuiPCEditing extends Screen{
	private float introScale;
	private float panelX;
	private EntityPC pc_case;
	private boolean openCase;
	private MinecraftClient minecraft;
	
	private final Language lang = Language.getInstance();

	private final Object vmTurningON = new Object();

	public GuiPCEditing(EntityPC pc_case) {
		super(Text.translatable("text.pc_editor.title"));
		this.pc_case = pc_case;
		minecraft = MinecraftClient.getInstance();
	}
	
	public void renderBackgroundAndMobo(MatrixStack ms) {
		this.fillGradient(ms, 0, 0, this.width, this.height, new Color(0f,0f,0f,Math.max(0.5f*introScale,0)).getRGB(), new Color(0f,0f,0f,0.5f*introScale).getRGB());
		ms.push();
	    RenderSystem.enableBlend();
	    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
	    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    ms.translate((this.width / 2), (this.height / 2)-40, 100.0F);
		ms.multiply(new Quaternion(0, -90f * introScale, 0f, true));
		ms.multiply(new Quaternion(0, 0.6f * introScale, 0.6f * introScale, true));
	    ms.scale(1.0F, -1.0F, 1.0F);
	    ms.scale(introScale, introScale, introScale);
	    ms.scale(230.0F, 230.0F, 230.0F);
	    renderItem(ItemList.PC_CASE_NO_PANEL, ms);
	    if(pc_case.getMotherboardInstalled()) {
	    	ms.push();
			ms.multiply(new Quaternion(0f, 0f, -90f, true));
			ms.multiply(new Quaternion(0f, -90f, 0f, true));
		    ms.translate(0.04f, 0.2f, -0.16f);
		    ms.scale(0.55f, 0.55f, 0.55f);
		    renderItem(ItemList.ITEM_MOTHERBOARD, ms);
		    ms.pop();
	    }
	    if(pc_case.getCpuDividedBy() > 0) {
		    ms.push();
			ms.multiply(new Quaternion(0f, 0f, -90f, true));
		    ms.scale(0.55f, 0.55f, 0.55f);
		    ms.translate(0.23f, 0.48f, 0.13f);
	    	renderItem(ItemList.ITEM_CPU2, ms);
	    	ms.pop();
	    }
	    if(pc_case.getGpuInstalled()) {
	    	ms.push();
			ms.multiply(new Quaternion(0f, -90f, 0f, true));
	    	ms.scale(0.55f, 0.55f, 0.55f);
			ms.multiply(new Quaternion(-90f, 0f, 0f, true));
	    	ms.translate(0.33f, 0.5f, -0.565f);
	    	renderItem(ItemList.ITEM_GPU, ms);
	    	ms.pop();
	    }
	    if(pc_case.getGigsOfRamInSlot0() > 0) {
	    	ms.push();
	    	ms.scale(0.55f, 0.55f, 0.55f);
			ms.multiply(new Quaternion(0f, 0f, -90f, true));
	    	ms.translate(0.07f, 0.5f, -0.203f);
	    	renderItem(ItemList.ITEM_RAM64M, ms);
	    	ms.pop();
	    }
	    if(pc_case.getGigsOfRamInSlot1() > 0) {
	    	ms.push();
	    	ms.scale(0.55f, 0.55f, 0.55f);
			ms.multiply(new Quaternion(0f, 0f, -90f, true));
	    	ms.translate(0.07f, 0.5f, -0.33f);
	    	renderItem(ItemList.ITEM_RAM64M, ms);
	    	ms.pop();
	    }
	    if(!pc_case.getHardDriveFileName().isEmpty()) {
	    	ms.push();
		    ms.scale(0.55f, 0.55f, 0.55f);
		    ms.translate(0.1f, -0.3f, -0.5f);
		    renderItem(ItemList.ITEM_HARDDRIVE, ms);
		    ms.pop();
	    }
	    ms.push();
	    ms.translate(0, -panelX, 0);
	    if(pc_case.getGlassSidepanel()) {
	    	renderItem(ItemList.PC_CASE_GLASS_PANEL, ms);
	    }else{
	    	renderItem(ItemList.PC_CASE_ONLY_PANEL, ms);
	    }
		ms.pop();
		ms.pop();
	}

	private void renderItem(Item item, MatrixStack matrices) {
		BakedModel bakedModel = minecraft.getItemRenderer().getModels().getModel(item);
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

		// More barebones implementation of net.minecraft.client.render.item.ItemRenderer.renderItem since we don't need
		// all the layers of abstraction

		matrices.push();
		matrices.translate(-0.5F, -0.5F, -0.5F);
		VertexConsumer vertices = immediate.getBuffer(TexturedRenderLayers.getEntityTranslucentCull());

		MatrixStack.Entry matrixEntry = matrices.peek();

		List<BakedQuad> modelQuads = bakedModel.getQuads(null, null, Random.create());
		modelQuads.forEach(quad -> vertices.quad(matrixEntry, quad, 1, 1, 1, 15728640, OverlayTexture.DEFAULT_UV));

		immediate.draw();
		matrices.pop();

		RenderSystem.enableDepthTest();
	}
	
	
	private void addMotherboard(boolean sixtyFour) {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeBoolean(sixtyFour);
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_ADD_MOBO, b);
	}
	
	private void removeMotherboard() {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_REMOVE_MOBO, b);
	}
	
	private void addCPU(Item cpuItem, int dividedBy) {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(dividedBy);
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_ADD_CPU, b);
	}
	
	private void addGPU() {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_ADD_GPU, b);
	}
	
	private void addHardDrive(String fileName) {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeString(fileName);
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_ADD_HARD_DRIVE, b);
	}
	
	private void removeHardDrive() {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_REMOVE_HARD_DRIVE, b);
	}
	
	private void addRamStick(Item ramItem, int megs) {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(megs);
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_ADD_RAM, b);
	}
	
	private void removeRamStick(int slot) {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(slot);
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_REMOVE_RAM, b);
	}
	private void removeCPU() {
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_REMOVE_CPU, b);
	}
	
	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		if(introScale > 0.92f && openCase)
			panelX = MVCUtils.lerp(panelX, 8, delta/20f);
		if(!openCase) {
			panelX = MVCUtils.lerp(panelX, 0, delta/1.5f);
		}
		introScale = MVCUtils.lerp(introScale, 1f, delta/4f);
		this.renderBackgroundAndMobo(ms);
		this.clearChildren();
		if(introScale > 0.99f) {
			if(openCase) {
				if((ClientMod.vmTurningOn || ClientMod.vmTurnedOn) && ClientMod.vmEntityID == pc_case.getId()) {
					openCase = false;
				}
				if(SystemUtils.IS_OS_MAC) {
					this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.put_panel_back_mac"), 4, 14, -1);
					if(GLFW.glfwGetKey(minecraft.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS) {
						openCase = false;
						panelX = -panelX;
					}
				}else {
					this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.put_panel_back"), 4, 14, -1);
					if(GLFW.glfwGetKey(minecraft.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS) {
						openCase = false;
						panelX = -panelX;
					}
				}
				if(pc_case.getMotherboardInstalled()) {
					this.addDrawableChild(new ButtonWidget(this.width/2-70, this.height / 2 - 70, 10, 10, Text.of("x"), (btn) -> this.removeMotherboard()));
					RenderSystem.disableDepthTest();
					ms.push();
					ms.translate(0, 0, 200);
					if(pc_case.get64Bit()) {
						this.textRenderer.draw(ms, lang.get("mcvmcomputers.64bit"), this.width/2 - 66, this.height/2 - 56, -1);
					}else {
						this.textRenderer.draw(ms, lang.get("mcvmcomputers.32bit"), this.width/2 - 66, this.height/2 - 56, -1);
					}
					ms.pop();
					RenderSystem.enableDepthTest();
					if(pc_case.getCpuDividedBy() == 0) {
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.add_cpu"), this.width/2 - 120, this.height/2 - 40, -1);
						ms.pop();
						RenderSystem.enableDepthTest();
						int addCpuWidth = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.add_cpu_btn").replace("%s", "6"));
						ButtonWidget div2 = new ButtonWidget(this.width/2 - (addCpuWidth+59), this.height / 2 - 31, addCpuWidth+4, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_cpu_btn").replace("%s", "2")), (btn) -> this.addCPU(ItemList.ITEM_CPU2, 2));
						ButtonWidget div4 = new ButtonWidget(this.width/2 - (addCpuWidth+59), this.height / 2 - 18, addCpuWidth+4, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_cpu_btn").replace("%s", "4")), (btn) -> this.addCPU(ItemList.ITEM_CPU4, 4));
						ButtonWidget div6 = new ButtonWidget(this.width/2 - (addCpuWidth+59), this.height / 2 - 5, addCpuWidth+4, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_cpu_btn").replace("%s", "6")), (btn) -> this.addCPU(ItemList.ITEM_CPU6, 6));
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_CPU2))) {
							div2.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_CPU4))) {
							div4.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_CPU6))) {
							div6.active = false;
						}
						this.addDrawableChild(div2);
						this.addDrawableChild(div4);
						this.addDrawableChild(div6);
					}else {
						this.addDrawableChild(new ButtonWidget(this.width/2-43, this.height / 2 - 16, 10, 10, Text.of("x"), (btn) -> this.removeCPU()));
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						this.textRenderer.draw(ms, "1/" + pc_case.getCpuDividedBy(), this.width/2-25, this.height/2+2, -1);
						ms.pop();
						RenderSystem.enableDepthTest();
					}
					if(!pc_case.getGpuInstalled()) {
						int addGpuWidth = this.textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.add_gpu"));
						ButtonWidget bw = new ButtonWidget(this.width/2 - 64, this.height / 2 + 33, addGpuWidth+4, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_gpu")), (btn) -> this.addGPU());
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_GPU)))
							bw.active = false;
						this.addDrawableChild(bw);
					}
					if(pc_case.getHardDriveFileName().isEmpty()) {
						int lastYOffset = 0;
						int xOffCount = 0;
						int lastXOffset = 0;
						int count = 0;
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.add_vhds"), this.width/2 + 20, this.height/2 + 30, -1);
						ms.pop();
						RenderSystem.enableDepthTest();
						for(ItemStack is : minecraft.player.getInventory().main) {
							if(is.getItem() instanceof ItemHardDrive) {
								if(is.getNbt() != null){
									if(is.getNbt().contains("vhdfile")) {
		    							String file = is.getNbt().getString("vhdfile");
		    							if(new File(ClientMod.vhdDirectory, file).exists()) {
		    								int w = Math.max(50, this.textRenderer.getWidth(file)+4);
		    								this.addDrawableChild(new ButtonWidget(this.width/2 + 20 + lastXOffset, this.height / 2 + 40 + lastYOffset, Math.max(50, this.textRenderer.getWidth(file)+4), 12, Text.of(file), (btn) -> this.addHardDrive(file)));
		    								lastXOffset += w+1;
											xOffCount += 1;
											if(xOffCount >= 3) {
												xOffCount = 0;
												lastXOffset = 0;
												lastYOffset += 13;
											}
											count++;
		    							}
									}
								}
							}
						}
						if(count == 0) {
							RenderSystem.disableDepthTest();
							ms.push();
							ms.translate(0, 0, 200);
							this.textRenderer.draw(ms, (char) (0xfeff00a7) + "7" + lang.get("mcvmcomputers.pc_editing.no_valid_vhd"), this.width/2 + 20, this.height/2 + 40, -1);
							ms.pop();
							RenderSystem.enableDepthTest();
						}
					}else {
						this.addDrawableChild(new ButtonWidget(this.width/2+30, this.height / 2 + 55, 10, 10, Text.of("x"), (btn) -> this.removeHardDrive()));
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						this.textRenderer.draw(ms, pc_case.getHardDriveFileName(), this.width/2+45, this.height/2+65, -1);
						ms.pop();
						RenderSystem.enableDepthTest();
					}
					if(pc_case.getGigsOfRamInSlot0() == 0 || pc_case.getGigsOfRamInSlot1() == 0) {
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.add_ram"), this.width/2 + 50, this.height/2 - 60, -1);
						ms.pop();
						RenderSystem.enableDepthTest();
						int addMBRamWidth = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.add_mbram_btn").replace("%s", "512"))+4;
						ButtonWidget sixfourM = new ButtonWidget(this.width/2 + 50, this.height / 2 - 64, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_mbram_btn").replace("%s", "64")), (btn) -> this.addRamStick(ItemList.ITEM_RAM64M, 64));
						ButtonWidget oneM = new ButtonWidget(this.width/2 + 50, this.height / 2 - 51, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_mbram_btn").replace("%s", "128")), (btn) -> this.addRamStick(ItemList.ITEM_RAM128M, 128));
						ButtonWidget twoM = new ButtonWidget(this.width/2 + 50, this.height / 2 - 38, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_mbram_btn").replace("%s", "256")), (btn) -> this.addRamStick(ItemList.ITEM_RAM256M, 256));
						ButtonWidget fiveM = new ButtonWidget(this.width/2 + 50, this.height / 2 - 25, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_mbram_btn").replace("%s", "512")), (btn) -> this.addRamStick(ItemList.ITEM_RAM512M, 512));
						ButtonWidget oneG = new ButtonWidget(this.width/2 + 50, this.height / 2 - 12, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_ram_btn").replace("%s", "1")), (btn) -> this.addRamStick(ItemList.ITEM_RAM1G, 1024));
						ButtonWidget twoG = new ButtonWidget(this.width/2 + 50, this.height / 2 + 1, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_ram_btn").replace("%s", "2")), (btn) -> this.addRamStick(ItemList.ITEM_RAM2G, 2048));
						ButtonWidget fourG = new ButtonWidget(this.width/2 + 50, this.height / 2 + 14, addMBRamWidth, 12, Text.of(lang.get("mcvmcomputers.pc_editing.add_ram_btn").replace("%s", "4")), (btn) -> this.addRamStick(ItemList.ITEM_RAM4G, 4096));
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM64M))) {
							sixfourM.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM128M))) {
							oneM.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM256M))) {
							twoM.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM512M))) {
							fiveM.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM1G))) {
							oneG.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM2G))) {
							twoG.active = false;
						}
						if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_RAM4G))) {
							fourG.active = false;
						}
						this.addDrawableChild(sixfourM);
						this.addDrawableChild(oneM);
						this.addDrawableChild(twoM);
						this.addDrawableChild(fiveM);
						this.addDrawableChild(oneG);
						this.addDrawableChild(twoG);
						this.addDrawableChild(fourG);
					}
					if(pc_case.getGigsOfRamInSlot0() > 0) {
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						if(pc_case.getGigsOfRamInSlot0() < 1000 && pc_case.getGigsOfRamInSlot0() >= 100) {
							this.textRenderer.draw(ms, (int) pc_case.getGigsOfRamInSlot0() + " MB", this.width/2+4, this.height/2+2, -1);
						}else if(pc_case.getGigsOfRamInSlot0() < 100) {
							this.textRenderer.draw(ms, (int) pc_case.getGigsOfRamInSlot0() + " MB", this.width/2+10, this.height/2+2, -1);
						}else {
							this.textRenderer.draw(ms, (int) pc_case.getGigsOfRamInSlot0()/1024 + " GB", this.width / 2 + 16, this.height / 2 + 2, -1);
						}
						ms.pop();
						RenderSystem.enableDepthTest();
						this.addDrawableChild(new ButtonWidget(this.width/2+21, this.height / 2 - 70, 10, 10, Text.of("x"), (btn) -> this.removeRamStick(0)));
					}
					if(pc_case.getGigsOfRamInSlot1() > 0) {
						RenderSystem.disableDepthTest();
						ms.push();
						ms.translate(0, 0, 200);
						if(pc_case.getGigsOfRamInSlot1() < 1000) {
							this.textRenderer.draw(ms, (int) pc_case.getGigsOfRamInSlot1() + " MB", this.width/2+42, this.height/2+2, -1);
						}else {
							this.textRenderer.draw(ms, (int) pc_case.getGigsOfRamInSlot1()/1024 + " GB", this.width / 2+42, this.height / 2+2, -1);
						}
						ms.pop();
						RenderSystem.enableDepthTest();
						this.addDrawableChild(new ButtonWidget(this.width/2 + 37, this.height / 2 - 70, 10, 10, Text.of("x"), (btn) -> this.removeRamStick(1)));
					}
				}else {
					int thirtytwow = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.add_32bit_mobo"))+4;
					ButtonWidget thirtytwo = new ButtonWidget(this.width/2 - (thirtytwow/2), this.height / 2 - 23, thirtytwow, 14, Text.translatable("mcvmcomputers.pc_editing.add_32bit_mobo"), (btn) -> this.addMotherboard(false));
					int sixtyfourw = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.add_64bit_mobo"))+4;
					ButtonWidget sixtyfour = new ButtonWidget(this.width/2 - (sixtyfourw/2), this.height / 2 - 7, sixtyfourw, 14, Text.translatable("mcvmcomputers.pc_editing.add_64bit_mobo"), (btn) -> this.addMotherboard(true));
					
					if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_MOTHERBOARD))) {
						thirtytwo.active = false;
					}
					if(!minecraft.player.getInventory().contains(new ItemStack(ItemList.ITEM_MOTHERBOARD64))) {
						sixtyfour.active = false;
					}
					
					this.addDrawableChild(thirtytwo);
					this.addDrawableChild(sixtyfour);
				}
			}else {
				boolean turnedOn = (ClientMod.vmTurningOn && ClientMod.vmEntityID == pc_case.getId()) || (ClientMod.vmTurnedOn && ClientMod.vmEntityID == pc_case.getId());
				if(turnedOn) {
					int buttonW = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.turn_off"))+4;
					this.addDrawableChild(new ButtonWidget((this.width/2 + 103) - buttonW, this.height / 2 - 80, buttonW, 12, Text.translatable("mcvmcomputers.pc_editing.turn_off"), (btn) -> this.turnOffPC(btn)));
				}else {
					int buttonW = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.turn_on"))+4;
					this.addDrawableChild(new ButtonWidget((this.width/2 + 103) - buttonW, this.height / 2 - 80, buttonW, 12, Text.translatable("mcvmcomputers.pc_editing.turn_on"), (btn) -> this.turnOnPC(btn)));
				}
				
				if(ClientMod.vmSession != null) {
					boolean ejected = false;
					
					try {
						ejected = ClientMod.vmSession.getMachine().getMediumAttachment("IDE Controller", 1, 0).getIsEjected();
					}catch(VBoxException e) {}
					
					if(ejected && !pc_case.getIsoFileName().isEmpty()) {
						this.removeISO();
					}
				}
				
				if(pc_case.getIsoFileName().isEmpty()) {
					RenderSystem.disableDepthTest();
					ms.push();
					ms.translate(0, 0, 200);
					this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.select_iso"), this.width/2 - 75, this.height/2 - 75, -1);
					ms.pop();
					RenderSystem.enableDepthTest();
					int offX = 0;
					int offY = 0;
					for(File f : ClientMod.isoDirectory.listFiles()) {
						if(f.getName().endsWith(".iso") || f.getName().endsWith(".ISO")) {
							if((this.width/2 - 75 + offX) + this.textRenderer.getWidth(f.getName())+10 > this.width/2 + 105) {
								offX = 0;
								offY += 14;
							}
							this.addDrawableChild(new ButtonWidget(this.width/2 - 75 + offX, this.height / 2 - 62 + offY, this.textRenderer.getWidth(f.getName())+8, 12, Text.of(f.getName()), (btn) -> insertISO(f.getName())));
							offX += this.textRenderer.getWidth(f.getName())+10;
						}
					}
				}else {
					RenderSystem.disableDepthTest();
					ms.push();
					ms.translate(0, 0, 200);
					this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.inserted_iso"), this.width/2 - 75, this.height/2 - 75, -1);
					this.textRenderer.draw(ms, (char) (0xfeff00a7) + "7" + pc_case.getIsoFileName(), this.width/2 - 75, this.height/2 - 65, -1);
					ms.pop();
					RenderSystem.enableDepthTest();
					int ejectW = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.eject"));
					this.addDrawableChild(new ButtonWidget(this.width/2 - 75, this.height / 2 - 50, ejectW+4, 12, Text.of(lang.get("mcvmcomputers.pc_editing.eject")), (btn) -> removeISO()));
				}
				int openCaseW = textRenderer.getWidth(lang.get("mcvmcomputers.pc_editing.open_case"));
				ButtonWidget bw = new ButtonWidget(this.width/2 - 82, this.height / 2 + 65, openCaseW+4, 12, Text.of(lang.get("mcvmcomputers.pc_editing.open_case")), (btn) -> openCase = true);
				bw.active = !turnedOn;
				this.addDrawableChild(bw);
			}
		}
		RenderSystem.disableDepthTest();
		ms.translate(0, 0, 200);
		super.render(ms, mouseX, mouseY, delta);
		this.textRenderer.draw(ms, lang.get("mcvmcomputers.pc_editing.close"), 4, 4, -1);
		RenderSystem.enableDepthTest();
	}
	
	private void removeISO() {
		if((ClientMod.vmTurningOn || ClientMod.vmTurnedOn) && ClientMod.vmEntityID == pc_case.getId()) {
			try {
				ClientMod.vmSession.getMachine().unmountMedium("IDE Controller", 1, 0, true);
			}catch(VBoxException ex) {}
		}
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_REMOVE_ISO, b);
	}
	
	private void insertISO(String name) {
		if(ClientMod.vmTurnedOn && ClientMod.vmEntityID == pc_case.getId()) {
			IMedium m = ClientMod.vb.openMedium(new File(ClientMod.isoDirectory, name).getPath(), DeviceType.DVD, AccessMode.ReadOnly, true);
			ClientMod.vmSession.getMachine().mountMedium("IDE Controller", 1, 0, m, true);
		}
		if(ClientMod.vmTurningOn && ClientMod.vmEntityID == pc_case.getId()) {
			minecraft.player.sendMessage(Text.translatable("mcvmcomputers.waitingforvmtostart").formatted(Formatting.YELLOW), false);
			synchronized (vmTurningON) {
				try {
					vmTurningON.wait();
				} catch (InterruptedException e) {}
				IMedium m = ClientMod.vb.openMedium(new File(ClientMod.isoDirectory, name).getPath(), DeviceType.DVD, AccessMode.ReadOnly, true);
				ClientMod.vmSession.getMachine().mountMedium("IDE Controller", 1, 0, m, true);
			}
		}
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		b.writeString(name);
		b.writeInt(this.pc_case.getId());
		ClientPlayNetworking.send(PacketList.C2S_ADD_ISO, b);
	}
	
	public void turnOffPC(ButtonWidget wdgt) {
		ClientMod.vmTurningOff = true;
		ClientMod.vmTurnedOn = false;
		PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
		ClientPlayNetworking.send(PacketList.C2S_TURN_OFF_PC, b);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(ClientMod.vmTurningOn) {}
				IProgress ip = ClientMod.vmSession.getConsole().powerDown();
				ip.waitForCompletion(-1);

				try {
					ClientMod.vmSession.unlockMachine();
				}catch (VBoxException e) {}

				ClientMod.vmSession = null;
				ClientMod.vmTurnedOn = false;
				ClientMod.vmTurningOff = false;
				ClientMod.vmEntityID = -1;
			}
		}, "Turn off PC").start();
	}
	
	public void turnOnPC(ButtonWidget wdgt) {
		if(pc_case.getCpuDividedBy() > 0 && pc_case.getGpuInstalled() && pc_case.getMotherboardInstalled() && (pc_case.getGigsOfRamInSlot0() + pc_case.getGigsOfRamInSlot1()) >= 1) {
			if(!pc_case.getHardDriveFileName().isEmpty()) {
				if(!new File(ClientMod.vhdDirectory, pc_case.getHardDriveFileName()).exists()) {
					minecraft.player.sendMessage(Text.translatable("mcvmcomputers.hdd_doesnt_exist").formatted(Formatting.RED), false);
					return;
				}
			}
			if(!pc_case.getIsoFileName().isEmpty()) {
				if(!new File(ClientMod.isoDirectory, pc_case.getIsoFileName()).exists()) {
					minecraft.player.sendMessage(Text.translatable("mcvmcomputers.iso_doesnt_exist").formatted(Formatting.RED), false);
					return;
				}
			}
			
			if(ClientMod.vmTurningOn || ClientMod.vmTurnedOn) {
				return;
			}
			ClientMod.vmTurningOn = true;
			ClientMod.vmEntityID = pc_case.getId();
			PacketByteBuf b = new PacketByteBuf(Unpooled.buffer());
			b.writeInt(pc_case.getId());
			ClientPlayNetworking.send(PacketList.C2S_TURN_ON_PC, b);
			new Thread(() -> {
				ArrayList<ISession> usedSessions = new ArrayList<>();
				try {
					IMachine found = null;
					try {
						found = ClientMod.vb.findMachine("VmComputersVm");
					}catch(VBoxException e) {}

					if(found != null) {
						if(found.getState() == MachineState.Running) {
							ISession sess = ClientMod.vbManager.getSessionObject();
							found.lockMachine(sess, LockType.Shared);
							IProgress ip = sess.getConsole().powerDown();
							ip.waitForCompletion(-1);
							sess.unlockMachine();
						}
						ISession sess = ClientMod.vbManager.getSessionObject();
						found.lockMachine(sess, LockType.Write);
						usedSessions.add(sess);
						IMachine edit = sess.getMachine();
						String OSType = edit.getOSTypeId();
						if(pc_case.get64Bit()) {
							if(!OSType.endsWith("_64"))
								OSType += "_64";
						}else {
							OSType = OSType.replace("_64","");
						}
						edit.setOSTypeId(OSType);
						edit.setMemorySize((long) Math.min(ClientMod.maxRam, (pc_case.getGigsOfRamInSlot0() + pc_case.getGigsOfRamInSlot1())));
						edit.setCPUCount(Math.max(1, ClientMod.vb.getHost().getProcessorCount() / pc_case.getCpuDividedBy()));
						edit.getGraphicsAdapter().setAccelerate2DVideoEnabled(true);
						edit.getGraphicsAdapter().setAccelerate3DEnabled(true);
						edit.getGraphicsAdapter().setVRAMSize((long)ClientMod.videoMem);
						try{
							edit.removeStorageController("IDE Controller");
						}catch (VBoxException ex){}
						edit.addStorageController("IDE Controller", StorageBus.IDE);
						if(!pc_case.getHardDriveFileName().isEmpty()) {
							if(new File(ClientMod.vhdDirectory, pc_case.getHardDriveFileName()).exists()) {
								IMedium medium = ClientMod.vb.openMedium(new File(ClientMod.vhdDirectory, pc_case.getHardDriveFileName()).getPath(), DeviceType.HardDisk, AccessMode.ReadWrite, true);
								edit.attachDevice("IDE Controller", 0, 0, DeviceType.HardDisk, medium);
							}
						}
						if(!pc_case.getIsoFileName().isEmpty()) {
							if(new File(ClientMod.isoDirectory, pc_case.getIsoFileName()).exists()) {
								IMedium cd = ClientMod.vb.openMedium(new File(ClientMod.isoDirectory, pc_case.getIsoFileName()).getPath(), DeviceType.DVD, AccessMode.ReadOnly, true);
								try {
									edit.attachDevice("IDE Controller", 1, 0, DeviceType.DVD, cd);
								}catch(VBoxException ex) {}
							}else if(pc_case.getIsoFileName().equals("Additions")) {
								IMedium cd = ClientMod.vb.openMedium(new File(ClientMod.vb.getSystemProperties().getDefaultAdditionsISO()).getPath(), DeviceType.DVD, AccessMode.ReadOnly, true);
								try {
									edit.attachDevice("IDE Controller", 1, 0, DeviceType.DVD, cd);
								}catch(VBoxException ex) {}
							}
						}
						if(pc_case.getIsoFileName().isEmpty()) {
							edit.attachDevice("IDE Controller",1,0,DeviceType.DVD,null);
						}
						edit.saveSettings();
						sess.unlockMachine();
						usedSessions.remove(sess);
					}else {
						String OSType = "Other";
						if(pc_case.get64Bit()) {
							OSType += "_64";
						}
						IMachine machine = ClientMod.vb.createMachine("", "VmComputersVm", null, OSType, "", "", "", "");
						ClientMod.vb.registerMachine(machine);
						ISession sess = ClientMod.vbManager.getSessionObject();
						machine.lockMachine(sess, LockType.Write);
						usedSessions.add(sess);
						IMachine edit = sess.getMachine();
						edit.setMemorySize((long) Math.min(ClientMod.maxRam, (pc_case.getGigsOfRamInSlot0() + pc_case.getGigsOfRamInSlot1())));
						edit.setCPUCount(Math.min(1, ClientMod.vb.getHost().getProcessorCount() / pc_case.getCpuDividedBy()));
						edit.getGraphicsAdapter().setAccelerate2DVideoEnabled(true);
						edit.getGraphicsAdapter().setAccelerate3DEnabled(true);
						edit.getGraphicsAdapter().setVRAMSize((long)ClientMod.videoMem);
						edit.addStorageController("IDE Controller", StorageBus.IDE);
						if(!pc_case.getHardDriveFileName().isEmpty()) {
							if(new File(ClientMod.vhdDirectory, pc_case.getHardDriveFileName()).exists()) {
								IMedium medium = ClientMod.vb.openMedium(new File(ClientMod.vhdDirectory, pc_case.getHardDriveFileName()).getPath(), DeviceType.HardDisk, AccessMode.ReadWrite, true);
								edit.attachDevice("IDE Controller", 0, 0, DeviceType.HardDisk, medium);
							}
						}
						if(!pc_case.getIsoFileName().isEmpty()) {
							if(new File(ClientMod.isoDirectory, pc_case.getIsoFileName()).exists()) {
								IMedium cd = ClientMod.vb.openMedium(new File(ClientMod.isoDirectory, pc_case.getIsoFileName()).getPath(), DeviceType.DVD, AccessMode.ReadOnly, true);
								try {
									edit.attachDevice("IDE Controller", 1, 0, DeviceType.DVD, cd);
								}catch(VBoxException ex) {}
							}else if(pc_case.getIsoFileName().equals("Additions")) {
								IMedium cd = ClientMod.vb.openMedium(new File(ClientMod.vb.getSystemProperties().getDefaultAdditionsISO()).getPath(), DeviceType.DVD, AccessMode.ReadOnly, true);
								try {
									edit.attachDevice("IDE Controller", 1, 0, DeviceType.DVD, cd);
								}catch(VBoxException ex) {}
							}
						}
						if(pc_case.getIsoFileName().isEmpty()) {
							edit.attachDevice("IDE Controller",1,0,DeviceType.DVD,null);
						}
						edit.saveSettings();
						sess.unlockMachine();
						usedSessions.remove(sess);
					}

					IMachine machine = ClientMod.vb.findMachine("VmComputersVm");
					ClientMod.vmSession = ClientMod.vbManager.getSessionObject();
					IProgress pr = machine.launchVMProcess(ClientMod.vmSession, "headless", Arrays.asList());
					pr.waitForCompletion(-1);
					ClientMod.vmTurningOn = false;
					ClientMod.vmTurnedOn = true;
					synchronized (vmTurningON) {
						vmTurningON.notify();
					}
				}catch(Exception ex) {
					for(ISession is : usedSessions) {
						try {
							is.unlockMachine();
						}catch(Exception exx) {}
					}
					minecraft.player.sendMessage(Text.translatable("mcvmcomputers.failed_to_start", ex.getMessage()).formatted(Formatting.RED), false);
					minecraft.player.sendMessage(Text.translatable("mcvmcomputers.contact_me").formatted(Formatting.RED), false);
					ClientMod.vmTurningOn = false;
					ClientMod.vmTurnedOn = false;

					PacketByteBuf b1 = new PacketByteBuf(Unpooled.buffer());
					ClientPlayNetworking.send(PacketList.C2S_TURN_OFF_PC, b1);
				}
			}, "Turn on PC").start();
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
