package net.c0gg.ms13;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class BlockAirlockAssembly extends Block {
	public BlockAirlockAssembly(int par1) {
		super(par1, ModMinestation.materialStationMetal);
		setHardness(1).setResistance(1).setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
    public void onBlockAdded(World world, int x, int y, int z) {
		//startPos
		ChunkPosition sp;
		
		if (world.getBlockId(x, y+1, z)==this.blockID) {
			sp = new ChunkPosition(x,y,z);
		} else if (world.getBlockId(x, y-1, z)==this.blockID) {
			sp = new ChunkPosition(x,y-1,z);
		} else {
			return;
		}
		
		ChunkPosition[] positionsToCheck = new ChunkPosition[] {new ChunkPosition(sp.x+1,sp.y,sp.z),new ChunkPosition(sp.x-1,sp.y,sp.z),new ChunkPosition(sp.x,sp.y,sp.z+1),new ChunkPosition(sp.x,sp.y,sp.z-1)};
		
		//basePos
		ChunkPosition bp=null;
		boolean orientedZ=false;
		
		for (int i=0;i<4;i++) {
			ChunkPosition ip = positionsToCheck[i];
			if (world.getBlockId(ip.x,ip.y,ip.z)==this.blockID&&world.getBlockId(ip.x,ip.y+1,ip.z)==this.blockID) {
				if (i>1) {
					orientedZ=true;
				}
				if (i==0||i==2) {
					bp=sp;
				} else {
					bp = ip;
				}
				break;
			}
		}
		
		if (bp==null) return;
		
		setupBlocks(world,bp,orientedZ);
	}
	
	private void setupBlocks(World world,ChunkPosition base,boolean orientedZ) {
		if (!orientedZ) {
			world.setBlock(base.x,base.y,base.z,ModMinestation.blockAirlockFrame.blockID,8,3);
			world.setBlock(base.x+1,base.y,base.z,ModMinestation.blockAirlockFrame.blockID,9,3);
			world.setBlock(base.x,base.y+1,base.z,ModMinestation.blockAirlockFrame.blockID,10,3);
			world.setBlock(base.x+1,base.y+1,base.z,ModMinestation.blockAirlockFrame.blockID,11,3);
		} else {
			world.setBlock(base.x,base.y,base.z,ModMinestation.blockAirlockFrame.blockID,12,3);
			world.setBlock(base.x,base.y,base.z+1,ModMinestation.blockAirlockFrame.blockID,13,3);
			world.setBlock(base.x,base.y+1,base.z,ModMinestation.blockAirlockFrame.blockID,14,3);
			world.setBlock(base.x,base.y+1,base.z+1,ModMinestation.blockAirlockFrame.blockID,15,3);
		}
	}
}
