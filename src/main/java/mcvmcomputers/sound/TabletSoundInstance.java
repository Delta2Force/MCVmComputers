package mcvmcomputers.sound;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class TabletSoundInstance extends AbstractSoundInstance{
	public TabletSoundInstance(Identifier soundId, SoundCategory category, Random random) {
		super(soundId, SoundCategory.MASTER, Random.create());
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
		return .15F * this.volume;
	}

}
