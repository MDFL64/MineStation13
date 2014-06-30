package net.c0gg.ms13;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockStationFree extends Block implements ToolableWelder {
	public BlockStationFree(int par1) {
		super(ModMinestation.materialStationMetal);
		setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	/** Change to the welded version of the block. */
	@Override
	public void onUseWelder(World world, int x, int y, int z, int dir) {
		world.setBlock(x, y, z, ModMinestation.blockStationBlock);
	}
}