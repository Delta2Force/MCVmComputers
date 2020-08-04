package mcvmcomputers.client.utils;

public class QemuKey {
	public int keySym;
	public boolean pressed;
	
	public QemuKey(int keySym, boolean pressed) {
		this.keySym = keySym;
		this.pressed = pressed;
	}
	
	@Override
	public String toString() {
		return Integer.toHexString(keySym) + " " + pressed;
	}
}
