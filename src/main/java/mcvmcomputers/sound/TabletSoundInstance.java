package mcvmcomputers.sound;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class TabletSoundInstance extends AbstractSoundInstance{
	final boolean streamed;
	
	public TabletSoundInstance(SoundEvent soundId, boolean streamed) {
		super(soundId, SoundCategory.MASTER);
		this.streamed = streamed;
	}
	
	@Override
	public boolean isRepeatable() {
		return true;
	}
	
	@Override
	public boolean shouldAlwaysPlay() {
		return true;
	}
	
	public boolean isStreamed() {
		return streamed;
	}
	
	@Override
	public float getVolume() {
		return .15F * this.sound.getVolume();
	}

}
