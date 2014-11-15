package rocks.cogg.ms13;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public interface StructurePlacer {
	public ChunkPosition getPos(World world,int sizeX,int sizeY,int sizeZ);
}