@echo off
cl /c /Isdk/bindings/c/include /Isdk/bindings/mscom/include /Isdk/bindings/c/glue /Fobin/VBoxCAPIGlue.obj sdk/bindings/c/glue/VBoxCAPIGlue.c 
cl /c /Isdk/bindings/c/include /Isdk/bindings/mscom/include /Isdk/bindings/c/glue /Fobin/VirtualBox_i.obj sdk/bindings/mscom/lib/VirtualBox_i.c
cl /c /I"%JAVA_HOME%/include" /I"%JAVA_HOME%/include/win32" /Isdk/bindings/c/include /Isdk/bindings/mscom/include /Isdk/bindings/c/glue /Fobin/vbhook.obj src/c/vbhook_VBHook.c
link /dll /OUT:bin/vbhook.dll bin/VirtualBox_i.obj bin/VBoxCAPIGlue.obj bin/vbhook.obj
del bin/*.o
