package rocks.cogg.ms13;

import net.minecraft.block.Block.SoundType;

/**
 * It is impossible to specify a domain(?) for vanilla block sounds. This type forces the ms13 domain(?).
 */

public class SoundTypeMs extends SoundType {
	public SoundTypeMs(String par1Str, float par2, float par3) {
		super(par1Str, par2, par3);
	}
	
	@Override
	public String getBreakSound()
    {
        return "ms13:"+super.getBreakSound();
    }

	@Override
    public String getStepResourcePath()
    {
        return "ms13:"+super.getStepResourcePath();
    }
}