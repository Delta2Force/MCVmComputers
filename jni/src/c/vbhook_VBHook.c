#include "vbhook_VBHook.h"
#include "VBoxCAPIGlue.h"
#include "jni.h"
#include <stdio.h>
#include <stdlib.h>

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1vb_1client (JNIEnv* env, jobject obj) {
	IVirtualBoxClient* vboxClient = NULL;
	g_pVBoxFuncs->pfnClientInitialize(NULL, &vboxClient);
	return (jlong)vboxClient;
}

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1vb(JNIEnv* env, jobject obj, jlong lng) {
	IVirtualBoxClient* vboxClient = (IVirtualBoxClient*) lng;
	IVirtualBox* vbox = NULL;
	IVirtualBoxClient_get_VirtualBox(vboxClient, &vbox);
	return (jlong)vbox;
}

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1session(JNIEnv* env, jobject obj, jlong lng) {
	ISession* session = NULL;
	IVirtualBoxClient* vboxClient = (IVirtualBoxClient*) lng;
	IVirtualBoxClient_get_Session(vboxClient, &session);
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

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1vb_1client(JNIEnv* env, jobject obj, jlong lng) {
	if(lng) IVirtualBoxClient_Release((IVirtualBoxClient*)lng);
}

JNIEXPORT jboolean JNICALL Java_vbhook_VBHook_init_1glue(JNIEnv* env, jobject obj, jstring vbox_home) {
	setenv("VBOX_APP_HOME", (*env)->GetStringUTFChars(env, vbox_home, NULL), 1);
	return !VBoxCGlueInit();
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
	PRUnichar* vbname = (PRUnichar*)(*env)->GetStringChars(env, name, NULL);
	PRUnichar* vbos_type = (PRUnichar*)(*env)->GetStringChars(env, os_type, NULL);

	IVirtualBox_FindMachine(virtualbox, vbname, &vm);
	if(!vm) {
		//create if not found
		BSTR empty; g_pVBoxFuncs->pfnUtf8ToUtf16("", &empty);
		BSTR options; g_pVBoxFuncs->pfnUtf8ToUtf16("forceOverwrite=1", &options);
		virtualbox->lpVtbl->CreateMachine(virtualbox, empty, vbname, 0, NULL, vbos_type, options, &vm);
		g_pVBoxFuncs->pfnUtf16Free(empty); g_pVBoxFuncs->pfnUtf16Free(options);
		IVirtualBox_RegisterMachine(virtualbox, vm);
	}
	return (long)vm;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_vm_1values(JNIEnv* env, jobject obj, jlong session, jlong vb, jlong vm, jlong vram, jlong mem, jlong cpu, jstring hdd, jstring iso){
	ISession* sessionvb = (ISession*)session;
	IVirtualBox* vbvb = (IVirtualBox*)vb;
	IMachine* vmvb = (IMachine*)vm;
	
	IMachine_LockMachine(vmvb, sessionvb, LockType_Write);
	IMachine* edit = NULL;
	ISession_GetMachine(sessionvb, &edit);
	IMachine_SetMemorySize(edit, mem);
	IMachine_SetCPUCount(edit, cpu);

	IGraphicsAdapter* adapter = NULL;
	IMachine_GetGraphicsAdapter(edit, &adapter);
	IGraphicsAdapter_SetAccelerate2DVideoEnabled(adapter, PR_TRUE);
	IGraphicsAdapter_SetAccelerate3DEnabled(adapter, PR_FALSE);
	IGraphicsAdapter_SetVRAMSize(adapter, vram);
	IGraphicsAdapter_Release(adapter); //we're done with it
	
	BSTR storage_controller_name;
	g_pVBoxFuncs->pfnUtf8ToUtf16("IDE Controller", &storage_controller_name);

	IMachine_RemoveStorageController(edit, storage_controller_name);
	IStorageController* storage_controller = NULL;
	IMachine_AddStorageController(edit, storage_controller_name, StorageBus_IDE, &storage_controller);
	IStorageController_Release(storage_controller);
	
	if((*env)->GetStringLength(env, hdd) > 0) {
		IMedium* hdd_medium = NULL;
		IVirtualBox_OpenMedium(vbvb, (PRUnichar*)(*env)->GetStringChars(env, hdd, NULL), DeviceType_HardDisk, AccessMode_ReadWrite, PR_TRUE, &hdd_medium);
		if(hdd_medium) {
			IMachine_AttachDevice(edit, storage_controller_name, 0, 0, DeviceType_HardDisk, hdd_medium);
			IMedium_Release(hdd_medium);
		}else{
			IMachine_AttachDevice(edit, storage_controller_name, 0, 0, DeviceType_HardDisk, NULL);
		}
	}
	
	if((*env)->GetStringLength(env, iso) > 0) {
		IMedium* iso_medium = NULL;
		IVirtualBox_OpenMedium(vbvb, (PRUnichar*)(*env)->GetStringChars(env, iso, NULL), DeviceType_DVD, AccessMode_ReadOnly, PR_TRUE, &iso_medium);
		IMachine_AttachDevice(edit, storage_controller_name, 1, 0, DeviceType_DVD, iso_medium);
		IMedium_Release(iso_medium);
	}else{
		IMachine_AttachDevice(edit, storage_controller_name, 1, 0, DeviceType_DVD, NULL);
	}

	IMachine_SaveSettings(edit);
	ISession_UnlockMachine(sessionvb);
	IMachine_Release(edit);
	g_pVBoxFuncs->pfnUtf16Clear(storage_controller_name);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_start_1vm(JNIEnv* env, jobject obj, jlong session, jlong vm) {
	ISession* sessionvb = (ISession*)session;
	IMachine* vmvb = (IMachine*)vm;
	
	BSTR headless; g_pVBoxFuncs->pfnUtf8ToUtf16("headless", &headless);
	IProgress* progress = NULL;
	vmvb->lpVtbl->LaunchVMProcess(vmvb, sessionvb, headless, 0, NULL, &progress);
	IProgress_WaitForCompletion(progress, -1);
	IProgress_Release(progress);

	g_pVBoxFuncs->pfnUtf16Free(headless);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_stop_1vm(JNIEnv* env, jobject obj, jlong session) {
	IConsole* console = NULL;
	IProgress* progress = NULL;

	ISession* sessionvb = (ISession*)session;
	ISession_GetConsole(sessionvb, &console);
	if(console == NULL) return;
	IConsole_PowerDown(console, &progress);
	IProgress_WaitForCompletion(progress, -1);
	IProgress_Release(progress);

	IConsole_Release(console);
	sessionvb->lpVtbl->UnlockMachine(sessionvb);
}

JNIEXPORT jboolean JNICALL Java_vbhook_VBHook_vm_1powered_1on(JNIEnv* env, jobject obj, jlong machine) {
	IMachine* machinevb = (IMachine*)machine;
	PRUint32 state;
	machinevb->lpVtbl->GetState(machinevb, &state);

	return state != MachineState_PoweredOff;
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_create_1hdd(JNIEnv* env, jobject obj, jlong vb, jlong size, jstring format, jstring path) {
	IMedium* medium = NULL;
	IVirtualBox_CreateMedium((IVirtualBox*)vb, (PRUnichar*)(*env)->GetStringChars(env, format, NULL), (PRUnichar*)(*env)->GetStringChars(env, path, NULL), AccessMode_ReadWrite, DeviceType_HardDisk, &medium);
	
	IProgress* progress = NULL;
	PRUint32 variant = MediumVariant_Standard;
	medium->lpVtbl->CreateBaseStorage(medium, size, 1, &variant, &progress);
	IProgress_WaitForCompletion(progress, -1);

	IMedium_Release(medium);
	IProgress_Release(progress);
}

JNIEXPORT jbyteArray JNICALL Java_vbhook_VBHook_tick_1vm(JNIEnv* env, jobject obj, jlong vbclient, jlong machine, jint mousedeltax, jint mousedeltay, jint mousedeltascroll, jint mouseclick, jintArray scancodes) {
	IVirtualBoxClient* vbclientvb = (IVirtualBoxClient*)vbclient;
	IMachine* machinevb = (IMachine*)machine;

	ISession* session = NULL;
	IVirtualBoxClient_GetSession(vbclientvb, &session);
	IMachine_LockMachine(machinevb, session, LockType_Shared);
	IConsole* console = NULL;
	ISession_GetConsole(session, &console);
	if(console == NULL) {
		ISession_UnlockMachine(session);
		ISession_Release(session);
		return (*env)->NewByteArray(env, 0);
	}

	IMouse* mouse = NULL;
	IConsole_GetMouse(console, &mouse);
	IMouse_PutMouseEvent(mouse, mousedeltax, mousedeltay, mousedeltascroll, 0, mouseclick);
	IMouse_Release(mouse);

	IKeyboard* keyboard = NULL;
	IConsole_GetKeyboard(console, &keyboard);
	PRUint32 sent_stuff;
	keyboard->lpVtbl->PutScancodes(keyboard, (*env)->GetArrayLength(env, scancodes), (*env)->GetIntArrayElements(env, scancodes, NULL), &sent_stuff);
	IKeyboard_Release(keyboard);

	IDisplay* display = NULL;
	IConsole_GetDisplay(console, &display);
	PRUint32 width, height, bitspp, status; PRInt32 xorigin, yorigin;
	IDisplay_GetScreenResolution(display, 0, &width, &height, &bitspp, &xorigin, &yorigin, &status);
	PRUint8* array = NULL;
	PRUint32 array_size = 0;
	display->lpVtbl->TakeScreenShotToArray(display, 0, width, height, BitmapFormat_RGBA, &array_size, &array);
	IDisplay_Release(display);

	IConsole_Release(console);
	ISession_UnlockMachine(session);
	ISession_Release(session);

	jbyteArray retval = (*env)->NewByteArray(env, array_size);
	(*env)->SetByteArrayRegion(env, retval, 0, array_size, (const jbyte*)array);
	return retval;
}
