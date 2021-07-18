package vbhook;

public class VBHook {
	public native long create_vb_client();
	public native long create_vb(long vb_client);
	public native void free_vb(long vb);
	public native void free_vb_client(long vb_client);
	public native void init_glue();
	public native void terminate_glue();

	public static void main(String[] args) {
		System.loadLibrary("vbhook");
		VBHook vbhook = new VBHook();
		vbhook.init_glue();
		long a = vbhook.create_vb_client();
		long b = vbhook.create_vb(a);

		System.out.printf("%d %d\n", a, b);
		vbhook.free_vb(b);
		vbhook.free_vb_client(a);
		vbhook.terminate_glue();
	}
}
