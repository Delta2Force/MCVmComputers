#include "vbhook_VBHook.h"
#include "VBoxCAPIGlue.h"
#include "jni.h"
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <stdint.h>

#if defined(__MINGW32__) || defined(_MSC_VER)
#define VBHOOK_WIN
#include <Windows.h>
int setenv(const char *name, const char *value, int overwrite)
{
    int errcode = 0;
    if(!overwrite) {
        size_t envsize = 0;
        errcode = getenv_s(&envsize, NULL, 0, name);
        if(errcode || envsize) return errcode;
    }
    return _putenv_s(name, value);
}
#endif

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1vb_1client (JNIEnv* env, jobject obj) {
	IVirtualBoxClient* vboxClient = NULL;
	g_pVBoxFuncs->pfnClientInitialize(NULL, &vboxClient);
	return (jlong)vboxClient;
}

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1vb(JNIEnv* env, jobject obj, jlong lng) {
	IVirtualBoxClient* vboxClient = (IVirtualBoxClient*) lng;
	IVirtualBox* vbox = NULL;
#ifdef VBHOOK_WIN
	vboxClient->lpVtbl->get_VirtualBox(vboxClient, &vbox);
#else
	vboxClient->lpVtbl->GetVirtualBox(vboxClient, &vbox);
#endif
	return (jlong)vbox;
}

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1session(JNIEnv* env, jobject obj, jlong lng) {
	ISession* session = NULL;
	IVirtualBoxClient* vboxClient = (IVirtualBoxClient*) lng;
#ifdef VBHOOK_WIN
	vboxClient->lpVtbl->get_Session(vboxClient, &session);
#else
	vboxClient->lpVtbl->GetSession(vboxClient, &session);
#endif
	return (jlong) session;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1vm(JNIEnv* env, jobject obj, jlong lng) {
	if(lng) IMachine_Release((IMachine*)lng);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1vb(JNIEnv* env, jobject obj, jlong lng) {
	if(lng) IVirtualBox_Release((IVirtualBox*)lng);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1session(JNIEnv* env, jobject obj, jlong lng) {
	if(lng) ISession_Release((ISession*)lng);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1and_1unlock_1session(JNIEnv* env, jobject obj, jlong lng) {
	ISession* session = (ISession*)lng;
	if(lng) {
		ISession_UnlockMachine(session);
		ISession_Release(session);
	}
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1vb_1client(JNIEnv* env, jobject obj, jlong lng) {
	if(lng) IVirtualBoxClient_Release((IVirtualBoxClient*)lng);
}

#ifdef VBHOOK_WIN
wchar_t* charToWChar(const char* text)
{
    const size_t size = strlen(text) + 1;
    wchar_t* wText = (wchar_t*)malloc(size*sizeof(wchar_t));
    mbstowcs(wText, text, size);
    return wText;
}
#endif

JNIEXPORT jboolean JNICALL Java_vbhook_VBHook_init_1glue(JNIEnv* env, jobject obj, jstring vbox_home) {
	setenv("VBOX_APP_HOME", (*env)->GetStringUTFChars(env, vbox_home, NULL), 1);
#ifdef VBHOOK_WIN
	SetDefaultDllDirectories(LOAD_LIBRARY_SEARCH_USER_DIRS | LOAD_LIBRARY_SEARCH_DEFAULT_DIRS);
	AddDllDirectory(charToWChar((*env)->GetStringUTFChars(env, vbox_home, NULL)));
#endif
	if(VBoxCGlueInit()) {
		return FALSE;
	}
	return TRUE;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_terminate_1glue(JNIEnv* env, jobject obj) {
	VBoxCGlueTerm();
}

JNIEXPORT jstring JNICALL Java_vbhook_VBHook_get_1vb_1version(JNIEnv* env, jobject obj) {
	char str[256];
	unsigned int ver = g_pVBoxFuncs->pfnGetVersion();
	sprintf(str, "%u.%u.%u", ver / 1000000, ver / 1000 % 1000, ver % 1000);
	return (*env)->NewStringUTF(env, str);
}

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_find_1or_1create_1vm(JNIEnv* env, jobject obj, jlong vb, jstring name, jstring os_type){
	IMachine* vm = NULL;
	IVirtualBox* virtualbox = (IVirtualBox*)vb;
#ifdef VBHOOK_WIN
	BSTR vbname; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, name, NULL), &vbname);
	BSTR vbos_type; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, os_type, NULL), &vbos_type);
#else
	PRUnichar* vbname = (PRUnichar*)(*env)->GetStringChars(env, name, NULL);
	PRUnichar* vbos_type = (PRUnichar*)(*env)->GetStringChars(env, os_type, NULL);
#endif

	virtualbox->lpVtbl->FindMachine(virtualbox, vbname, &vm);
	if(!vm) {
		//create if not found
		BSTR empty; g_pVBoxFuncs->pfnUtf8ToUtf16("", &empty);
		BSTR options; g_pVBoxFuncs->pfnUtf8ToUtf16("forceOverwrite=1", &options);
#ifdef VBHOOK_WIN
		virtualbox->lpVtbl->CreateMachine(virtualbox, empty, vbname, NULL, vbos_type, options, &vm);
#else
		virtualbox->lpVtbl->CreateMachine(virtualbox, empty, vbname, 0, NULL, vbos_type, options, &vm);
#endif
		g_pVBoxFuncs->pfnUtf16Free(empty); g_pVBoxFuncs->pfnUtf16Free(options);
		IVirtualBox_RegisterMachine(virtualbox, vm);
	}

#ifdef VBHOOK_WIN
	g_pVBoxFuncs->pfnUtf16Free(vbname);
	g_pVBoxFuncs->pfnUtf16Free(vbos_type);
#endif
	return (jlong)vm;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_vm_1values(JNIEnv* env, jobject obj, jlong session, jlong vb, jlong vm, jlong vram, jlong mem, jlong cpu, jstring hdd, jstring iso){
	ISession* sessionvb = (ISession*)session;
	IVirtualBox* vbvb = (IVirtualBox*)vb;
	IMachine* vmvb = (IMachine*)vm;
	
	vmvb->lpVtbl->LockMachine(vmvb, sessionvb, LockType_Write);
	IMachine* edit = NULL;
#ifdef VBHOOK_WIN
	sessionvb->lpVtbl->get_Machine(sessionvb, &edit);
	edit->lpVtbl->put_MemorySize(edit, mem);
	edit->lpVtbl->put_CPUCount(edit, cpu);
#else
	sessionvb->lpVtbl->GetMachine(sessionvb, &edit);
	edit->lpVtbl->SetMemorySize(edit, mem);
	edit->lpVtbl->SetCPUCount(edit, cpu);
#endif
	
	IGraphicsAdapter* adapter = NULL;
#ifdef VBHOOK_WIN
	edit->lpVtbl->get_GraphicsAdapter(edit, &adapter);
	adapter->lpVtbl->put_Accelerate2DVideoEnabled(adapter, TRUE);
	adapter->lpVtbl->put_Accelerate3DEnabled(adapter, FALSE);
	adapter->lpVtbl->put_VRAMSize(adapter, vram);
	adapter->lpVtbl->Release(adapter); //we're done with it
#else
	edit->lpVtbl->GetGraphicsAdapter(edit, &adapter);
	adapter->lpVtbl->SetAccelerate2DVideoEnabled(adapter, TRUE);
	adapter->lpVtbl->SetAccelerate3DEnabled(adapter, FALSE);
	adapter->lpVtbl->SetVRAMSize(adapter, vram);
	adapter->lpVtbl->Release(adapter); //we're done with it
#endif

	BSTR storage_controller_name;
	g_pVBoxFuncs->pfnUtf8ToUtf16("IDE Controller", &storage_controller_name);

	edit->lpVtbl->RemoveStorageController(edit, storage_controller_name);
	IStorageController* storage_controller = NULL;
	edit->lpVtbl->AddStorageController(edit, storage_controller_name, StorageBus_IDE, &storage_controller);
	storage_controller->lpVtbl->Release(storage_controller);
	
	if((*env)->GetStringLength(env, hdd) > 0) {
		IMedium* hdd_medium = NULL;
#ifdef VBHOOK_WIN
		BSTR path; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, hdd, NULL), &path);
		vbvb->lpVtbl->OpenMedium(vbvb, path, DeviceType_HardDisk, AccessMode_ReadWrite, TRUE, &hdd_medium);
		g_pVBoxFuncs->pfnUtf16Free(path);
#else
		vbvb->lpVtbl->OpenMedium(vbvb, (PRUnichar*)(*env)->GetStringChars(env, hdd, NULL), DeviceType_HardDisk, AccessMode_ReadWrite, TRUE, &hdd_medium);
#endif
		if(hdd_medium) {
			edit->lpVtbl->AttachDevice(edit, storage_controller_name, 0, 0, DeviceType_HardDisk, hdd_medium);
			hdd_medium->lpVtbl->Release(hdd_medium);
		}else{
			edit->lpVtbl->AttachDevice(edit, storage_controller_name, 0, 0, DeviceType_HardDisk, NULL);
		}
	}
	
	if((*env)->GetStringLength(env, iso) > 0) {
		IMedium* iso_medium = NULL;
#ifdef VBHOOK_WIN
		BSTR path; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, iso, NULL), &path);
		vbvb->lpVtbl->OpenMedium(vbvb, path, DeviceType_DVD, AccessMode_ReadOnly, TRUE, &iso_medium);
		g_pVBoxFuncs->pfnUtf16Free(path);
#else
		vbvb->lpVtbl->OpenMedium(vbvb, (PRUnichar*)(*env)->GetStringChars(env, iso, NULL), DeviceType_DVD, AccessMode_ReadOnly, TRUE, &iso_medium);
#endif
		edit->lpVtbl->AttachDevice(edit, storage_controller_name, 1, 0, DeviceType_DVD, iso_medium);
		iso_medium->lpVtbl->Release(iso_medium);
	}else{
		edit->lpVtbl->AttachDevice(edit, storage_controller_name, 1, 0, DeviceType_DVD, NULL);
	}

	edit->lpVtbl->SaveSettings(edit);
	sessionvb->lpVtbl->UnlockMachine(sessionvb);
	edit->lpVtbl->Release(edit);
	g_pVBoxFuncs->pfnUtf16Free(storage_controller_name);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_start_1vm(JNIEnv* env, jobject obj, jlong session, jlong vm) {
	ISession* sessionvb = (ISession*)session;
	IMachine* vmvb = (IMachine*)vm;
#ifdef VBHOOK_WIN
	MachineState state;
	vmvb->lpVtbl->get_State(vmvb, &state);
#else
	PRUint32 state;
	vmvb->lpVtbl->GetState(vmvb, &state);
#endif
	if(state != MachineState_PoweredOff) return;
	
	BSTR headless; g_pVBoxFuncs->pfnUtf8ToUtf16("headless", &headless);
	IProgress* progress = NULL;
#ifdef VBHOOK_WIN
	vmvb->lpVtbl->LaunchVMProcess(vmvb, sessionvb, headless, NULL, &progress);
#else
	vmvb->lpVtbl->LaunchVMProcess(vmvb, sessionvb, headless, 0, NULL, &progress);
#endif
	progress->lpVtbl->WaitForCompletion(progress, -1);
	progress->lpVtbl->Release(progress);
	
	console->lpVtbl->Release(console);
	g_pVBoxFuncs->pfnUtf16Free(headless);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_stop_1vm(JNIEnv* env, jobject obj, jlong vbsession_ptr) {
	ISession* sessionvb = (ISession*)vbsession_ptr;
	if(!vbsession_ptr) return;

	IConsole* console = NULL;
#ifdef VBHOOK_WIN
	sessionvb->lpVtbl->get_Console(sessionvb, &console);
#else
	sessionvb->lpVtbl->GetConsole(sessionvb, &console);
#endif
	if(console == NULL) {
		sessionvb->lpVtbl->UnlockMachine(sessionvb);
		sessionvb->lpVtbl->Release(sessionvb);
		return;
	}
	IProgress* pdprogress = NULL;
	console->lpVtbl->PowerDown(console, &pdprogress);
	pdprogress->lpVtbl->WaitForCompletion(pdprogress, -1);

	sessionvb->lpVtbl->UnlockMachine(sessionvb);
	pdprogress->lpVtbl->Release(pdprogress);
	console->lpVtbl->Release(console);
	sessionvb->lpVtbl->Release(sessionvb);
	g_pVBoxFuncs->pfnProcessEventQueue(250);
}

JNIEXPORT jboolean JNICALL Java_vbhook_VBHook_vm_1powered_1on(JNIEnv* env, jobject obj, jlong machine) {
	IMachine* machinevb = (IMachine*)machine;
#ifdef VBHOOK_WIN
	MachineState state;
	machinevb->lpVtbl->get_State(machinevb, &state);
#else
	PRUint32 state;
	machinevb->lpVtbl->GetState(machinevb, &state);
#endif

	g_pVBoxFuncs->pfnProcessEventQueue(250);
	return state == MachineState_Running || state == MachineState_Starting || state == MachineState_Saving || state == MachineState_Stopping;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_create_1hdd(JNIEnv* env, jobject obj, jlong vb, jlong size, jstring format, jstring path) {
	IMedium* medium = NULL;
	IVirtualBox* vbvb = (IVirtualBox*)vb;
#ifdef VBHOOK_WIN
	BSTR vbformat; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, format, NULL), &vbformat);
	BSTR vbpath; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, path, NULL), &vbpath);
	vbvb->lpVtbl->CreateMedium(vbvb, vbformat, vbpath, AccessMode_ReadWrite, DeviceType_HardDisk, &medium);
	g_pVBoxFuncs->pfnUtf16Free(vbformat);
	g_pVBoxFuncs->pfnUtf16Free(vbpath);
