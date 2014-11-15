package rocks.cogg.ms13;

import net.minecraft.block.BlockLadder;
import net.minecraft.creativetab.CreativeTabs;

public class BlockStationLadder extends BlockLadder {
	public BlockStationLadder() {
		super();
		setCreativeTab(ModMinestation.tabSpacestation);
		setStepSound(ModMinestation.soundStationFootstep);
	}
}
