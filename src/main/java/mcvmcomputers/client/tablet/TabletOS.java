package mcvmcomputers.client.tablet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.glfw.GLFW;

import io.netty.buffer.Unpooled;
import mcvmcomputers.client.ClientMod;
import mcvmcomputers.client.entities.model.OrderingTabletModel;
import mcvmcomputers.item.ItemList;
import mcvmcomputers.item.OrderableItem;
import mcvmcomputers.networking.PacketList;
import mcvmcomputers.sound.SoundList;
import mcvmcomputers.sound.TabletSoundInstance;
import mcvmcomputers.utils.MVCUtils;
import mcvmcomputers.utils.TabletOrder.OrderStatus;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class TabletOS {
	//Rendering variables
	public NativeImage renderedImage;
	public NativeImageBackedTexture nibt;
	public Identifier textureIdentifier;
	public ByteArrayInputStream byteArrayInputStream;
	
	//Shop variables
	private float shopPx; //OffsetX, kind of like a camera
	private float shopPy; //OffsetY, ^
	private float shopGradientEndValue = 1f;
	private int shopIndex; //used for first page
	private int shopExtraIndex; //used for everything but first page
	private ShopState shopState;
	private ShopState renderExtraShopState; //for transitions
	private boolean arrowDownPressed = false;    //
	private boolean arrowUpPressed = false;		 // Used for 'tapping' input.
	private boolean arrowLeftPressed = false;	 // (e.g. GetKeyDown in Unity)
	private boolean arrowRightPressed = false;	 //
	private SoundInstance shopIntroSound;
	private SoundInstance shopMusicSound;
	private SoundInstance shopOutroSound;
	private ArrayList<OrderableItem> shoppingCart;
	private static final List<OrderableItem> PC_PARTS = Arrays.asList(ItemList.PC_CASE, ItemList.PC_CASE_SIDEPANEL, ItemList.ITEM_MOTHERBOARD, ItemList.ITEM_MOTHERBOARD64, ItemList.ITEM_RAM64M, ItemList.ITEM_RAM128M, ItemList.ITEM_RAM256M, ItemList.ITEM_RAM512M, ItemList.ITEM_RAM1G, ItemList.ITEM_RAM2G, ItemList.ITEM_RAM4G, ItemList.ITEM_CPU2, ItemList.ITEM_CPU4, ItemList.ITEM_CPU6, ItemList.ITEM_GPU, ItemList.ITEM_HARDDRIVE);
	private static final List<OrderableItem> PERIPHERALS = Arrays.asList(ItemList.ITEM_KEYBOARD, ItemList.ITEM_MOUSE, ItemList.ITEM_CRTSCREEN, ItemList.ITEM_FLATSCREEN, ItemList.ITEM_WALLTV);
	private BufferedImage lastShopImage;
	
	//Delivery variables
	private float chestX;
	private float chestY;
	private boolean drawChest;
	private SoundInstance displayOrderMusicSound;
	
	//Radar variables
	private SoundInstance radarSound;
	private ArrayList<Float> radarRadius; //radius of circles when looking at radar
	private float totalTimeRadar;
	private BufferedImage lastRadarImage; //last rendered image for transition from radar to store
	private boolean satelliteVisible = false;
	public final OrderingTabletModel orderingTabletModel = new OrderingTabletModel();
	
	//General variables
	private float deltaTime;
	private long lastDeltaTimeTime;
	public boolean tabletOn = false;
	private final Font font;
	public State tabletState = State.LOOKING_FOR_SATELLITE;
	private MinecraftClient mcc = MinecraftClient.getInstance();
	
	public TabletOS() throws FontFormatException, IOException {
		radarSound = new TabletSoundInstance(SoundList.RADAR_SOUND);
		shopIntroSound = new TabletSoundInstance(SoundList.SHOPINTRO_SOUND);
		shopOutroSound = new TabletSoundInstance(SoundList.SHOPOUTRO_SOUND);
		shopMusicSound = PositionedSoundInstance.master(SoundEvents.MUSIC_DISC_FAR, 0.6f, 0.2f);
		displayOrderMusicSound = PositionedSoundInstance.master(SoundEvents.MUSIC_DISC_STRAD, 0.6f, 0.2f);
		font = Font.createFont(Font.PLAIN, mcc.getResourceManager().getResource(new Identifier("mcvmcomputers", "font/tabletfont.ttf")).getInputStream());
		radarRadius = new ArrayList<Float>();
	}
	
	public void tabletTakenOut() {
		radarRadius.clear();
		totalTimeRadar = 0;
		radarRadius.add(0f);
		if(tabletState != State.SHOP && tabletState != State.DISPLAY_ORDER) {
			mcc.getSoundManager().play(radarSound);
			tabletState = State.LOOKING_FOR_SATELLITE;
		}
	}
	public void tabletUnequipped() {
		lastDeltaTimeTime = 0;
		deltaTime = 0;
		if(tabletState == State.LOOKING_FOR_SATELLITE) {
			mcc.getSoundManager().stop(radarSound);
		}else if(tabletState == State.SHOP_INTRO) {
			mcc.getSoundManager().stop(shopIntroSound);
			tabletState = State.SHOP;
		}else if(tabletState == State.SHOP_OUTRO) {
			mcc.getSoundManager().stop(shopOutroSound);
			mcc.getSoundManager().stop(shopMusicSound);
			tabletState = State.DISPLAY_ORDER;
		}else if(tabletState == State.SHOP) {
			mcc.getSoundManager().stop(shopMusicSound);
		}else if(tabletState == State.DISPLAY_ORDER) {
			mcc.getSoundManager().stop(displayOrderMusicSound);
		}
	}
	
	public void render() {
		if(lastDeltaTimeTime == 0) {
			lastDeltaTimeTime = System.currentTimeMillis();
		}else {
			long now = System.currentTimeMillis();
			long diff = now - lastDeltaTimeTime;
			lastDeltaTimeTime = now;
			
			deltaTime = (float) diff / 1000f;
		}	
		
		BufferedImage bi = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		
		if(tabletState == State.LOOKING_FOR_SATELLITE) {
			if(mcc.world != null) {
				totalTimeRadar += deltaTime;
				if(totalTimeRadar >= 4.396f) {
					totalTimeRadar = 0;
					radarRadius.add(0f);
				}
				ArrayList<Float> removeIndices = new ArrayList<Float>();
				for(int i = 0;i<radarRadius.size();i++) {
					float f = radarRadius.get(i)+deltaTime*50;
					radarRadius.set(i, f);
					if(f >= 420) {
						removeIndices.add(f);
					}
				}
				for(Float f : removeIndices) {
					radarRadius.remove(f);
				}
				removeIndices.clear();
				float satelliteAngle = ((float)mcc.world.getSkyAngle(deltaTime)*5f);
				satelliteAngle %= 1f;
				boolean canSee = satelliteAngle < 0.22249603 || satelliteAngle > 0.78432274;
				satelliteVisible = canSee;
				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, 0, 256, 256);
				g2d.setColor(Color.white);
				g2d.fillRect(64, 168, 128, 1);
				g2d.setFont(font.deriveFont(20f));
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				if(canSee) {
					g2d.drawString("Satellite found!", 78, 40);
					g2d.drawString("Connect to 'store' using ENTER", 26, 54);
					
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setColor(Color.gray);
					g2d.fillOval((int)(128 + (96*Math.cos((satelliteAngle*6.3)-1.65))), (int)(168 + (32*Math.sin((satelliteAngle*6.3)-1.65))), 16, 16);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				}else {
					g2d.drawString("Scanning for Satellite...", 54, 40);
					g2d.drawString("Please check throughout the day", 17, 54);
				}
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.gray);
				for(Float f : radarRadius) {
					g2d.drawOval(Math.round(128 - f/2), Math.round(160 - f/2), Math.round(f), Math.round(f));
				}
				lastRadarImage = bi;
			}
		}else if(tabletState == State.SHOP_INTRO) {
			if(!mcc.getSoundManager().isPlaying(shopIntroSound)) {
				mcc.getSoundManager().play(shopIntroSound);
			}
			g2d.drawImage(lastRadarImage, 0, 0, null);
			totalTimeRadar += deltaTime;
			float firstPercentage = Math.min(1f, totalTimeRadar/0.091f);
			float secondPercentage = Math.min(1f, (totalTimeRadar-0.091f)/0.122f);
			float thirdPercentage = Math.min(1f, (totalTimeRadar-0.213f)/0.108f);
			float fourthPercentage = Math.min(1f, (totalTimeRadar-0.321f)/0.14f);
			float fifthPercentage = Math.min(1f, (totalTimeRadar-0.461f)/0.103f);
			float[] percentages = new float[] {firstPercentage, secondPercentage, thirdPercentage, fourthPercentage, fifthPercentage};
			if(totalTimeRadar > 2) {
				mcc.getSoundManager().stop(shopIntroSound);
				lastRadarImage = null;
				shoppingCart = new ArrayList<OrderableItem>();
				tabletState = State.SHOP;
				shopState = ShopState.MENU;
				shopGradientEndValue = 1f;
				shopPy = 500;
				shopPx = 0;
			}
				for(int y = 0;y<5;y++) {
					float val = percentages[y];
					int by = (int) ((y*52)+(52*(val-1f)));
					for(int yy = 0;yy<52;yy++) {
						float c = (((y*52)+yy)/260f)*.9f;
						float vav = val;
						if(val < 0) { vav = 0f; }
						g2d.setColor(new Color(0f, Math.min(1f, c+.1f), Math.min(1f, c+.1f), vav));
						g2d.fillRect(0, by+yy, 256, 1);
						if(y == 4) {
						    g2d.setColor(new Color(1f,1f,1f));
							g2d.setFont(font.deriveFont(60f));
							g2d.drawString("Store", 75, 140+(40*(val-1f)));
						}
					}
				}
		}else if(tabletState == State.SHOP) {
			if(!mcc.getSoundManager().isPlaying(shopMusicSound) && tabletOn) {
				mcc.getSoundManager().play(shopMusicSound);
			}
			shopGradientEndValue = MVCUtils.lerp(shopGradientEndValue, 0.3f, deltaTime*2.5f);
			
			GradientPaint gp = new GradientPaint(0, 0, new Color(0f,0.1f,0.1f), 0, 256, new Color(0f,shopGradientEndValue,shopGradientEndValue));
			Paint p = g2d.getPaint();
			g2d.setPaint(gp);
			g2d.fillRect(0, 0, 256, 256);
			g2d.setPaint(p);
			g2d.setFont(font.deriveFont(60f));
			g2d.drawString("Store", 75-shopPx, 640-shopPy);
			
			g2d.setFont(font.deriveFont(32f));
			g2d.drawString("VMcorp Store", 4-shopPx, 20-shopPy);
			
			g2d.setFont(font.deriveFont(46f));
			g2d.drawString("Parts", 24-shopPx, 60-shopPy);
			g2d.drawString("Peripherals", 24-shopPx, 90-shopPy);
			g2d.drawString("Shopping cart", 24-shopPx, 120-shopPy);
			
			g2d.drawString(">", 6-shopPx, (60+(30*shopIndex))-shopPy);
			
			g2d.setFont(font.deriveFont(16f));
			g2d.drawString("(including screens)", 90-shopPx, 98-shopPy);
			g2d.drawString("(" + shoppingCart.size() + " items)", 200-shopPx, 130-shopPy);
			g2d.drawString("Navigate using arrow keys,", 58-shopPx, 140-shopPy);
			g2d.drawString("select with enter.", 83-shopPx, 150-shopPy);
			g2d.drawString("Items are sponsored by Solar System ZA-83. By", 4-shopPx, 210-shopPy);
			g2d.drawString("purchasing from this store you agree to", 4-shopPx, 220-shopPy);
			g2d.drawString("transfer ownership of your planet to Solar", 4-shopPx, 230-shopPy);
			g2d.drawString("System ZA-83 and the corporations residing in", 4-shopPx, 240-shopPy);
			g2d.drawString("it including VMcorp if requested.", 4-shopPx, 250-shopPy);
			
			if(shopState == ShopState.PC_PARTS || renderExtraShopState == ShopState.PC_PARTS) {
				int offY = 20;
				for(OrderableItem oi : PC_PARTS) {
					int offX = 0;
					g2d.setFont(font.deriveFont(23f)); offY += 20;
					g2d.drawString(oi.getName().getString(), (270+offX)-shopPx, offY-shopPy);
					g2d.setFont(font.deriveFont(16f)); offY += 10; offX = 10;
					int inCart = 0;
					for(OrderableItem io : shoppingCart) {
						if(io.equals(oi)) {
							inCart++;
						}
					}
					if(inCart > 0) {
						g2d.drawString(oi.getPrice() + " Iron Ingots | " + inCart + " in cart", (270+offX)-shopPx, offY-shopPy);
					}else {
						g2d.drawString(oi.getPrice() + " Iron Ingots", (270+offX)-shopPx, offY-shopPy);
					}
				}
				g2d.setFont(font.deriveFont(32f));
				g2d.drawString("PC parts", 260-shopPx, 20-shopPy);
				g2d.setFont(font.deriveFont(23f));
				int ry = (40+(shopExtraIndex*30));
				float targetShopPy = shopPy;
				if(ry + 60 > shopPy+256) {
					targetShopPy = ry - 186;
				}
				if(ry-50 < shopPy) {
					targetShopPy = ry - 60;
				}
				if(targetShopPy < 0) {
					targetShopPy = 0;
				}else if(targetShopPy > offY - 248) {
					targetShopPy = offY - 248;
				}
				if(shopState == ShopState.PC_PARTS) {
					shopPx = MVCUtils.lerp(shopPx, 256, deltaTime*5f);
					shopPy = MVCUtils.lerp(shopPy, targetShopPy, deltaTime*5f);
				}
				g2d.drawString(">", 260-shopPx, ry-shopPy);
				g2d.setFont(font.deriveFont(16f));
				g2d.drawString("Navigate using arrows,", 390-shopPx, 15-shopPy);
				g2d.drawString("Select using enter,", 409-shopPx, 25-shopPy);
				g2d.drawString("Exit using left", 432-shopPx, 35-shopPy);
			}else if(shopState == ShopState.SHOPPING_CART || renderExtraShopState == ShopState.SHOPPING_CART) {
				int offY = 30;
				int sum = 0;
				for(OrderableItem oi : shoppingCart) {
					int offX = 0;
					g2d.setFont(font.deriveFont(23f)); offY += 20;
					g2d.drawString(oi.getName().getString(), (270+offX)-shopPx, offY-shopPy);
					g2d.setFont(font.deriveFont(16f)); offY += 10; offX = 10;
					g2d.drawString(oi.getPrice() + " Iron Ingots | enter to remove", (270+offX)-shopPx, offY-shopPy);
					sum += oi.getPrice();
				}
				if(shoppingCart.size() == 0) {
					g2d.setFont(font.deriveFont(23f));
					g2d.setColor(Color.gray);
					g2d.drawString("Your cart is empty. Fill it up", 276-shopPx, 45-shopPy);
					g2d.drawString("so you can order something!", 274-shopPx, 60-shopPy);
					g2d.setColor(Color.white);
				}
				int offX = 0;
				offY += 30;
				g2d.setFont(font.deriveFont(23f)); offY += 20;
				g2d.drawString("Purchase", (270+offX)-shopPx, offY-shopPy);
				g2d.setFont(font.deriveFont(16f)); offY += 10; offX = 10;
				g2d.drawString(sum + " Iron Ingots | enter to order", (270+offX)-shopPx, offY-shopPy);
				
				g2d.setFont(font.deriveFont(32f));
				g2d.drawString("Your cart", 260-shopPx, 20-shopPy);
				g2d.setFont(font.deriveFont(23f));
				int in = shopExtraIndex;
				if(in == shoppingCart.size()) {
					in++;
				}
				int ry = (50+(in*30));
				float targetShopPy = shopPy;
				if(ry + 60 > shopPy+256) {
					targetShopPy = ry - 186;
				}
				if(ry-50 < shopPy) {
					targetShopPy = ry - 60;
				}
				if(targetShopPy < 0) {
					targetShopPy = 0;
				}else if(targetShopPy > offY - 248) {
					targetShopPy = offY - 248;
				}
				if(shopState == ShopState.SHOPPING_CART) {
					shopPx = MVCUtils.lerp(shopPx, 256, deltaTime*5f);
					shopPy = MVCUtils.lerp(shopPy, targetShopPy, deltaTime*5f);
				}
				g2d.drawString(">", 260-shopPx, ry-shopPy);
				g2d.setFont(font.deriveFont(16f));
				g2d.drawString("(" + shoppingCart.size() + " items)", 260-shopPx, 30-shopPy);
				g2d.drawString("Navigate using arrows,", 390-shopPx, 15-shopPy);
				g2d.drawString("Exit using left", 432-shopPx, 25-shopPy);
			}else if(shopState == ShopState.PERIPHERALS || renderExtraShopState == ShopState.PERIPHERALS) {
				int offY = 20;
				for(OrderableItem oi : PERIPHERALS) {
					int offX = 0;
					g2d.setFont(font.deriveFont(23f)); offY += 20;
					g2d.drawString(oi.getName().getString(), (270+offX)-shopPx, offY-shopPy);
					g2d.setFont(font.deriveFont(16f)); offY += 10; offX = 10;
					int inCart = 0;
					for(OrderableItem io : shoppingCart) {
						if(io.equals(oi)) {
							inCart++;
						}
					}
					if(inCart > 0) {
						g2d.drawString(oi.getPrice() + " Iron Ingots | " + inCart + " in cart", (270+offX)-shopPx, offY-shopPy);
					}else {
						g2d.drawString(oi.getPrice() + " Iron Ingots", (270+offX)-shopPx, offY-shopPy);
					}
				}
				g2d.setFont(font.deriveFont(32f));
				g2d.drawString("Peripherals", 260-shopPx, 20-shopPy);
				g2d.setFont(font.deriveFont(23f));
				int ry = (40+(shopExtraIndex*30));
				float targetShopPy = shopPy;
				if(ry + 60 > shopPy+256) {
					targetShopPy = ry - 186;
				}
				if(ry-50 < shopPy) {
					targetShopPy = ry - 60;
				}
				if(targetShopPy < 0) {
					targetShopPy = 0;
				}else if(targetShopPy > offY - 248) {
					targetShopPy = offY - 248;
				}
				if(shopState == ShopState.PERIPHERALS) {
					shopPx = MVCUtils.lerp(shopPx, 256, deltaTime*5f);
					shopPy = MVCUtils.lerp(shopPy, targetShopPy, deltaTime*5f);
				}
				g2d.drawString(">", 260-shopPx, ry-shopPy);
				g2d.setFont(font.deriveFont(16f));
				g2d.drawString("Navigate using arrows,", 390-shopPx, 15-shopPy);
				g2d.drawString("Select using enter,", 409-shopPx, 25-shopPy);
				g2d.drawString("Exit using left", 432-shopPx, 35-shopPy);
			}
			
			if(shopState == ShopState.MENU) {
				shopPx = MVCUtils.lerp(shopPx, 0, deltaTime*5f);
				shopPy = MVCUtils.lerp(shopPy, 0, deltaTime*5f);
			}
			
			lastShopImage = bi;
		}else if(tabletState == State.SHOP_OUTRO) {
			totalTimeRadar += deltaTime;
			float firstPercentage = Math.max(0f, Math.min(1f, (totalTimeRadar-0.802f)/0.123f));
			float secondPercentage = Math.max(0f, Math.min(1f, (totalTimeRadar-0.925f)/0.124f));
			float thirdPercentage = Math.max(0f, Math.min(1f, (totalTimeRadar-1.049f)/0.118f));
			float fourthPercentage = Math.max(0f, Math.min(1f, (totalTimeRadar-1.167f)/0.117f));
			float fifthPercentage = Math.max(0f, Math.min(1f, (totalTimeRadar-1.284f)/0.18f));
			
			if(fifthPercentage == 1f && mcc.getSoundManager().isPlaying(shopOutroSound)) {
				mcc.getSoundManager().stop(shopOutroSound);
				mcc.getSoundManager().stop(shopMusicSound);
				tabletState = State.DISPLAY_ORDER;
				shopPy = 256;
				totalTimeRadar = 0f;
			}
			
			int oneImage = 51;
			
			g2d.setColor(Color.darkGray.darker().darker().darker());
			g2d.fillRect(0, 0, 256, 256);
			
			g2d.drawImage(lastShopImage.getSubimage(0, 0, 256, oneImage), (int) (256 * firstPercentage), 0, null);
			g2d.drawImage(lastShopImage.getSubimage(0, oneImage, 256, oneImage), (int) (256 * secondPercentage),oneImage, null);
			g2d.drawImage(lastShopImage.getSubimage(0, oneImage*2, 256, oneImage), (int) (256 * thirdPercentage), oneImage*2, null);
			g2d.drawImage(lastShopImage.getSubimage(0, oneImage*3, 256, oneImage), (int) (256 * fourthPercentage),oneImage*3, null);
			g2d.drawImage(lastShopImage.getSubimage(0, oneImage*4, 256, oneImage), (int) (256 * fifthPercentage), oneImage*4, null);
		}else if(tabletState == State.DISPLAY_ORDER) {
			if(!mcc.getSoundManager().isPlaying(displayOrderMusicSound) && tabletOn) {
				mcc.getSoundManager().play(displayOrderMusicSound);
			}
			if(ClientMod.myOrder != null) {
				shopPy = MVCUtils.lerp(shopPy, 0, deltaTime*2);
			}
			g2d.setColor(Color.darkGray.darker().darker().darker());
			g2d.fillRect(0, 0, 256, 256);
			
			g2d.setColor(Color.white);
			g2d.fillRect(64, (int)(190-shopPy), 128, 3);
			
			float satelliteAngle = ((float)mcc.world.getSkyAngle(deltaTime)*5f);
			satelliteAngle %= 1f;
			boolean canSee = satelliteAngle < 0.22249603 || satelliteAngle > 0.78432274;
			int satX = (int)(128 + (96*Math.cos((satelliteAngle*6.3)-1.65)));
			int satY = (int)((190 + (32*Math.sin((satelliteAngle*6.3)-1.65)))-shopPy);
			
			if(ClientMod.myOrder.currentStatus == OrderStatus.PAYMENT_CHEST_ARRIVAL_SOON) {
				g2d.setFont(font.deriveFont(43f));
				g2d.setColor(Color.white);
				g2d.drawString("Payment chest", 32, 64-shopPy);
				g2d.setFont(font.deriveFont(32f));
				g2d.setColor(Color.gray);
				g2d.drawString("arriving soon", 87, 80-shopPy);
				
				g2d.setFont(font.deriveFont(16f));
				g2d.setColor(Color.white);
				g2d.drawString("A chest will be delivered soon which", 36, 100-shopPy);
				g2d.drawString("you will fill with your payment.", 36, 109-shopPy);
				g2d.drawString("The satellite needs to be over you", 36, 118-shopPy);
				g2d.drawString("for this to work.", 36, 127-shopPy);
			}else if(ClientMod.myOrder.currentStatus == OrderStatus.PAYMENT_CHEST_ARRIVED) {
				g2d.setFont(font.deriveFont(43f));
				g2d.setColor(Color.white);
				g2d.drawString("Payment chest", 32, 64-shopPy);
				g2d.setFont(font.deriveFont(32f));
				g2d.setColor(Color.gray);
				g2d.drawString("arrived", 150, 80-shopPy);
				
				g2d.setFont(font.deriveFont(16f));
				g2d.setColor(Color.white);
				g2d.drawString("Fill it up with ingots and it will", 36, 100-shopPy);
				g2d.drawString("automatically be sent back! Don't", 36, 109-shopPy);
				g2d.drawString("worry about the booster, it won't", 36, 118-shopPy);
				g2d.drawString("kill you.", 36, 127-shopPy);
			}else if(ClientMod.myOrder.currentStatus == OrderStatus.PAYMENT_CHEST_RECEIVING) {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.white);
				g2d.fillOval((int)chestX, (int)chestY, 4, 4);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				
				g2d.setFont(font.deriveFont(43f));
				g2d.setColor(Color.white);
				g2d.drawString("Payment chest", 32, 64-shopPy);
				g2d.setFont(font.deriveFont(32f));
				g2d.setColor(Color.gray);
				g2d.drawString("being received", 77, 80-shopPy);
				
				g2d.setFont(font.deriveFont(16f));
				g2d.setColor(Color.white);
				g2d.drawString("We are receiving your payment.", 36, 100-shopPy);
			}else if(ClientMod.myOrder.currentStatus == OrderStatus.ORDER_CHEST_ARRIVAL_SOON) {
				g2d.setFont(font.deriveFont(43f));
				g2d.setColor(Color.white);
				g2d.drawString("Ordered items", 34, 64-shopPy);
				g2d.setFont(font.deriveFont(32f));
				g2d.setColor(Color.gray);
				g2d.drawString("arriving soon", 84, 80-shopPy);
				
				g2d.setFont(font.deriveFont(16f));
				g2d.setColor(Color.white);
				g2d.drawString("Your items will arrive soon! A chest", 36, 100-shopPy);
				g2d.drawString("will land and you will collect your", 36, 109-shopPy);
				g2d.drawString("packages from it.", 36, 118-shopPy);
			}else if(ClientMod.myOrder.currentStatus == OrderStatus.ORDER_CHEST_ARRIVED) {
				if(this.drawChest) {
					g2d.setFont(font.deriveFont(43f));
					g2d.setColor(Color.white);
					g2d.drawString("Ordered items", 34, 64-shopPy);
					g2d.setFont(font.deriveFont(32f));
					g2d.setColor(Color.gray);
					g2d.drawString("arriving soon", 84, 80-shopPy);
					
					g2d.setFont(font.deriveFont(16f));
					g2d.setColor(Color.white);
					g2d.drawString("Your items will arrive soon! A chest", 36, 100-shopPy);
					g2d.drawString("will land and you will collect your", 36, 109-shopPy);
					g2d.drawString("packages from it.", 36, 118-shopPy);
					this.chestX = MVCUtils.lerp(chestX, 128, deltaTime);
					this.chestY = MVCUtils.lerp(chestY, 189, deltaTime);
					
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setColor(Color.white);
					g2d.fillOval((int)chestX, (int)chestY, 4, 4);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				}else{
					g2d.setFont(font.deriveFont(43f));
					g2d.setColor(Color.white);
					g2d.drawString("Ordered items", 34, 64-shopPy);
					g2d.setFont(font.deriveFont(32f));
					g2d.setColor(Color.gray);
					g2d.drawString("arrived", 145, 80-shopPy);
					
					g2d.setFont(font.deriveFont(16f));
					g2d.setColor(Color.white);
					g2d.drawString("Your items are at your location!", 36, 100-shopPy);
					g2d.drawString("Collect your packages.", 36, 109-shopPy);
				}
			}else if(ClientMod.myOrder.currentStatus == OrderStatus.ORDER_CHEST_RECEIVED) {
				totalTimeRadar += deltaTime;
				g2d.setColor(Color.white);
				g2d.setFont(font.deriveFont(43f));
				g2d.drawString("Thank you!", 51, 100);
				
				if(totalTimeRadar > 6) {
					tabletState = State.LOOKING_FOR_SATELLITE;
					if(tabletOn) {
						mcc.getSoundManager().stop(displayOrderMusicSound);
						mcc.getSoundManager().play(radarSound);
					}
				}
			}
			
			if(canSee) {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.gray);
				g2d.fillOval(satX, satY, 16, 16);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.setFont(font.deriveFont(16f));
				g2d.drawString("Satellite", satX-12, satY-1);
			}
		}
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "PNG", baos);
			byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private boolean pressed(int key) {
		return GLFW.glfwGetKey(mcc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
	}
	
	private void update() {
		//System.out.println(mcc.getSoundManager().isPlaying(shopMusicSound));
		
		if(tabletOn) {
			orderingTabletModel.setButtons(pressed(GLFW.GLFW_KEY_UP), pressed(GLFW.GLFW_KEY_DOWN), pressed(GLFW.GLFW_KEY_LEFT), pressed(GLFW.GLFW_KEY_RIGHT), pressed(GLFW.GLFW_KEY_ENTER), mcc.getTickDelta());
			if(tabletState == State.LOOKING_FOR_SATELLITE && satelliteVisible) {
				if(pressed(GLFW.GLFW_KEY_ENTER)) {
					totalTimeRadar = 0;
					tabletState = State.SHOP_INTRO;
					mcc.getSoundManager().stop(radarSound);
				}
			}
			if(tabletState == State.SHOP) {
				if(pressed(GLFW.GLFW_KEY_LEFT)) {
					if(!arrowLeftPressed) {
						arrowLeftPressed = true;
						if(shopState == ShopState.PC_PARTS) {
							shopState = ShopState.MENU;
							renderExtraShopState = ShopState.PC_PARTS;
						}else if(shopState == ShopState.SHOPPING_CART) {
							shopState = ShopState.MENU;
							renderExtraShopState = ShopState.SHOPPING_CART;
						}else if(shopState == ShopState.PERIPHERALS) {
							shopState = ShopState.MENU;
							renderExtraShopState = ShopState.PERIPHERALS;
						}
					}
				}else {
					if(arrowLeftPressed) {
						arrowLeftPressed = false;
					}
				}
				
				if(pressed(GLFW.GLFW_KEY_DOWN)) {
					if(!arrowDownPressed) {
						arrowDownPressed = true;
						if(shopState == ShopState.MENU) {
							shopIndex += 1;
							if(shopIndex > 2) {
								shopIndex = 0;
							}
						}else if(shopState == ShopState.PC_PARTS) {
							shopExtraIndex += 1;
							if(shopExtraIndex >= PC_PARTS.size()) {
								shopExtraIndex = 0;
							}
						}else if(shopState == ShopState.SHOPPING_CART) {
							shopExtraIndex += 1;
							if(shopExtraIndex > shoppingCart.size()) {
								shopExtraIndex = 0;
							}
						}else if(shopState == ShopState.PERIPHERALS) {
							shopExtraIndex += 1;
							if(shopExtraIndex >= PERIPHERALS.size()) {
								shopExtraIndex = 0;
							}
						}
					}
				}else {
					if(arrowDownPressed) {
						arrowDownPressed = false;
					}
				}
				
				if(pressed(GLFW.GLFW_KEY_UP)) {
					if(!arrowUpPressed) {
						arrowUpPressed = true;
						if(shopState == ShopState.MENU) {
							shopIndex -= 1;
							if(shopIndex < 0) {
								shopIndex = 2;
							}
						}else if(shopState == ShopState.PC_PARTS) {
							shopExtraIndex -= 1;
							if(shopExtraIndex < 0) {
								shopExtraIndex = PC_PARTS.size()-1;
							}
						}else if(shopState == ShopState.SHOPPING_CART) {
							shopExtraIndex -= 1;
							if(shopExtraIndex < 0) {
								shopExtraIndex = shoppingCart.size();
							}
						}else if(shopState == ShopState.PERIPHERALS) {
							shopExtraIndex -= 1;
							if(shopExtraIndex < 0) {
								shopExtraIndex = PERIPHERALS.size()-1;
							}
						}
					}
				}else {
					if(arrowUpPressed) {
						arrowUpPressed = false;
					}
				}
				
				if(pressed(GLFW.GLFW_KEY_ENTER)) {
					if(!arrowRightPressed) {
						arrowRightPressed = true;
						if(shopState == ShopState.MENU) {
							shopExtraIndex = 0; 
							if(shopIndex == 0) {
								shopState = ShopState.PC_PARTS;
								renderExtraShopState = ShopState.PC_PARTS;
							}else if(shopIndex == 1) {
								shopState = ShopState.PERIPHERALS;
								renderExtraShopState = ShopState.PERIPHERALS;
							}else {
								shopState = ShopState.SHOPPING_CART;
								renderExtraShopState = ShopState.SHOPPING_CART;
							}
						}else if(shopState == ShopState.PC_PARTS) {
							shoppingCart.add(PC_PARTS.get(shopExtraIndex));
						}else if(shopState == ShopState.PERIPHERALS) {
							shoppingCart.add(PERIPHERALS.get(shopExtraIndex));
						}else if(shopState == ShopState.SHOPPING_CART) {
							if(shopExtraIndex != shoppingCart.size()){
								shoppingCart.remove(shopExtraIndex);
							}else {
								if(shoppingCart.size() > 0) {
									PacketByteBuf p = new PacketByteBuf(Unpooled.buffer());
									p.writeInt(shoppingCart.size());
									for(OrderableItem i : shoppingCart) {
										p.writeItemStack(new ItemStack(i));
									}
									ClientSidePacketRegistry.INSTANCE.sendToServer(PacketList.C2S_ORDER, p);
									
									tabletState = State.SHOP_OUTRO;
									totalTimeRadar = 0;
									mcc.getSoundManager().play(shopOutroSound);
								}
							}
						}
					}
				}else {
					if(arrowRightPressed) {
						arrowRightPressed = false;
					}
				}
			}
		}
	}
	
	public void generateTexture() {
		if(!tabletOn) {
			orderingTabletModel.rotateButtons(2F, deltaTime);
		}else {
			orderingTabletModel.rotateButtons(-0.7854F, deltaTime/1.4f);
		}
		if(!tabletOn) {
			return;
		}
		this.update();
		if(byteArrayInputStream == null) {
			return;
		}
		if(nibt != null) {nibt.close(); nibt = null;}
		if(renderedImage != null) {renderedImage.close(); renderedImage = null;}
		try {
			renderedImage = NativeImage.read(byteArrayInputStream);
			if(renderedImage != null) {
				nibt = new NativeImageBackedTexture(renderedImage);
				if(textureIdentifier != null) {
					MinecraftClient.getInstance().getTextureManager().destroyTexture(textureIdentifier);
				}
				textureIdentifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("tablet_display", nibt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		byteArrayInputStream = null;
	}
	
	public enum ShopState{
		MENU, PC_PARTS, PERIPHERALS, SHOPPING_CART
	}
	
	public enum State{
		LOOKING_FOR_SATELLITE, SHOP_INTRO, SHOP, SHOP_OUTRO, DISPLAY_ORDER
	}
}
