package mcvmcomputers.client.gui.setup.pages;

import mcvmcomputers.client.gui.setup.GuiSetup;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class SetupPage {
	protected final GuiSetup setupGui;
	protected final TextRenderer textRender;
	protected final MinecraftClient minecraft;
	
	public SetupPage(GuiSetup setupGui, TextRenderer textRender) {
		this.setupGui = setupGui;
		this.textRender = textRender;
		this.minecraft = MinecraftClient.getInstance();
	}
	
	public abstract void render(MatrixStack ms, int mouseX, int mouseY, float delta);
	public abstract void init();
}