#else
	vbvb->lpVtbl->CreateMedium(vbvb, (PRUnichar*)(*env)->GetStringChars(env, format, NULL), (PRUnichar*)(*env)->GetStringChars(env, path, NULL), AccessMode_ReadWrite, DeviceType_HardDisk, &medium);
#endif

	IProgress* progress = NULL;
#ifdef VBHOOK_WIN
	ULONG variant = MediumVariant_Standard;
	
	SAFEARRAY* savariants = g_pVBoxFuncs->pfnSafeArrayCreateVector(VT_I4, 0, 1);
	g_pVBoxFuncs->pfnSafeArrayCopyInParamHelper(savariants, &variant, sizeof(ULONG));
	medium->lpVtbl->CreateBaseStorage(medium, size, ComSafeArrayAsInParam(savariants), &progress);
	g_pVBoxFuncs->pfnSafeArrayDestroy(savariants);
#else
	PRUint32 variant = MediumVariant_Standard;
	medium->lpVtbl->CreateBaseStorage(medium, size, 1, &variant, &progress);
#endif
	progress->lpVtbl->WaitForCompletion(progress, -1);

	medium->lpVtbl->Release(medium);
	progress->lpVtbl->Release(progress);
	g_pVBoxFuncs->pfnProcessEventQueue(250);
}

