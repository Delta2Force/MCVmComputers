package mcvmcomputers.client.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.math.NumberUtils;
import org.virtualbox_6_1.AccessMode;
import org.virtualbox_6_1.DeviceType;
import org.virtualbox_6_1.IMedium;
import org.virtualbox_6_1.IProgress;
import org.virtualbox_6_1.MediumVariant;

import mcvmcomputers.MainInitializer;
import mcvmcomputers.item.ItemHarddrive;
import mcvmcomputers.item.ItemList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class GuiCreateHarddrive extends Screen{
	private TextFieldWidget hddSize;
	private String status;
	private State currentState = State.MENU;
	
	private static final char COLOR_CHAR = (char) (0xfeff00a7);
	
	public enum State{
		MENU,
		CREATE_NEW,
		SELECT_OLD
	}
	
	public GuiCreateHarddrive() {
		super(new TranslatableText("Create Harddrive"));
	}
	
	@Override
	public void init() {
		if(currentState == State.CREATE_NEW) {
			String s = "";
			if(hddSize != null) {
				s = hddSize.getText();
			}
			hddSize = new TextFieldWidget(this.font, this.width/2-150, this.height/2-10, 300, 20, "");
			hddSize.setText(s);
			hddSize.setChangedListener((st) -> hddSizeUpdate(st));
			this.children.add(hddSize);
			this.hddSizeUpdate(hddSize.getText());
			
			this.addButton(new ButtonWidget(this.width/2-100, this.height/2+50, 200, 20, "Create hard drive", (wdgt) -> createNew(wdgt)));
			this.addButton(new ButtonWidget(this.width - 60, this.height - 30, 50, 20, "Menu", (wdgt) -> switchState(State.MENU)));
		}else if(currentState == State.MENU) {
			this.addButton(new ButtonWidget(this.width/2 - 100, this.height/2 - 12, 200, 20, "Create new hard drive", (wdgt) -> switchState(State.CREATE_NEW)));
			this.addButton(new ButtonWidget(this.width/2 - 100, this.height/2 + 12, 200, 20, "Select / delete existing hard drive", (wdgt) -> switchState(State.SELECT_OLD)));
		}else {
			int lastY = 60;
			ArrayList<File> files = new ArrayList<>();
			for(File f : MainInitializer.vhdDirectory.listFiles()) {
				if(f.getName().endsWith(".vdi")) {
					files.add(f);
				}
			}
			files.sort(new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			for(File f : files) {
				this.addButton(new ButtonWidget(this.width/2 - 90, lastY, 180, 14, f.getName() + " | " + ((float)f.length()/1024f/1024f) + " MB used", (wdgt) -> selectOld(wdgt)));
				this.addButton(new ButtonWidget(this.width/2 + 92, lastY, 14, 14, "x", (wdgt) -> removevhd(f.getName())));
				lastY += 16;
			}
			
			this.addButton(new ButtonWidget(this.width - 60, this.height - 30, 50, 20, "Menu", (wdgt) -> switchState(State.MENU)));
		}
	}
	
	private void switchState(State newState) {
		this.buttons.clear();
		this.children.clear();
		currentState = newState;
		this.init();
	}
	
	private void selectOld(ButtonWidget wdgt) {
		String fileName = wdgt.getMessage().split(" | ")[0];
		ItemStack it = ItemHarddrive.createHardDrive(fileName);
		ServerPlayerEntity spe = minecraft.getServer().getPlayerManager().getPlayerList().get(0);
		spe.inventory.getInvStack(spe.inventory.getSlotWithStack(new ItemStack(ItemList.ITEM_NEW_HARDDRIVE))).decrement(1);
		spe.giveItemStack(it);
		minecraft.openScreen(null);
	}
	
	private void createNew(ButtonWidget wdgt) {
		if(!status.startsWith(COLOR_CHAR + "c")) {
			Long size = Long.parseLong(hddSize.getText())*1024L*1024L;
			int i = MainInitializer.latestVHDNum;
			File vhd = new File(MainInitializer.vhdDirectory, "vhd" + i + ".vdi");
			
			IMedium hdd = MainInitializer.vb.createMedium("vdi", vhd.getPath(), AccessMode.ReadWrite, DeviceType.HardDisk);
			IProgress pr = hdd.createBaseStorage(size, Arrays.asList(MediumVariant.Standard));
			pr.waitForCompletion(-1);
			
			ItemStack it = ItemHarddrive.createHardDrive(vhd.getName());
			ServerPlayerEntity spe = minecraft.getServer().getPlayerManager().getPlayerList().get(0);
			spe.inventory.getInvStack(spe.inventory.getSlotWithStack(new ItemStack(ItemList.ITEM_NEW_HARDDRIVE))).decrement(1);
			spe.giveItemStack(it);
			
			try {
				MainInitializer.increaseVHDNum();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			minecraft.openScreen(null);
		}
	}
	
	private void removevhd(String name) {
		new File(MainInitializer.vhdDirectory, name).delete();
		minecraft.openScreen(null);
	}
	
	private void hddSizeUpdate(String in) {
		if(NumberUtils.isDigits(in)) {
			long i = 0;
			try {
				i = Long.parseLong(in);
			}catch(NumberFormatException e) {
				status = COLOR_CHAR + "cError while parsing number. Number too big?";
				return;
			}
			if(i > 0) {
				if(i*1024*1024 < 0) { //Buffer overflow
					status = COLOR_CHAR + "cNumber too big. (max " + Long.MAX_VALUE/1024L/1024L + ")";
					return;
				}
				
				if(i*1024*1024 >= new File(".").getFreeSpace()) {
					status = COLOR_CHAR + "cYou don't have enough space for that.";
					return;
				}else {
					status = COLOR_CHAR + "aValid virtual hard drive size. (Check your free space)";
					return;
				}
			}else {
				status = COLOR_CHAR + "cToo small. (must be > 0)";
				return;
			}
		}else {
			status = COLOR_CHAR + "cNot a number.";
			return;
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		if(currentState == State.CREATE_NEW) {
			this.font.draw(status, this.width/2-150, this.height/2+13, -1);
			this.font.draw("Harddrive size: (in MB) (1024 MB = 1 GB)", this.width/2-150, this.height/2-20, -1);
			this.hddSize.render(mouseX, mouseY, delta);
		}else if(currentState == State.MENU) {
			this.font.draw("Set up your new hard drive", this.width/2 - this.font.getStringWidth("Set up your new hard drive")/2, this.height/2 - 30, -1);
		}
		super.render(mouseX, mouseY, delta);
	}

}
