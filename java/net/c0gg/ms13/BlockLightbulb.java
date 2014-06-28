package net.c0gg.ms13;

import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

public class BlockLightbulb extends BlockTorch {
	public BlockLightbulb(int id) {
		super(id);
		setCreativeTab(ModMinestation.tabSpacestation);
		setLightValue(0.9375F);
	}
	
	@Override
	public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random) {}
}