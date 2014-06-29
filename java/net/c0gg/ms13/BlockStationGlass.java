package net.c0gg.ms13;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockStationGlass extends BlockBreakable {
	public BlockStationGlass() {
		super(null,Material.glass,false); //YOLO ~Pdan
		setHardness(5).setResistance(20).setStepSound(Block.soundGlassFootstep).setCreativeTab(ModMinestation.tabSpacestation);;
	}
	
	@Override
	public int quantityDropped(Random random)
    {
        return 0;
    }
	
	@Override
	public void registerIcons(IIconRegister iconregister)
    {
        this.blockIcon = iconregister.registerIcon(getTextureName());
    }
	
	//Station Block Functions
	@Override
    public void onBlockAdded(World world, int x, int y, int z) {
		AtmosZoner atmos = AtmosZoner.getForWorld(world);
		if (atmos!=null) {
			atmos.blockAdd(new ChunkPosition(x,y,z));
		}
	}
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		super.breakBlock(world, x, y, z, par5, par6);
		AtmosZoner atmos = AtmosZoner.getForWorld(world);
		if (atmos!=null) {
			atmos.blockRemove(new ChunkPosition(x,y,z));
		}
	}
}