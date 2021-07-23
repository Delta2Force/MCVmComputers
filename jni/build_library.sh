#!/bin/sh

if [ ! -d sdk ]; then
	curl -O https://download.virtualbox.org/virtualbox/6.1.22/VirtualBoxSDK-6.1.22-144080.zip
	unzip VirtualBoxSDK-6.1.22-144080.zip
	rm VirtualBoxSDK-6.1.22-144080.zip
fi

JAVA_DIR=""
if [ -z ${JAVA_HOME+x} ]; then
	JAVA_DIR=$(cd $(dirname $(realpath /usr/bin/java)) && cd .. && pwd)
else
	JAVA_DIR=${JAVA_HOME}
fi

if [ ! -d bin ]; then
	mkdir bin
fi

gcc -g -Wall -c -fPIC -Isdk/bindings/c/include -Isdk/bindings/xpcom/include -Isdk/bindings/c/glue sdk/bindings/c/glue/VBoxCAPIGlue.c -o bin/VBoxCAPIGlue.o
gcc -g -Wall -c -fPIC -Isdk/bindings/c/include -Isdk/bindings/xpcom/include -Isdk/bindings/c/glue sdk/bindings/xpcom/lib/VirtualBox_i.c -o bin/VirtualBox_i.o
gcc -g -Wall -c -fPIC -I$JAVA_DIR/include -I$JAVA_DIR/include/linux -Isdk/bindings/c/include -Isdk/bindings/xpcom/include -Isdk/bindings/c/glue src/c/vbhook_VBHook.c -o bin/vbhook.o
gcc -g -Wall -shared -fPIC -o bin/libvbhook.so bin/VirtualBox_i.o bin/VBoxCAPIGlue.o bin/vbhook.o -lc
rm bin/*.o
