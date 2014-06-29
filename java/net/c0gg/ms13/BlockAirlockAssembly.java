package net.c0gg.ms13;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class BlockAirlockAssembly extends Block {
	public BlockAirlockAssembly(int par1) {
		super(ModMinestation.materialStationMetal);
		setHardness(1).setResistance(1).setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
    public void onBlockAdded(World world, int x, int y, int z) {
		//startPos
		ChunkPosition sp;
		
		if (world.getBlock(x, y+1, z)== this) {
			sp = new ChunkPosition(x,y,z);
		} else if (world.getBlock(x, y-1, z)==this) {
			sp = new ChunkPosition(x,y-1,z);
		} else {
			return;
		}
		
		ChunkPosition[] positionsToCheck = new ChunkPosition[] {new ChunkPosition(sp.chunkPosX+1,sp.chunkPosY,sp.chunkPosZ),new ChunkPosition(sp.chunkPosX-1,sp.chunkPosY,sp.chunkPosZ),new ChunkPosition(sp.chunkPosX,sp.chunkPosY,sp.chunkPosZ+1),new ChunkPosition(sp.chunkPosX,sp.chunkPosY,sp.chunkPosZ-1)};
		
		//basePos
		ChunkPosition bp=null;
		boolean orientedZ=false;
		
		for (int i=0;i<4;i++) {
			ChunkPosition ip = positionsToCheck[i];
			if (world.getBlock(ip.chunkPosX,ip.chunkPosY,ip.chunkPosZ)==this&&world.getBlock(ip.chunkPosX,ip.chunkPosY+1,ip.chunkPosZ)==this) {
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
			world.setBlock(base.chunkPosX,base.chunkPosY,base.chunkPosZ,ModMinestation.blockAirlockFrame,8,3); //Used to be blockName.blockID ~Pdan
			world.setBlock(base.chunkPosX+1,base.chunkPosY,base.chunkPosZ,ModMinestation.blockAirlockFrame,9,3);
			world.setBlock(base.chunkPosX,base.chunkPosY+1,base.chunkPosZ,ModMinestation.blockAirlockFrame,10,3);
			world.setBlock(base.chunkPosX+1,base.chunkPosY+1,base.chunkPosZ,ModMinestation.blockAirlockFrame,11,3);
		} else {
			world.setBlock(base.chunkPosX,base.chunkPosY,base.chunkPosZ,ModMinestation.blockAirlockFrame,12,3);
			world.setBlock(base.chunkPosX,base.chunkPosY,base.chunkPosZ+1,ModMinestation.blockAirlockFrame,13,3); 
			world.setBlock(base.chunkPosX,base.chunkPosY+1,base.chunkPosZ,ModMinestation.blockAirlockFrame,14,3);
			world.setBlock(base.chunkPosX,base.chunkPosY+1,base.chunkPosZ+1,ModMinestation.blockAirlockFrame,15,3);
		}
	}
}
