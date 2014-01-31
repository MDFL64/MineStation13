package net.c0gg.ms13;

import net.minecraft.block.StepSound;

//It is impossible to specify a domain(?) for vanilla step sounds. This one forces the ms13 domain(?).

public class StepSoundMs extends StepSound {
	public StepSoundMs(String par1Str, float par2, float par3) {
		super(par1Str, par2, par3);
	}
	
	@Override
	public String getBreakSound()
    {
        return "ms13:"+super.getBreakSound();
    }

    @Override
    public String getStepSound()
    {
        return "ms13:"+super.getStepSound();
    }
}