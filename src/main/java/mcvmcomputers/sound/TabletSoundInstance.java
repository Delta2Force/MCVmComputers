package mcvmcomputers.sound;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class TabletSoundInstance extends AbstractSoundInstance{
	
	public TabletSoundInstance(SoundEvent soundId) {
		super(soundId, SoundCategory.MASTER);
	}
	
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
		return .15F * this.sound.getVolume();
	}

}
