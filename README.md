<p align="center"><img src="https://raw.githubusercontent.com/y2k04/MCVmComputers/master/src/main/resources/assets/mcvmcomputers/icon.png" height="320"></p>

<a href="https://discord.gg/gNgaxZa4yX">Join the MCVmComputers Discord Server!</a>

<details>
	<summary>Donate to the original creator (<a href="https://github.com/Delta2Force">Delta2Force</a>)<br><sub><i>Note: Only accepts Bitcoin and BitcoinCash</i></sub></summary>
<i>BTC:</i> <code>3GubEkHV69gCkjWhRgRWYqWqyjcWW3gxFF</code><br><i>BCH:</i> <code>bitcoincash:qq6jttzlvgj68lvecnh75pt3znezj4vx6sysfvj3j5</code>
</details><hr>

<h3>Dependencies</h3>
<ul>
	<li>Minecraft 1.16.5</li>
	<ul>
		<li><a href="https://fabricmc.net/use">Fabric</a></li>
		<li><a href="https://curseforge.com/minecraft/mc-mods/fabric-api">Fabric API</a></li>
	</ul>
	<li><a href="https://www.virtualbox.org/wiki/Download_Old_Builds_6_1">VirtualBox 6.1</a></li>
</ul>

<h2>How to use</h2>

<h3>Ordering</h3>
<ol>
	<li>Craft an ordering tablet (as seen below)<br><img src="https://i.imgur.com/GtyPntY.png"></li>
	<li>Wait until it detects a satellite (they appear quite often)<br><img src="https://i.imgur.com/hWRK8wb.png"></li>
	<li>The rest is (hopefully) self-explanatory</li>
</ol>
<hr>

<h3>Creating a Hard Drive / Importing an ISO disk image</h3>
<p>Virtual Hard Drives are created by pressing the use button (right-click) while holding an hard drive in your hand. It doesn't matter if it has been assigned to a disk image or not as it will always open the menu. You can either use an existing hard drive (located in <code>.minecraft/vm_computers/vhds</code>) or create a new one.</p>
<p>ISO images are CDs / DVDs but as files. They are stored at <code>.minecraft/vm_computers/isos</code>. Once you place them in the folder, you can select them in the PC case menu.</p>
<hr>

<h3>Building the computer</h3>
<p>You will need the following items (which can be bought from the Ordering Tablet):</p>
<ul>
	<li>PC Case,</li>
	<li>Motherboard,</li>
	<li>RAM,</li>
	<li>GPU, and</li>
	<li>Monitor</li>
</ul>
<sub>Note: The keyboard and mouse are purely decorative (currently...) and you can start the VM without a hard drive or ISO inserted.</sub><br><br>
<p>To insert a hard drive, you will need to create one by right-clicking with a hard drive in your hand.<br>It doesn't matter if it is already assigned to a disk image or not.</p>
<ol>
	<li>Place down the case<br><img src="https://i.imgur.com/8Wgqtcb.png"></li>
	<li>Press the use button (right-click) on the case<br><img src="https://i.imgur.com/OPRi9xa.png"></li>
	<li>(Optional) Select an ISO image from the list</li>
	<li>Open the case<br><img src="https://i.imgur.com/sXYTRuc.png"><br><sub>Note: The buttons will be disabled if you do not have the item in your inventory.</sub></li>
	<li>The rest (hopefully) is self-explanatory</li>
</ol>
<hr>

<h3>Using the computer</h3>
<p>To turn on the computer, press the Power On button in the PC case menu. To interact with the computer, press the use button (right-click) on the monitor.<br><sub>Note: Pressing Alt+F4 will close the game, not the window you are on in the virtual machine.</sub><br><br>To stop interacting with the computer, press the keys mentioned in the top-right corner of the screen when interacting with the monitor.</p>
<hr>

<h2>Contributing</h2>
<p>If you'd like to contribute to this project, please fork the master branch, make your changes and create a pull request to have the changes reviewed. If you plan to contribute larger changes, please create a issue to discuss the major changes that you were planning to make.</p>
<p>I don't like rules, but to keep consistency and readability of the code please follow these guidelines:</p>
<ol>
	<li>Use lambda expressions when appropriate</li>
	<ul>
		<li>You'll find that it is faster to write <code>() -> {}</code>, just saying :)</li>
	</ul>
	<li>Try to follow the code style of the project</li>
	<ul>
		<li>Makes everyone's life so much easier when it comes to making changes and understanding the code</li>
	</ul>
	<li>Commit messages should describe the change well without being too short</li>
	<ul>
		<li>Where it says <i>'Update [insert file name here]'</i> should be replaced with a short overview of what was changed. Use the optional extended description to explain the specific changes that were made.</li>
	</ul>
	<li>When making a pull request, make sure that you have made all of the changes you have wanted to before posting the pull request.</li>
	<ul>
		<li>It gets messy when you create a pull request and continue to make changes after the fact.</li>
	</ul>
</ol>
