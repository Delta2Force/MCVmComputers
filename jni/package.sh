#!/bin/sh
cp -r bin src/java/vbhook-libs
jar cvf ../lib/vbhook.jar -C src/java vbhook/VBHook.class vbhook-libs/libvbhook.so vbhook-libs/vbhook.dll
rm -r src/java/vbhook-libs
