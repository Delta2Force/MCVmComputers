package mcvmcomputers.client.gui.setup.pages;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static mcvmcomputers.client.ClientMod.*;

import mcvmcomputers.client.gui.setup.GuiSetup;

public class SetupPageUnfocusBinding extends SetupPage{
	public static boolean changeBinding;
	public static int bindingToBeChangedNum;
	public static boolean bindingJustChanged;
	
	public SetupPageUnfocusBinding(GuiSetup setupGui, TextRenderer textRender) {
		super(setupGui, textRender);
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float delta) {
		if(bindingJustChanged) {
			setupGui.init();
			bindingJustChanged = false;
		}
		String s = setupGui.translation("mcvmcomputers.setup.unfocusCombo");
		this.textRender.draw(ms, s, setupGui.width/2-this.textRender.getWidth(s)/2, setupGui.height/2-20, -1);
	}
	
	private void changeBinding(int num) {
		changeBinding = true;
		bindingToBeChangedNum = num;
		bindingJustChanged = false;
		setupGui.init();
	}
	
	private void clearBinding(int num) {
		switch(num) {
		case 1:
			glfwUnfocusKey1 = -1;
			break;
		case 2:
			glfwUnfocusKey2 = -1;
			break;
		case 3:
			glfwUnfocusKey3 = -1;
			break;
		case 4:
			glfwUnfocusKey4 = -1;
			break;
		}
		setupGui.init();
	}

	@Override
	public void init() {
		if(changeBinding) {
			setupGui.addButton(new ButtonWidget(setupGui.width/2-130, setupGui.height/2-10, 60, 20, Text.translatable((bindingToBeChangedNum == 1 ? "> " : "") + getKeyName(glfwUnfocusKey1) + (bindingToBeChangedNum == 1 ? " <" : "")), (bw) -> changeBinding(1)));
			setupGui.addButton(new ButtonWidget(setupGui.width/2-64, setupGui.height/2-10, 60, 20, Text.translatable((bindingToBeChangedNum == 2 ? "> " : "") + getKeyName(glfwUnfocusKey2) + (bindingToBeChangedNum == 2 ? " <" : "")), (bw) -> changeBinding(2)));
			setupGui.addButton(new ButtonWidget(setupGui.width/2+3, setupGui.height/2-10, 60, 20, Text.translatable((bindingToBeChangedNum == 3 ? "> " : "") + getKeyName(glfwUnfocusKey3) + (bindingToBeChangedNum == 3 ? " <" : "")), (bw) -> changeBinding(3)));
			setupGui.addButton(new ButtonWidget(setupGui.width/2+70, setupGui.height/2-10, 60, 20, Text.translatable((bindingToBeChangedNum == 4 ? "> " : "") + getKeyName(glfwUnfocusKey4) + (bindingToBeChangedNum == 4 ? " <" : "")), (bw) -> changeBinding(4)));
		}else {
			setupGui.addButton(new ButtonWidget(setupGui.width/2-130, setupGui.height/2-10, 60, 20, Text.translatable(getKeyName(glfwUnfocusKey1)), (bw) -> changeBinding(1)));
			setupGui.addButton(new ButtonWidget(setupGui.width/2-64, setupGui.height/2-10, 60, 20, Text.translatable(getKeyName(glfwUnfocusKey2)), (bw) -> changeBinding(2)));
			setupGui.addButton(new ButtonWidget(setupGui.width/2+3, setupGui.height/2-10, 60, 20, Text.translatable(getKeyName(glfwUnfocusKey3)), (bw) -> changeBinding(3)));
			setupGui.addButton(new ButtonWidget(setupGui.width/2+70, setupGui.height/2-10, 60, 20, Text.translatable(getKeyName(glfwUnfocusKey4)), (bw) -> changeBinding(4)));
		}
		
		setupGui.addButton(new ButtonWidget(setupGui.width/2-130, setupGui.height/2+12, 60, 12, Text.translatable(setupGui.translation("mcvmcomputers.setup.clearButton")), (bw) -> clearBinding(1)));
		setupGui.addButton(new ButtonWidget(setupGui.width/2-64, setupGui.height/2+12, 60, 12, Text.translatable(setupGui.translation("mcvmcomputers.setup.clearButton")), (bw) -> clearBinding(2)));
		setupGui.addButton(new ButtonWidget(setupGui.width/2+3, setupGui.height/2+12, 60, 12, Text.translatable(setupGui.translation("mcvmcomputers.setup.clearButton")), (bw) -> clearBinding(3)));
		setupGui.addButton(new ButtonWidget(setupGui.width/2+70, setupGui.height/2+12, 60, 12, Text.translatable(setupGui.translation("mcvmcomputers.setup.clearButton")), (bw) -> clearBinding(4)));
		
		int nextButtonW = textRender.getWidth(setupGui.translation("mcvmcomputers.setup.nextButton"))+40;
		setupGui.addButton(new ButtonWidget(setupGui.width/2 - (nextButtonW/2), setupGui.height - 40, nextButtonW, 20, Text.translatable(setupGui.translation("mcvmcomputers.setup.nextButton")), (bw) -> this.setupGui.nextPage()));
	}

}
