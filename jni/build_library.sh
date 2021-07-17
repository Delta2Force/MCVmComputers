#!/bin/sh
mkdir bin
g++ -c -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux src/cpp/vbhook_VBHook.cpp -o bin/vbhook.o
g++ -shared -fPIC -o bin/libvbhook.so bin/vbhook.o -lc
rm bin/vbhook.o
