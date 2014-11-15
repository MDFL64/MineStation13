package rocks.cogg.ms13;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockStationWelded extends BlockStation implements ToolableWelder {
	public BlockStationWelded(int par1) {
		
		super(par1);
	}
	private Block StationFree = ModMinestation.blockStationBlockFree;
	
	/** Change to the non-welded version of the block. */
	@Override
	public void onUseWelder(World world, int x, int y, int z, int dir) {
		world.setBlock(x, y, z, ModMinestation.blockStationBlockFree);
	}
}
