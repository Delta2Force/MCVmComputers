package mcvmcomputers.client.gui;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.TimerTask;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import mcvmcomputers.client.ClientMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;

public class GuiFocus extends Screen{
	private String keyString;
	private ArrayList<Integer> keys;
	private final Language lang = Language.getInstance();
	
	public GuiFocus() {
		super(new TranslatableText("Focus"));
	}
	
	@Override
	protected void init() {
		ClientMod.releaseKeys = false;
		
		keyString = "";
		
		int[] unfocusKeys = new int[] {ClientMod.glfwUnfocusKey1,
									   ClientMod.glfwUnfocusKey2,
									   ClientMod.glfwUnfocusKey3,
									   ClientMod.glfwUnfocusKey4};

		keys = new ArrayList<>();
		for(int key : unfocusKeys) {
			if(key > 0) {
				keys.add(key);
			}
		}
		
		boolean plus = false;
		for(int key : keys) {
			if(plus) {
				keyString += " + ";
			}
			keyString += ClientMod.getKeyName(key);
			plus = true;
		}
	}
	
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
		ClientMod.mouseCurX = mouseX;
		ClientMod.mouseCurY = mouseY;
		ClientMod.leftMouseButton = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
		ClientMod.middleMouseButton = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
		ClientMod.rightMouseButton = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
		
		this.font.draw(lang.translate("mcvmcomputers.focus.lose").replace("%s", keyString), 4, 4, -1);
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

		int releaseButtonsPressed = 0;
		for(int key : keys) {
			if(GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS) {
				releaseButtonsPressed++;
			}
		}

		if(releaseButtonsPressed == keys.size()) {
			minecraft.openScreen(null);
			ClientMod.unfocusTimeoutTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					ClientMod.isAllowedToRecordKeys = false;
				}
			},500);
		}
		
		super.render(wmouseX, wmouseY, delta);
	}
	
	@Override
	public void removed() {
		ClientMod.releaseKeys = true;
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
