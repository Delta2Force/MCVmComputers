package vbhook;

public class VBHook {
	/**
	 * Creates an IVirtualBoxClient object
	 *
	 * @return Pointer to IVirtualBoxClient object
	 */
	public native long create_vb_client();

	/**
	 * Creates an IVirtualBox object
	 *
	 * @return Pointer to IVirtualBox object
	 */
	public native long create_vb(long vb_client);

	public static void main(String[] args) {
		System.loadLibrary("vbhook");
		VBHook vbhook = new VBHook();
		long a = vbhook.create_vb_client();
		long b = vbhook.create_vb(a);

		System.out.printf("%d %d\n", a, b);
	}
}
