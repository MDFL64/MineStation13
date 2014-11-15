package rocks.cogg.ms13;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockStation extends Block {
	public BlockStation(int par1) {
		super(ModMinestation.materialStationMetal);
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