JNIEXPORT jboolean JNICALL Java_vbhook_VBHook_vm_1iso_1ejected(JNIEnv* env, jobject obj, jlong vbmachine){
	IMachine* edit = (IMachine*)vbmachine;
	IMediumAttachment* attachment = NULL;
	BSTR path; g_pVBoxFuncs->pfnUtf8ToUtf16("IDE Controller", &path);
	edit->lpVtbl->GetMediumAttachment(edit, path, 1, 0, &attachment);
	g_pVBoxFuncs->pfnUtf16Free(path);

	if(attachment == NULL)
		return (jboolean)TRUE;

	BOOL ejected;
#ifdef VBHOOK_WIN
	attachment->lpVtbl->get_IsEjected(attachment, &ejected);
#else
	attachment->lpVtbl->GetIsEjected(attachment, &ejected);
#endif
	attachment->lpVtbl->Release(attachment);
	g_pVBoxFuncs->pfnProcessEventQueue(250);
	return (jboolean)ejected;
}

JNIEXPORT jlongArray JNICALL Java_vbhook_VBHook_tick_1vm(JNIEnv* env, jobject obj, jlong vbclient, jlong machine, jint mousedeltax, jint mousedeltay, jint mousedeltascroll, jint mouseclick, jintArray scancodes) {
	IVirtualBoxClient* vbclientvb = (IVirtualBoxClient*)vbclient;
	IMachine* machinevb = (IMachine*)machine;
	
	ISession* session = NULL;
#ifdef VBHOOK_WIN
	vbclientvb->lpVtbl->get_Session(vbclientvb, &session);
#else
	vbclientvb->lpVtbl->GetSession(vbclientvb, &session);
#endif
	machinevb->lpVtbl->LockMachine(machinevb, session, LockType_Shared);
	IConsole* console = NULL;
#ifdef VBHOOK_WIN
	session->lpVtbl->get_Console(session, &console);
#else
	session->lpVtbl->GetConsole(session, &console);
#endif
	if(console == NULL) {
		ISession_UnlockMachine(session);
		ISession_Release(session);
		g_pVBoxFuncs->pfnProcessEventQueue(250);
		return (*env)->NewLongArray(env, 0);
	}

	IMouse* mouse = NULL;
#ifdef VBHOOK_WIN
	console->lpVtbl->get_Mouse(console, &mouse);
#else
	console->lpVtbl->GetMouse(console, &mouse);
#endif
	mouse->lpVtbl->PutMouseEvent(mouse, mousedeltax, mousedeltay, mousedeltascroll, 0, mouseclick);
	mouse->lpVtbl->Release(mouse);

	IKeyboard* keyboard = NULL;
#ifdef VBHOOK_WIN
	ULONG sent_stuff;
	console->lpVtbl->get_Keyboard(console, &keyboard);
	size_t len = (*env)->GetArrayLength(env, scancodes);
	jint* arr = (*env)->GetIntArrayElements(env, scancodes, NULL);
	SAFEARRAY* sascancodes = g_pVBoxFuncs->pfnSafeArrayCreateVector(VT_I8, 0, len);
	g_pVBoxFuncs->pfnSafeArrayCopyInParamHelper(sascancodes, &arr, sizeof(jint));
	keyboard->lpVtbl->PutScancodes(keyboard, ComSafeArrayAsInParam(sascancodes), &sent_stuff);
	g_pVBoxFuncs->pfnSafeArrayDestroy(sascancodes);
#else
	PRUint32 sent_stuff;
	console->lpVtbl->GetKeyboard(console, &keyboard);
	keyboard->lpVtbl->PutScancodes(keyboard, (*env)->GetArrayLength(env, scancodes), (*env)->GetIntArrayElements(env, scancodes, NULL), &sent_stuff);
#endif
	keyboard->lpVtbl->Release(keyboard);

	IDisplay* display = NULL;
#ifdef VBHOOK_WIN
	console->lpVtbl->get_Display(console, &display);
	ULONG width, height, bitspp; LONG xorigin, yorigin; GuestMonitorStatus status;
#else
	console->lpVtbl->GetDisplay(console, &display);
	PRUint32 width, height, bitspp, status; PRInt32 xorigin, yorigin;
#endif

	if(display == NULL) {
		console->lpVtbl->Release(console);
		session->lpVtbl->UnlockMachine(session);
		session->lpVtbl->Release(session);
		g_pVBoxFuncs->pfnProcessEventQueue(250);
		return (*env)->NewLongArray(env, 0);
	}
	display->lpVtbl->GetScreenResolution(display, 0, &width, &height, &bitspp, &xorigin, &yorigin, &status);
	if(width == 0 || height == 0) {
		display->lpVtbl->Release(display);
		console->lpVtbl->Release(console);
		session->lpVtbl->UnlockMachine(session);
		session->lpVtbl->Release(session);
		g_pVBoxFuncs->pfnProcessEventQueue(250);
		return (*env)->NewLongArray(env, 0);
	}

	jlong array[] = {(jlong)width, (jlong)height, (jlong)display, (jlong)console, (jlong)session};
	jintArray retval = (*env)->NewLongArray(env, 5);
	(*env)->SetLongArrayRegion(env, retval, 0, 5, array);
	g_pVBoxFuncs->pfnProcessEventQueue(250);
	return retval;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_screenshot_1vm(JNIEnv* env, jobject obj, jlong display_ptr, jlong console_ptr, jlong session_ptr, jlong width, jlong height, jlong buf) {
	ISession* session = (ISession*)session_ptr;
	IDisplay* display = (IDisplay*)display_ptr;
	IConsole* console = (IConsole*)console_ptr;
	
	//TODO: ADD SCREENSHOT FUNCTIONALITY THAT WORKS!
	/*
#ifdef VBHOOK_WIN
	display->lpVtbl->TakeScreenShotToArray(display, 0, width, height, BitmapFormat_PNG, &array);
#else
	display->lpVtbl->TakeScreenShotToArray(display, 0, width, height, BitmapFormat_PNG, &array_size, &array);
#endif
*/

	display->lpVtbl->Release(display);
	console->lpVtbl->Release(console);
	session->lpVtbl->UnlockMachine(session);
	session->lpVtbl->Release(session);
	g_pVBoxFuncs->pfnProcessEventQueue(250);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_stop_1vm_1if_1exists(JNIEnv* env, jobject obj, jlong vb, jlong vbclient, jstring str){
	IMachine* vm = NULL;
	IVirtualBox* virtualbox = (IVirtualBox*)vb;
	IVirtualBoxClient* virtualbox_client = (IVirtualBoxClient*)vbclient;
#ifdef VBHOOK_WIN
	BSTR vbname; g_pVBoxFuncs->pfnUtf8ToUtf16((*env)->GetStringUTFChars(env, str, NULL), &vbname);
#else
	PRUnichar* vbname = (PRUnichar*)(*env)->GetStringChars(env, str, NULL);
#endif

	virtualbox->lpVtbl->FindMachine(virtualbox, vbname, &vm);
#ifdef VBHOOK_WIN
	g_pVBoxFuncs->pfnUtf16Free(vbname);
#endif
	if(vm) {
		ISession* session = NULL;
#ifdef VBHOOK_WIN
		virtualbox_client->lpVtbl->get_Session(virtualbox_client, &session);
#else
		virtualbox_client->lpVtbl->GetSession(virtualbox_client, &session);
#endif
		if(!session) return;
		vm->lpVtbl->LockMachine(vm, session, LockType_Shared);
		IConsole* con = NULL;
#ifdef VBHOOK_WIN
		session->lpVtbl->get_Console(session, &con);
#else
		session->lpVtbl->GetConsole(session, &con);
#endif
		if(con) {
			IProgress* prog = NULL;
			con->lpVtbl->PowerDown(con, &prog);
			if(prog) {
				prog->lpVtbl->WaitForCompletion(prog, -1);
				prog->lpVtbl->Release(prog);
			}
			con->lpVtbl->Release(con);
		}
		session->lpVtbl->UnlockMachine(session);

		vm->lpVtbl->Release(vm);
		session->lpVtbl->Release(session);
	}
	g_pVBoxFuncs->pfnProcessEventQueue(250);
}
