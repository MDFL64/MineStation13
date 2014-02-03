package net.c0gg.ms13;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockStation extends Block {
	public BlockStation(int par1) {
		super(par1,ModMinestation.materialStationMetal);
		setBlockUnbreakable().setCreativeTab(ModMinestation.tabSpacestation);
	}
	@Override
	public int quantityDropped(Random random)
    {
        return 0;
    }
	
	//Station Block Functions
	@Override
    public void onBlockAdded(World world, int x, int y, int z) {
		AtmosSystem atmos = AtmosSystem.getForWorld(world);
		if (atmos!=null) {
			atmos.stationBlockAdded(x,y,z);
		}
	}
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		super.breakBlock(world, x, y, z, par5, par6);
		AtmosSystem atmos = AtmosSystem.getForWorld(world);
		if (atmos!=null) {
			atmos.stationBlockRemove(x,y,z);
		}
	}
}