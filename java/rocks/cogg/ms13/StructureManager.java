package rocks.cogg.ms13;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class StructureManager {
	public static void save(String name,World world,ChunkPosition seedpos) {
		//Flood fill to find bounding size of structure
		HashSet<ChunkPosition> closedPositions = new HashSet<ChunkPosition>();
		Queue<ChunkPosition> openPositions = new LinkedList<ChunkPosition>();
		
		openPositions.add(seedpos);
		
		int minx,maxx,miny,maxy,minz,maxz;
		
		minx=maxx=seedpos.chunkPosX;miny=maxy=seedpos.chunkPosY;minz=maxz=seedpos.chunkPosZ;
		
		while (!openPositions.isEmpty()) {
			ChunkPosition curPos = openPositions.poll();
			if (world.getBlock(curPos.chunkPosX, curPos.chunkPosY, curPos.chunkPosZ)!=Blocks.air&&!closedPositions.contains(curPos)) {
				closedPositions.add(curPos);
				
				openPositions.add(new ChunkPosition(curPos.chunkPosX+1, curPos.chunkPosY, curPos.chunkPosZ));
				openPositions.add(new ChunkPosition(curPos.chunkPosX-1, curPos.chunkPosY, curPos.chunkPosZ));
				openPositions.add(new ChunkPosition(curPos.chunkPosX, curPos.chunkPosY+1, curPos.chunkPosZ));
				openPositions.add(new ChunkPosition(curPos.chunkPosX, curPos.chunkPosY-1, curPos.chunkPosZ));
				openPositions.add(new ChunkPosition(curPos.chunkPosX, curPos.chunkPosY, curPos.chunkPosZ+1));
				openPositions.add(new ChunkPosition(curPos.chunkPosX, curPos.chunkPosY, curPos.chunkPosZ-1));
				
				if (curPos.chunkPosX<minx) {
					minx=curPos.chunkPosX;
				}
				if (curPos.chunkPosY<miny) {
					miny=curPos.chunkPosY;
				}
				if (curPos.chunkPosZ<minz) {
					minz=curPos.chunkPosZ;
				}
				
				if (curPos.chunkPosX>maxx) {
					maxx=curPos.chunkPosX;
				}
				if (curPos.chunkPosY>maxy) {
					maxy=curPos.chunkPosY;
				}
				if (curPos.chunkPosZ>maxz) {
					maxz=curPos.chunkPosZ;
				}
			}
		}
		
		//Write to file.
		DataOutputStream dataStream=null;
		try {
			File saveDir = MinecraftServer.getServer().getFile("minestation/data/structures");
			saveDir.mkdirs(); //Make sure the save directory exists
			File saveFile = new File(saveDir,name+".station");
			
			dataStream = new DataOutputStream(new FileOutputStream(saveFile.getAbsoluteFile()));
			
			//Part 1: Blocks
			dataStream.writeInt(maxx-minx+1);
			dataStream.writeInt(maxy-miny+1);
			dataStream.writeInt(maxz-minz+1);
			
			ArrayList<NBTBase> tileEnts= new ArrayList<NBTBase>();
			for (int ix=minx;ix<=maxx;ix++) {
				for (int iy=miny;iy<=maxy;iy++) { //TODO update to deal with new block/id system
					for (int iz=minz;iz<=maxz;iz++) {
						dataStream.writeShort(Block.getIdFromBlock(world.getBlock(ix,iy,iz)));
						dataStream.writeByte(world.getBlockMetadata(ix,iy,iz));
						
						TileEntity tileEnt = world.getTileEntity(ix,iy,iz);
						if (tileEnt!=null) {
							NBTTagCompound nbt=new NBTTagCompound();
							tileEnt.writeToNBT(nbt);
							//Localize position
							nbt.setInteger("x", ix-minx);
							nbt.setInteger("y", iy-miny);
							nbt.setInteger("z", iz-minz);
							
							tileEnts.add(nbt);
						}
					}
				}
			}
			
			//Part 2: Tile Ents
			dataStream.writeInt(tileEnts.size());
			for (NBTBase nbt:tileEnts) {
				CompressedStreamTools.write((NBTTagCompound)nbt,(DataOutput)dataStream); //TODO bad cast?
			}
			
			//Part 3: Ents
			List<Entity> ents = world.getEntitiesWithinAABB(Entity.class,AxisAlignedBB.getBoundingBox(minx,miny,minz,maxx+1,maxy+1,maxz+1));
			
			//Only count non-players
			int entCount=0;
			for (Entity ent:ents) {
				if (!(ent instanceof EntityPlayer))
					entCount++;
			}
			
			dataStream.writeInt(entCount);
			for (Entity ent:ents) {
				if (!(ent instanceof EntityPlayer)) {
					NBTTagCompound nbt=new NBTTagCompound();
					ent.writeToNBTOptional(nbt);
					
					//Clear ids so they don't conflict
					nbt.removeTag("UUIDMost");
					nbt.removeTag("UUIDLeast");
					
					//Localize position
					NBTTagList pos = new NBTTagList();
					pos.appendTag(new NBTTagDouble(ent.posX-minx));
					pos.appendTag(new NBTTagDouble(ent.posY-miny));
					pos.appendTag(new NBTTagDouble(ent.posZ-minz));
					nbt.setTag("pos",pos);

					CompressedStreamTools.write((NBTTagCompound)nbt,(DataOutput)dataStream);
				}
			}
			
			dataStream.flush();
			dataStream.close();
		} catch (IOException e) {
			System.out.println("Structure write failed! "+e.getMessage());
		} finally {
			if (dataStream!=null) {
				try {
					dataStream.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void load(String name,World world,StructurePlacer placer) {
		DataInputStream dataStream=null;
		try {
			File saveFile = MinecraftServer.getServer().getFile("minestation/data/structures/"+name+".station");
			
			dataStream = new DataInputStream(new FileInputStream(saveFile.getAbsoluteFile()));
			
			//Blocks
			int sizex = dataStream.readInt();
			int sizey = dataStream.readInt();
			int sizez = dataStream.readInt();
			
			ChunkPosition basePos = placer.getPos(world, sizex, sizey, sizez);
			
			for (int ix=0;ix<sizex;ix++) {
				for (int iy=0;iy<sizey;iy++) {
					for (int iz=0;iz<sizez;iz++) {
						int id = dataStream.readShort();
						Block block = (Block)Block.blockRegistry.getObjectById(id);
						int meta = dataStream.readByte();
						world.setBlock(basePos.chunkPosX+ix, basePos.chunkPosY+iy, basePos.chunkPosZ+iz, block, meta, 2);
					}
				}
			}
			//Tile Ents
			int numTileEnts = dataStream.readInt();
			for (int i=0;i<numTileEnts;i++) {
				NBTTagCompound nbt = CompressedStreamTools.read(dataStream);
				int px=nbt.getInteger("x")+basePos.chunkPosX;
				int py=nbt.getInteger("y")+basePos.chunkPosY;
				int pz=nbt.getInteger("z")+basePos.chunkPosZ;
				
				nbt.setInteger("x",px);
				nbt.setInteger("y",py);
				nbt.setInteger("z",pz);
				TileEntity tileEnt = TileEntity.createAndLoadEntity(nbt);
				world.setTileEntity(px,py,pz,tileEnt); // setBlockTileEntity(px, py, pz, tileEnt); //addTileEntity(tileEnt);
			}
			//Normal Ents
			int numEnts = dataStream.readInt();
			ArrayList<Entity> entstoload=new ArrayList<Entity>();
			for (int i=0;i<numEnts;i++) {
				NBTTagCompound nbt = CompressedStreamTools.read(dataStream);
				
				//Delocalize position
				NBTTagList pos = nbt.getTagList("Pos",6);
				NBTTagDouble posx = (NBTTagDouble)pos.removeTag(0);
				NBTTagDouble posy = (NBTTagDouble)pos.removeTag(0);
				NBTTagDouble posz = (NBTTagDouble)pos.removeTag(0);
				posx.func_150286_g();
				pos.appendTag(new NBTTagDouble(posx.func_150286_g()+basePos.chunkPosX));
				pos.appendTag(new NBTTagDouble(posy.func_150286_g()+basePos.chunkPosY));
				pos.appendTag(new NBTTagDouble(posz.func_150286_g()+basePos.chunkPosZ));
				
				entstoload.add(EntityList.createEntityFromNBT(nbt,world));
			}
			world.addLoadedEntities(entstoload);
			dataStream.close();
		} catch (IOException e) {
			System.out.println("Structure read failed! "+e.getMessage());
		} finally {
			if (dataStream!=null) {
				try {
					dataStream.close();
				} catch (IOException e) {}
			}
		}
	}
}
