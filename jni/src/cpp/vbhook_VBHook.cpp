#include "vbhook_VBHook.h"
#include <algorithm>

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1vb_1client (JNIEnv* env, jobject obj) {
	return std::rand();
}

JNIEXPORT jlong JNICALL Java_vbhook_VBHook_create_1vb(JNIEnv* env, jobject obj, jlong lng) {
	return lng * 2;
}
