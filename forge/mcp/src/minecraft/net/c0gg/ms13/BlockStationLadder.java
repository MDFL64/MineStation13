package net.c0gg.ms13;

import net.minecraft.block.BlockLadder;
import net.minecraft.creativetab.CreativeTabs;

public class BlockStationLadder extends BlockLadder {
	public BlockStationLadder(int id) {
		super(id);
		setCreativeTab(ModMinestation.tabSpacestation);
		setStepSound(ModMinestation.soundStationFootstep);
	}
}
