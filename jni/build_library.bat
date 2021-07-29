@echo off

REM Download VirtualBox SDK if it isn't present
IF exist "sdk\" ( echo VirtualBox found! ) else ( curl -O https://download.virtualbox.org/virtualbox/6.1.22/VirtualBoxSDK-6.1.22-144080.zip && tar -xf VirtualBoxSDK-6.1.22-144080.zip && rm VirtualBoxSDK-6.1.22-144080.zip )

REM Detect MSVC
cl >nul 2>&1 && (
	echo MSVC found. Make sure you're using the x64 Command Prompt!!
) || (
	echo This batch script needs to be executed within the Visual Studio x64 Command Prompt.
	exit /b
)

REM Compile & Link
cl /c /Isdk/bindings/c/include /Isdk/bindings/mscom/include /Isdk/bindings/c/glue /Fobin/VBoxCAPIGlue.obj sdk/bindings/c/glue/VBoxCAPIGlue.c 
cl /c /Isdk/bindings/c/include /Isdk/bindings/mscom/include /Isdk/bindings/c/glue /Fobin/VirtualBox_i.obj sdk/bindings/mscom/lib/VirtualBox_i.c
cl /c /I"%JAVA_HOME%/include" /I"%JAVA_HOME%/include/win32" /Isdk/bindings/c/include /Isdk/bindings/mscom/include /Isdk/bindings/c/glue /Fobin/vbhook.obj src/c/vbhook_VBHook.c
link /dll /OUT:bin/vbhook.dll bin/VirtualBox_i.obj bin/VBoxCAPIGlue.obj bin/vbhook.obj oleaut32.lib

REM Remove Residue
del bin\*.obj 2>NUL
del bin\*.lib 2>NUL
del bin\*.pdb 2>NUL
del bin\*.exp 2>NUL
