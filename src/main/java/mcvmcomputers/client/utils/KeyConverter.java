package mcvmcomputers.client.utils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyConverter {
	public static List<Integer> toVBKey(int key, int action) {
		List<Integer> ints = new ArrayList<>();
		int[] sck = scancodeFromGLFWKey(key);
		if(action == GLFW_PRESS) {
			for(int s : sck) {
				ints.add(s);
			}
		}else if(action == GLFW_RELEASE){
			for(int s : sck) {
				int ts = s;
				if(ts != 0xe0) {
					ts += 0x80;
				}
				ints.add(ts);
			}
		}else {
			return new ArrayList<Integer>();
		}
		return ints;
	}
	
	//is this good practice? it seems absolutely horrendous to me but
	//i dont know how else to solve it
	public static int[] scancodeFromGLFWKey(int glfwKey) {
		//US Layout only
		//Missing some keys
		switch(glfwKey) {
		case GLFW_KEY_ESCAPE:
			return new int[] {0x01};
		case GLFW_KEY_1:
			return new int[] {0x02};
		case GLFW_KEY_2:
			return new int[] {0x03};
		case GLFW_KEY_3:
			return new int[] {0x04};
		case GLFW_KEY_4:
			return new int[] {0x05};
		case GLFW_KEY_5:
			return new int[] {0x06};
		case GLFW_KEY_6:
			return new int[] {0x07};
		case GLFW_KEY_7:
			return new int[] {0x08};
		case GLFW_KEY_8:
			return new int[] {0x09};
		case GLFW_KEY_9:
			return new int[] {0x0a};
		case GLFW_KEY_0:
			return new int[] {0x0b};
		case GLFW_KEY_MINUS:
			return new int[] {0x0c};
		case GLFW_KEY_EQUAL:
			return new int[] {0x0d};
		case GLFW_KEY_BACKSPACE:
			return new int[] {0x0e};
		case GLFW_KEY_TAB:
			return new int[] {0x0f};
		case GLFW_KEY_Q:
			return new int[] {0x10};
		case GLFW_KEY_W:
			return new int[] {0x11};
		case GLFW_KEY_E:
			return new int[] {0x12};
		case GLFW_KEY_R:
			return new int[] {0x13};
		case GLFW_KEY_T:
			return new int[] {0x14};
		case GLFW_KEY_Y:
			return new int[] {0x15};
		case GLFW_KEY_U:
			return new int[] {0x16};
		case GLFW_KEY_I:
			return new int[] {0x17};
		case GLFW_KEY_O:
			return new int[] {0x18};
		case GLFW_KEY_P:
			return new int[] {0x19};
		case GLFW_KEY_LEFT_BRACKET:
			return new int[] {0x1a};
		case GLFW_KEY_RIGHT_BRACKET:
			return new int[] {0x1b};
		case GLFW_KEY_ENTER:
			return new int[] {0x1c};
		case GLFW_KEY_LEFT_CONTROL:
			return new int[] {0x1d};
		case GLFW_KEY_A:
			return new int[] {0x1e};
		case GLFW_KEY_S:
			return new int[] {0x1f};
		case GLFW_KEY_D:
			return new int[] {0x20};
		case GLFW_KEY_F:
			return new int[] {0x21};
		case GLFW_KEY_G:
			return new int[] {0x22};
		case GLFW_KEY_H:
			return new int[] {0x23};
		case GLFW_KEY_J:
			return new int[] {0x24};
		case GLFW_KEY_K:
			return new int[] {0x25};
		case GLFW_KEY_L:
			return new int[] {0x26};
		case GLFW_KEY_SEMICOLON:
			return new int[] {0x27};
		case GLFW_KEY_APOSTROPHE:
			return new int[] {0x28};
		case GLFW_KEY_GRAVE_ACCENT:
			return new int[] {0x29};
		case GLFW_KEY_LEFT_SHIFT:
			return new int[] {0x2a};
		case GLFW_KEY_BACKSLASH:
			return new int[] {0x2b};
		case GLFW_KEY_Z:
			return new int[] {0x2c};
		case GLFW_KEY_X:
			return new int[] {0x2d};
		case GLFW_KEY_C:
			return new int[] {0x2e};
		case GLFW_KEY_V:
			return new int[] {0x2f};
		case GLFW_KEY_B:
			return new int[] {0x30};
		case GLFW_KEY_N:
			return new int[] {0x31};
		case GLFW_KEY_M:
			return new int[] {0x32};
		case GLFW_KEY_COMMA:
			return new int[] {0x33};
		case GLFW_KEY_PERIOD:
			return new int[] {0x34};
		case GLFW_KEY_SLASH:
			return new int[] {0x35};
		case GLFW_KEY_RIGHT_SHIFT:
			return new int[] {0x36};
		case GLFW_KEY_KP_MULTIPLY:
			return new int[] {0x37};
		case GLFW_KEY_LEFT_ALT:
			return new int[] {0x38};
		case GLFW_KEY_SPACE:
			return new int[] {0x39};
		case GLFW_KEY_CAPS_LOCK:
			return new int[] {0x3a};
		case GLFW_KEY_F1:
			return new int[] {0x3b};
		case GLFW_KEY_F2:
			return new int[] {0x3c};
		case GLFW_KEY_F3:
			return new int[] {0x3d};
		case GLFW_KEY_F4:
			return new int[] {0x3e};
		case GLFW_KEY_F5:
			return new int[] {0x3f};
		case GLFW_KEY_F6:
			return new int[] {0x40};
		case GLFW_KEY_F7:
			return new int[] {0x41};
		case GLFW_KEY_F8:
			return new int[] {0x42};
		case GLFW_KEY_F9:
			return new int[] {0x43};
		case GLFW_KEY_F10:
			return new int[] {0x44};
		case GLFW_KEY_NUM_LOCK:
			return new int[] {0x45};
		case GLFW_KEY_SCROLL_LOCK:
			return new int[] {0x46};
		case GLFW_KEY_KP_7:
			return new int[] {0x47};
		case GLFW_KEY_KP_8:
			return new int[] {0x48};
		case GLFW_KEY_KP_9:
			return new int[] {0x49};
		case GLFW_KEY_KP_SUBTRACT:
			return new int[] {0x4a};
		case GLFW_KEY_KP_4:
			return new int[] {0x4b};
		case GLFW_KEY_KP_5:
			return new int[] {0x4c};
		case GLFW_KEY_KP_6:
			return new int[] {0x4d};
		case GLFW_KEY_KP_ADD:
			return new int[] {0x4e};
		case GLFW_KEY_KP_1:
			return new int[] {0x4f};
		case GLFW_KEY_KP_2:
			return new int[] {0x50};
		case GLFW_KEY_KP_3:
			return new int[] {0x51};
		case GLFW_KEY_KP_0:
			return new int[] {0x52};
		case GLFW_KEY_KP_DECIMAL:
			return new int[] {0x53};
		case GLFW_KEY_F11:
			return new int[] {0x57};
		case GLFW_KEY_F12:
			return new int[] {0x58};
		case GLFW_KEY_DOWN:
			return new int[] {0xe0, 0x50};
		case GLFW_KEY_UP:
			return new int[] {0xe0, 0x48};
		case GLFW_KEY_LEFT:
			return new int[] {0xe0, 0x4b};
		case GLFW_KEY_RIGHT:
			return new int[] {0xe0, 0x4d};
		case GLFW_KEY_RIGHT_ALT:
			return new int[] {0xe0, 0x38};
		case GLFW_KEY_KP_ENTER:
			return new int[] {0xe0, 0x1c};
		case GLFW_KEY_RIGHT_CONTROL:
			return new int[] {0xe0, 0x1d};
		case GLFW_KEY_KP_DIVIDE:
			return new int[] {0xe0, 0x35};
		case GLFW_KEY_HOME:
			return new int[] {0xe0, 0x47};
		case GLFW_KEY_PAGE_UP:
			return new int[] {0xe0, 0x49};
		case GLFW_KEY_END:
			return new int[] {0xe0, 0x50};
		case GLFW_KEY_PAGE_DOWN:
			return new int[] {0xe0, 0x51};
		case GLFW_KEY_INSERT:
			return new int[] {0xe0, 0x52};
		case GLFW_KEY_DELETE:
			return new int[] {0xe0, 0x53};
		case GLFW_KEY_MENU:
			return new int[] {0xe0, 0x5d};
		}
		return new int[] {0x00};
	}
}
