package mcvmcomputers.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoundList {
	public static SoundEvent RADAR_SOUND = new SoundEvent(new Identifier("mcvmcomputers", "radar"));
	
	public static void init() {
		Registry.register(Registry.SOUND_EVENT, new Identifier("mcvmcomputers", "radar"), RADAR_SOUND);
	}
}
