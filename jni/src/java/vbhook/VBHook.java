package vbhook;

import java.io.FileWriter;

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
		
		vbhook.create_hdd(b, 1024*1024*1024, "vdi", "/mnt/e/coding/MCVmComputers/jni/test.vdi");
		long machine = vbhook.find_or_create_vm(b, "VmComputersVm", "Other");
		vbhook.vm_values(sess, b, machine, 16, 1024, 2, "/mnt/e/coding/MCVmComputers/jni/test.vdi", "");
		vbhook.start_vm(sess, machine);
		Thread.sleep(5000);
		byte[] retval = vbhook.tick_vm(a, machine, 0, 0, 0, 0, new int[0]);
		vbhook.stop_vm(sess);

		char[] write = new char[retval.length];
		for(int i = 0;i<retval.length;i++) {
			write[i] = (char)retval[i];
		}
		
		FileWriter fw = new FileWriter("screenshot.png");
		fw.write(write, 0, retval.length);
		fw.flush(); fw.close();

		vbhook.free_session(sess);
		vbhook.free_vm(machine);
		vbhook.free_vb(b);
		vbhook.free_vb_client(a);
		vbhook.terminate_glue();
	}
}
