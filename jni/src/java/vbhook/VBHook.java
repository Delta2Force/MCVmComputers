package vbhook;

import java.io.FileOutputStream;
import java.io.File;

public class VBHook {
	//@return If the library has been successfully initialized.
	public native boolean init_glue(String vbox_home);
	//@return Pointer to IVirtualBoxClient
	public native long create_vb_client();
	//@return Pointer to IVirtualBox
	public native long create_vb(long vb_client);
	//@return Pointer to ISession
	public native long create_session(long vb_client);
	public native void terminate_glue();
	//@return Version string (e.g. "6.1.16")
	public native String get_vb_version();
	
	//@return Pointer to IMachine
	public native long find_or_create_vm(long vb, String name, String os_type);
	public native void vm_values(long session, long vb, long vm, long vram, long mem, long cpu, String hdd, String iso);
	public native void start_vm(long session, long vm);
	public native void stop_vm(long session);
	public native void create_hdd(long vb, long size, String file_format, String path);
	public native boolean vm_powered_on(long machine);
	//@return Screenshot
	public native byte[] tick_vm(long vb_client, long machine, int mouseDeltaX, int mouseDeltaY, int mouseDeltaScroll, int mouseClick, int[] scancodes);

	public native void free_vm(long vm);
	public native void free_vb(long vb);
	public native void free_session(long session);
	public native void free_vb_client(long vb_client);

	public static void main(String[] args) throws Exception {
		System.loadLibrary("vbhook");
		VBHook vbhook = new VBHook();
		if(!vbhook.init_glue("/usr/lib/virtualbox")) {
			System.out.println("Glue could not be initialized.");
			return;
		}
		System.out.println("Successfully doing stuff with VirtualBox " + vbhook.get_vb_version() + "!");
		
		long a = vbhook.create_vb_client();
		long b = vbhook.create_vb(a);
		long sess = vbhook.create_session(a);
		String path = new File("test.vdi").getAbsolutePath();

		vbhook.create_hdd(b, 1024*1024*1024, "vdi", path);
		long machine = vbhook.find_or_create_vm(b, "VmComputersVm", "Other");
		vbhook.vm_values(sess, b, machine, 16, 1024, 2, path, "");
		vbhook.start_vm(sess, machine);
		Thread.sleep(5000);
		long time = System.nanoTime();
		byte[] retval = vbhook.tick_vm(a, machine, 0, 0, 0, 0, new int[0]);
		System.out.println("Screenshot took " + (System.nanoTime() - time) + "ns");
		vbhook.stop_vm(sess);
		
		try (FileOutputStream fos = new FileOutputStream("screenshot.png")) {
		   fos.write(retval);
		}
		
		vbhook.free_session(sess);
		vbhook.free_vm(machine);
		vbhook.free_vb(b);
		vbhook.free_vb_client(a);
		vbhook.terminate_glue();
	}
}
