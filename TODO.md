# To Do

| Feature | Progress | Notes |
| --- | --- | --- |
| Pick up assembled PCs | Completed, version 1.4 |     |
| Big TV | Completed, version 1.4 | Allow many players to watch at the same time without overcrowding a tiny monitor |
| ✨ Gamer Snacks ✨ |     | Pizza, drinks, cans, etc... THE EDIBLES! |
| World interaction |     | Activate redstone circuits; connect to a router (block) via "ethernet" |
| Accessories |     | Headphones, mice, microphone, desk pad, mouse pad, RGB versions of them |
| Chairs |     | Generic chair, RGB, "Secret Lab"-style, and office chairs |
| Multi-core support (VirtualBox) |     | Currently limited to 1 core |
| VMware support |     |     |
| QEMU support |     | Not sure whether there is a Java library for it. |
| PCEM support |     |     |
| DOSBox support |     |     |
| Hyper-V support |     | Would have to read into whether there is a Java library to interface with the Hyper-V Manager... 🤔 |
| Floppy disk item + drive |     | Put .img files as nbt data in an item and click on a PC to insert it into the drive |
| CD / DVD item + Drive |     | Same as above, but for .iso files |
| Assign monitor, keyboard, mouse and PC to a single virtual machine |     | (unconfirmed issue) You can currently see the same virtual machine on every single monitor you spawn and is not specific to a local PC, keyboard or mouse. |
| Multi-VM support |     | I guess put some limits such as checking what the host CPU usage is at, as well as checking the memory usage. |
| Add sound effects |     | Such as case fan noises |
| Multi-monitor support |     | You can only use one at the moment |
| USB item |     | One for storing data and one for installing VirtualBox Tools (graphics acceleration) |
| Website for project |     | Will make a proper website once it is in a playable and stable state |
| Increase GPU memory (for newer OS') |     | You can barely run Windows 10 in VirtualBox because of 1. Low memory, 2. Underpowered virtual CPU and 3. Lack of GPU memory. |
| Cables |     | Only allow a certain radius for stuff to be connected to the computer, could repurpose the lead? |
| Server racks |     | Linus Tech Tips style rack (his personal rig is a literal server rack) |

Note to self:
Try this when interfacing with VMWare and other virtualisation software that doesn't have a Java API
https://github.com/java-native-access/jna