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
	
	//Source: https://cgit.freedesktop.org/xorg/proto/x11proto/plain/keysymdef.h
	public static int keySymFromGLFW(int glfwKey, boolean capital) {
		switch(glfwKey) {
		case GLFW_KEY_BACKSPACE:
			return 0xff08;
		case GLFW_KEY_TAB:
			return 0xff09;
		case GLFW_KEY_ENTER:
			return 0xff0d;
		case GLFW_KEY_PAUSE:
			return 0xff13;
		case GLFW_KEY_SCROLL_LOCK:
			return 0xff14;
		case GLFW_KEY_ESCAPE:
			return 0xff1b;
		case GLFW_KEY_DELETE:
			return 0xffff;
		case GLFW_KEY_HOME:
			return 0xff50;
		case GLFW_KEY_LEFT:
			return 0xff51;
		case GLFW_KEY_UP:
			return 0xff52;
		case GLFW_KEY_RIGHT:
			return 0xff53;
		case GLFW_KEY_DOWN:
			return 0xff54;
		case GLFW_KEY_PAGE_UP:
			return 0xff55;
		case GLFW_KEY_PAGE_DOWN:
			return 0xff56;
		case GLFW_KEY_END:
			return 0xff57;
		case GLFW_KEY_MENU:
			return 0xff67;
		case GLFW_KEY_KP_ENTER:
			return 0xff8d;
		case GLFW_KEY_KP_ADD:
			return 0xffab;
		case GLFW_KEY_KP_DECIMAL:
			return 0xffae;
		case GLFW_KEY_KP_DIVIDE:
			return 0xffaf;
		case GLFW_KEY_KP_MULTIPLY:
			return 0xffaa;
		case GLFW_KEY_KP_EQUAL:
			return 0xffbd;
		case GLFW_KEY_KP_SUBTRACT:
			return 0xffad;
		case GLFW_KEY_KP_0:
			return 0xffb0;
		case GLFW_KEY_KP_1:
			return 0xffb1;
		case GLFW_KEY_KP_2:
			return 0xffb2;
		case GLFW_KEY_KP_3:
			return 0xffb3;
		case GLFW_KEY_KP_4:
			return 0xffb4;
		case GLFW_KEY_KP_5:
			return 0xffb5;
		case GLFW_KEY_KP_6:
			return 0xffb6;
		case GLFW_KEY_KP_7:
			return 0xffb7;
		case GLFW_KEY_KP_8:
			return 0xffb8;
		case GLFW_KEY_KP_9:
			return 0xffb9;
		case GLFW_KEY_F1:
			return 0xffbe;
		case GLFW_KEY_F2:
			return 0xffbf;
		case GLFW_KEY_F3:
			return 0xffc0;
		case GLFW_KEY_F4:
			return 0xffc1;
		case GLFW_KEY_F5:
			return 0xffc2;
		case GLFW_KEY_F6:
			return 0xffc3;
		case GLFW_KEY_F7:
			return 0xffc4;
		case GLFW_KEY_F8:
			return 0xffc5;
		case GLFW_KEY_F9:
			return 0xffc6;
		case GLFW_KEY_F10:
			return 0xffc7;
		case GLFW_KEY_F11:
			return 0xffc8;
		case GLFW_KEY_F12:
			return 0xffc9;
		case GLFW_KEY_LEFT_SHIFT:
			return 0xffe1;
		case GLFW_KEY_RIGHT_SHIFT:
			return 0xffe2;
		case GLFW_KEY_LEFT_CONTROL:
			return 0xffe3;
		case GLFW_KEY_RIGHT_CONTROL:
			return 0xffe4;
		case GLFW_KEY_CAPS_LOCK:
			return 0xffe5;
		case GLFW_KEY_LEFT_ALT:
			return 0xffe9;
		case GLFW_KEY_RIGHT_ALT:
			return 0xffea;
		case GLFW_KEY_LEFT_SUPER:
			return 0xffeb;
		case GLFW_KEY_RIGHT_SUPER:
			return 0xffec;
		case GLFW_KEY_SPACE:
			return 0x0020;
		case GLFW_KEY_0:
			return 0x0030;
		case GLFW_KEY_1:
			return 0x0031;
		case GLFW_KEY_2:
			return 0x0032;
		case GLFW_KEY_3:
			return 0x0033;
		case GLFW_KEY_4:
			return 0x0034;
		case GLFW_KEY_5:
			return 0x0035;
		case GLFW_KEY_6:
			return 0x0036;
		case GLFW_KEY_7:
			return 0x0037;
		case GLFW_KEY_8:
			return 0x0038;
		case GLFW_KEY_9:
			return 0x0039;
		case GLFW_KEY_SEMICOLON:
			return 0x003b;
		case GLFW_KEY_A:
			if(capital){
				return 0x0041;
			}
			return 0x0061;
		case GLFW_KEY_B:
			if(capital){
				return 0x0042;
			}
			return 0x0062;
		case GLFW_KEY_C:
			if(capital){
				return 0x0043;
			}
			return 0x0063;
		case GLFW_KEY_D:
			if(capital){
				return 0x0044;
			}
			return 0x0064;
		case GLFW_KEY_E:
			if(capital){
				return 0x0045;
			}
			return 0x0065;
		case GLFW_KEY_F:
			if(capital){
				return 0x0046;
			}
			return 0x0066;
		case GLFW_KEY_G:
			if(capital){
				return 0x0047;
			}
			return 0x0067;
		case GLFW_KEY_H:
			if(capital){
				return 0x0048;
			}
			return 0x0068;
		case GLFW_KEY_I:
			if(capital){
				return 0x0049;
			}
			return 0x0069;
		case GLFW_KEY_J:
			if(capital){
				return 0x004a;
			}
			return 0x006a;
		case GLFW_KEY_K:
			if(capital){
				return 0x004b;
			}
			return 0x006b;
		case GLFW_KEY_L:
			if(capital){
				return 0x004c;
			}
			return 0x006c;
		case GLFW_KEY_M:
			if(capital){
				return 0x004d;
			}
			return 0x006d;
		case GLFW_KEY_N:
			if(capital){
				return 0x004e;
			}
			return 0x006e;
		case GLFW_KEY_O:
			if(capital){
				return 0x004f;
			}
			return 0x006f;
		case GLFW_KEY_P:
			if(capital){
				return 0x0050;
			}
			return 0x0070;
		case GLFW_KEY_Q:
			if(capital){
				return 0x0051;
			}
			return 0x0071;
		case GLFW_KEY_R:
			if(capital){
				return 0x0052;
			}
			return 0x0072;
		case GLFW_KEY_S:
			if(capital){
				return 0x0053;
			}
			return 0x0073;
		case GLFW_KEY_T:
			if(capital){
				return 0x0054;
			}
			return 0x0074;
		case GLFW_KEY_U:
			if(capital){
				return 0x0055;
			}
			return 0x0075;
		case GLFW_KEY_V:
			if(capital){
				return 0x0056;
			}
			return 0x0076;
		case GLFW_KEY_W:
			if(capital){
				return 0x0057;
			}
			return 0x0077;
		case GLFW_KEY_X:
			if(capital){
				return 0x0058;
			}
			return 0x0078;
		case GLFW_KEY_Y:
			if(capital){
				return 0x0059;
			}
			return 0x0079;
		case GLFW_KEY_Z:
			if(capital){
				return 0x005a;
			}

			return 0x007a;
		case GLFW_KEY_LEFT_BRACKET:
			return 0x005b;
		case GLFW_KEY_BACKSLASH:
			return 0x005c;
		case GLFW_KEY_RIGHT_BRACKET:
			return 0x005d;
		case GLFW_KEY_GRAVE_ACCENT:
			return 0x0060;
		}
		return 0;
	}
}
