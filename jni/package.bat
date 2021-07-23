@echo off
xcopy "bin" "src/java/vbhook-libs" /s /e /y /i
jar cvf ../lib/vbhook.jar -C src/java vbhook/VBHook.class -C src/java vbhook-libs/libvbhook.so -C src/java vbhook-libs/vbhook.dll
rd /s /q "src/java/vbhook-libs"
