This folder is a bit messy, so let me explain some stuff here for you:

The code in src/ is the combined code of a JNI library which uses the VirtualBox SDK to communicate with it.
If you want to compile it, run the .sh scripts for Linux or Mac and the .bat scripts for Windows IN THE FOLLOWING ORDER

build_header -> build_library -> package

I apologize again for the weird structure as you need MSVC and compilers for Mac and Linux respectively. I tried to do it using CMake at first, but it proved to just slow me down so I did it like this.
