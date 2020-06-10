package mcvmcomputers.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundList {
	public static SoundEvent RADAR_SOUND = new SoundEvent(new Identifier("mcvmcomputers", "radar"));
	public static SoundEvent ROCKET_SOUND = new SoundEvent(new Identifier("mcvmcomputers", "rocket"));
	public static SoundEvent SHOPINTRO_SOUND = new SoundEvent(new Identifier("mcvmcomputers", "shopintro"));
	public static SoundEvent SHOPOUTRO_SOUND = new SoundEvent(new Identifier("mcvmcomputers", "shopoutro"));
	public static SoundEvent SHOPMUSIC_SOUND = new SoundEvent(new Identifier("mcvmcomputers", "shopmusic"));
	
	public static void init() {
		Registry.register(Registry.SOUND_EVENT, new Identifier("mcvmcomputers", "radar"), RADAR_SOUND);
		Registry.register(Registry.SOUND_EVENT, new Identifier("mcvmcomputers", "rocket"), ROCKET_SOUND);
		Registry.register(Registry.SOUND_EVENT, new Identifier("mcvmcomputers", "shopintro"), SHOPINTRO_SOUND);
		Registry.register(Registry.SOUND_EVENT, new Identifier("mcvmcomputers", "shopoutro"), SHOPOUTRO_SOUND);
		Registry.register(Registry.SOUND_EVENT, new Identifier("mcvmcomputers", "shopmusic"), SHOPMUSIC_SOUND);
	}
}
