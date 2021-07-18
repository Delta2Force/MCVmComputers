#include "vbhook_VBHook.h"
#include "VBoxCAPIGlue.h"

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

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1vb(JNIEnv* env, jobject obj, jlong lng) {
	IVirtualBox_Release((IVirtualBox*)lng);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_free_1vb_1client(JNIEnv* env, jobject obj, jlong lng) {
	IVirtualBoxClient_Release((IVirtualBoxClient*)lng);
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_init_1glue(JNIEnv* env, jobject obj) {
	VBoxCGlueInit();
}

JNIEXPORT void JNICALL Java_vbhook_VBHook_terminate_1glue(JNIEnv* env, jobject obj) {
	VBoxCGlueTerm();
}
