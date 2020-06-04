package mcvmcomputers.gui;

import java.nio.DoubleBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import mcvmcomputers.MCVmComputersMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class GuiFocus extends Screen{

	public GuiFocus() {
		super(new TranslatableText("Focus"));
	}
	
	@Override
	protected void init() {
		MCVmComputersMod.releaseKeys = false;
	}
	
	private boolean pressed;
	
	@Override
	public void render(int wmouseX, int wmouseY, float delta) {
		long window = minecraft.getWindow().getHandle();
		DoubleBuffer mX = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer mY = BufferUtils.createDoubleBuffer(1);
		GLFW.glfwGetCursorPos(window, mX, mY);
		double mouseX = mX.get();
		double mouseY = mY.get();
		mX.clear();
		mY.clear();
		MCVmComputersMod.mouseCurX = mouseX;
		MCVmComputersMod.mouseCurY = mouseY;
		MCVmComputersMod.leftMouseButton = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		MCVmComputersMod.middleMouseButton = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
		MCVmComputersMod.rightMouseButton = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		
		this.font.draw("Press Left Ctrl + Right Ctrl + Backspace to lose focus", 4, 4, -1);
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		if(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS && GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS && GLFW.glfwGetKey(window, GLFW.GLFW_KEY_BACKSPACE) == GLFW.GLFW_PRESS) {
			minecraft.openScreen(null);
		}
		
		super.render(wmouseX, wmouseY, delta);
	}
	
	@Override
	public void removed() {
		MCVmComputersMod.releaseKeys = true;
	}
	
	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
