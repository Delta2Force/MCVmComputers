#!/bin/sh
cp -r bin src/java/vbhook-libs
jar cvf ../lib/vbhook.jar -C src/java vbhook/VBHook.class -C src/java vbhook-libs/libvbhook.so -C src/java vbhook-libs/vbhook.dll
rm -r src/java/vbhook-libs
