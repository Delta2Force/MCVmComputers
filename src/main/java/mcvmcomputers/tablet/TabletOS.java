package mcvmcomputers.tablet;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mcvmcomputers.sound.SoundList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class TabletOS {
	public NativeImage renderedImage;
	public State tabletState = State.LOOKING_FOR_SATELLITE;
	public NativeImageBackedTexture nibt;
	public Identifier textureIdentifier;
	public ByteArrayInputStream byteArrayInputStream;
	
	private final Font font;
	private float totalTimeRadar;
	
	private SoundInstance radarSound;
	private MinecraftClient mcc = MinecraftClient.getInstance();
	private ArrayList<Float> radarRadius; 
	
	public float deltaTime;
	public long lastDeltaTimeTime;
	
	public boolean tabletOn = false;
	
	public TabletOS() throws FontFormatException, IOException {
		radarSound = new AbstractSoundInstance(SoundList.RADAR_SOUND, SoundCategory.MASTER) {
			@Override
			public boolean isRepeatable() {
				return true;
			}
			
			@Override
			public boolean shouldAlwaysPlay() {
				return true;
			}
			
			@Override
			public float getVolume() {
				return 0.15F * this.sound.getVolume();
			}
		};
		font = Font.createFont(Font.PLAIN, mcc.getResourceManager().getResource(new Identifier("mcvmcomputers", "font/tabletfont.ttf")).getInputStream());
		renderedImage = new NativeImage(1, 1, true);
		radarRadius = new ArrayList<Float>();
	}
	
	public void tabletTakenOut() {
		radarRadius.clear();
		totalTimeRadar = 0;
		radarRadius.add(0f);
		mcc.getSoundManager().play(radarSound);
		tabletState = State.LOOKING_FOR_SATELLITE;
	}
	public void tabletUnequipped() {
		mcc.getSoundManager().stop(radarSound);
		tabletState = State.NONE;
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
					if(f >= 400) {
						removeIndices.add(f);
					}
				}
				for(Float f : removeIndices) {
					radarRadius.remove(f);
				}
				removeIndices.clear();
				float time = ((float)mcc.world.getSkyAngle(deltaTime)*6f);
				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, 0, 256, 256);
				g2d.setColor(Color.white);
				g2d.setFont(font.deriveFont(20f));
				g2d.drawString("Scanning for Satellite...", 54, 40);
				g2d.drawString("Approximate Location displayed.", 26, 53);
				g2d.fillRect(64, 168, 128, 1);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(Color.yellow);
				g2d.drawOval((int)(128 + (96*Math.cos((time*6.3)-1.65))), (int)(168 + (32*Math.sin((time*6.3)-1.65))), 16, 16);
				g2d.setColor(Color.gray);
				for(Float f : radarRadius) {
					g2d.drawOval(Math.round(128 - f/2), Math.round(160 - f/2), Math.round(f), Math.round(f));
				}
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.setColor(Color.yellow);
				g2d.fillRect(4, 236, 16, 16);
				g2d.drawString("Approximate Location", 24, 248 );
				g2d.setColor(Color.green);
				g2d.fillRect(4, 216, 16, 16);
				g2d.drawString("Real Location", 24, 228 );
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
	
	public void generateTexture() {
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
	
	public enum State{
		NONE, LOOKING_FOR_SATELLITE, SATELLITE_FOUND, DOWNLOADING, SHOP_INTRO, MENU, PC_PARTS, SCREENS, PERIPHERALS, OTHERS, SHOPPING_CART, DISPLAY_ORDER
	}
}
