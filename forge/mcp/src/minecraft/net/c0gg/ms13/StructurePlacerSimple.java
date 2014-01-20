package net.c0gg.ms13;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class StructurePlacerSimple implements StructurePlacer {
	private int x,y,z;
	
	public StructurePlacerSimple(int x,int y,int z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	@Override
	public ChunkPosition getPos(World world, int sizeX, int sizeY, int sizeZ) {
		return new ChunkPosition(x-sizeX/2,y-sizeY/2,z-sizeZ/2);
	}

}
