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
			return new ArrayList<>();
		}
		return ints;
	}
	
	//is this good practice? it seems absolutely horrendous to me but
	//i dont know how else to solve it
	public static int[] scancodeFromGLFWKey(int glfwKey) {
		//US Layout only
		//Missing some keys
		return switch (glfwKey) {
			case GLFW_KEY_ESCAPE -> new int[]{0x01};
			case GLFW_KEY_1 -> new int[]{0x02};
			case GLFW_KEY_2 -> new int[]{0x03};
			case GLFW_KEY_3 -> new int[]{0x04};
			case GLFW_KEY_4 -> new int[]{0x05};
			case GLFW_KEY_5 -> new int[]{0x06};
			case GLFW_KEY_6 -> new int[]{0x07};
			case GLFW_KEY_7 -> new int[]{0x08};
			case GLFW_KEY_8 -> new int[]{0x09};
			case GLFW_KEY_9 -> new int[]{0x0a};
			case GLFW_KEY_0 -> new int[]{0x0b};
			case GLFW_KEY_MINUS -> new int[]{0x0c};
			case GLFW_KEY_EQUAL -> new int[]{0x0d};
			case GLFW_KEY_BACKSPACE -> new int[]{0x0e};
			case GLFW_KEY_TAB -> new int[]{0x0f};
			case GLFW_KEY_Q -> new int[]{0x10};
			case GLFW_KEY_W -> new int[]{0x11};
			case GLFW_KEY_E -> new int[]{0x12};
			case GLFW_KEY_R -> new int[]{0x13};
			case GLFW_KEY_T -> new int[]{0x14};
			case GLFW_KEY_Y -> new int[]{0x15};
			case GLFW_KEY_U -> new int[]{0x16};
			case GLFW_KEY_I -> new int[]{0x17};
			case GLFW_KEY_O -> new int[]{0x18};
			case GLFW_KEY_P -> new int[]{0x19};
			case GLFW_KEY_LEFT_BRACKET -> new int[]{0x1a};
			case GLFW_KEY_RIGHT_BRACKET -> new int[]{0x1b};
			case GLFW_KEY_ENTER -> new int[]{0x1c};
			case GLFW_KEY_LEFT_CONTROL -> new int[]{0x1d};
			case GLFW_KEY_A -> new int[]{0x1e};
			case GLFW_KEY_S -> new int[]{0x1f};
			case GLFW_KEY_D -> new int[]{0x20};
			case GLFW_KEY_F -> new int[]{0x21};
			case GLFW_KEY_G -> new int[]{0x22};
			case GLFW_KEY_H -> new int[]{0x23};
			case GLFW_KEY_J -> new int[]{0x24};
			case GLFW_KEY_K -> new int[]{0x25};
			case GLFW_KEY_L -> new int[]{0x26};
			case GLFW_KEY_SEMICOLON -> new int[]{0x27};
			case GLFW_KEY_APOSTROPHE -> new int[]{0x28};
			case GLFW_KEY_GRAVE_ACCENT -> new int[]{0x29};
			case GLFW_KEY_LEFT_SHIFT -> new int[]{0x2a};
			case GLFW_KEY_BACKSLASH -> new int[]{0x2b};
			case GLFW_KEY_Z -> new int[]{0x2c};
			case GLFW_KEY_X -> new int[]{0x2d};
			case GLFW_KEY_C -> new int[]{0x2e};
			case GLFW_KEY_V -> new int[]{0x2f};
			case GLFW_KEY_B -> new int[]{0x30};
			case GLFW_KEY_N -> new int[]{0x31};
			case GLFW_KEY_M -> new int[]{0x32};
			case GLFW_KEY_COMMA -> new int[]{0x33};
			case GLFW_KEY_PERIOD -> new int[]{0x34};
			case GLFW_KEY_SLASH -> new int[]{0x35};
			case GLFW_KEY_RIGHT_SHIFT -> new int[]{0x36};
			case GLFW_KEY_KP_MULTIPLY -> new int[]{0x37};
			case GLFW_KEY_LEFT_ALT -> new int[]{0x38};
			case GLFW_KEY_SPACE -> new int[]{0x39};
			case GLFW_KEY_CAPS_LOCK -> new int[]{0x3a};
			case GLFW_KEY_F1 -> new int[]{0x3b};
			case GLFW_KEY_F2 -> new int[]{0x3c};
			case GLFW_KEY_F3 -> new int[]{0x3d};
			case GLFW_KEY_F4 -> new int[]{0x3e};
			case GLFW_KEY_F5 -> new int[]{0x3f};
			case GLFW_KEY_F6 -> new int[]{0x40};
			case GLFW_KEY_F7 -> new int[]{0x41};
			case GLFW_KEY_F8 -> new int[]{0x42};
			case GLFW_KEY_F9 -> new int[]{0x43};
			case GLFW_KEY_F10 -> new int[]{0x44};
			case GLFW_KEY_NUM_LOCK -> new int[]{0x45};
			case GLFW_KEY_SCROLL_LOCK -> new int[]{0x46};
			case GLFW_KEY_KP_7 -> new int[]{0x47};
			case GLFW_KEY_KP_8 -> new int[]{0x48};
			case GLFW_KEY_KP_9 -> new int[]{0x49};
			case GLFW_KEY_KP_SUBTRACT -> new int[]{0x4a};
			case GLFW_KEY_KP_4 -> new int[]{0x4b};
			case GLFW_KEY_KP_5 -> new int[]{0x4c};
			case GLFW_KEY_KP_6 -> new int[]{0x4d};
			case GLFW_KEY_KP_ADD -> new int[]{0x4e};
			case GLFW_KEY_KP_1 -> new int[]{0x4f};
			case GLFW_KEY_KP_2 -> new int[]{0x50};
			case GLFW_KEY_KP_3 -> new int[]{0x51};
			case GLFW_KEY_KP_0 -> new int[]{0x52};
			case GLFW_KEY_KP_DECIMAL -> new int[]{0x53};
			case GLFW_KEY_F11 -> new int[]{0x57};
			case GLFW_KEY_F12 -> new int[]{0x58};
			case GLFW_KEY_DOWN -> new int[]{0xe0, 0x50};
			case GLFW_KEY_UP -> new int[]{0xe0, 0x48};
			case GLFW_KEY_LEFT -> new int[]{0xe0, 0x4b};
			case GLFW_KEY_RIGHT -> new int[]{0xe0, 0x4d};
			case GLFW_KEY_RIGHT_ALT -> new int[]{0xe0, 0x38};
			case GLFW_KEY_KP_ENTER -> new int[]{0xe0, 0x1c};
			case GLFW_KEY_RIGHT_CONTROL -> new int[]{0xe0, 0x1d};
			case GLFW_KEY_KP_DIVIDE -> new int[]{0xe0, 0x35};
			case GLFW_KEY_HOME -> new int[]{0xe0, 0x47};
			case GLFW_KEY_PAGE_UP -> new int[]{0xe0, 0x49};
			case GLFW_KEY_END -> new int[]{0xe0, 0x50};
			case GLFW_KEY_PAGE_DOWN -> new int[]{0xe0, 0x51};
			case GLFW_KEY_INSERT -> new int[]{0xe0, 0x52};
			case GLFW_KEY_DELETE -> new int[]{0xe0, 0x53};
			case GLFW_KEY_MENU -> new int[]{0xe0, 0x5d};
			default -> new int[]{0x00};
		};
	}
}
