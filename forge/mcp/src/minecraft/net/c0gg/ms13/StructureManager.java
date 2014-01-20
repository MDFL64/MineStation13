package net.c0gg.ms13;

import java.io.DataInputStream;
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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
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
		
		minx=maxx=seedpos.x;miny=maxy=seedpos.y;minz=maxz=seedpos.z;
		
		while (!openPositions.isEmpty()) {
			ChunkPosition curPos = openPositions.poll();
			if (world.getBlockId(curPos.x, curPos.y, curPos.z)!=0&&!closedPositions.contains(curPos)) {
				closedPositions.add(curPos);
				
				openPositions.add(new ChunkPosition(curPos.x+1, curPos.y, curPos.z));
				openPositions.add(new ChunkPosition(curPos.x-1, curPos.y, curPos.z));
				openPositions.add(new ChunkPosition(curPos.x, curPos.y+1, curPos.z));
				openPositions.add(new ChunkPosition(curPos.x, curPos.y-1, curPos.z));
				openPositions.add(new ChunkPosition(curPos.x, curPos.y, curPos.z+1));
				openPositions.add(new ChunkPosition(curPos.x, curPos.y, curPos.z-1));
				
				if (curPos.x<minx) {
					minx=curPos.x;
				}
				if (curPos.y<miny) {
					miny=curPos.y;
				}
				if (curPos.z<minz) {
					minz=curPos.z;
				}
				
				if (curPos.x>maxx) {
					maxx=curPos.x;
				}
				if (curPos.y>maxy) {
					maxy=curPos.y;
				}
				if (curPos.z>maxz) {
					maxz=curPos.z;
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
				for (int iy=miny;iy<=maxy;iy++) {
					for (int iz=minz;iz<=maxz;iz++) {
						dataStream.writeShort(world.getBlockId(ix,iy,iz));
						dataStream.writeByte(world.getBlockMetadata(ix,iy,iz));
						
						TileEntity tileEnt = world.getBlockTileEntity(ix,iy,iz);
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
				NBTBase.writeNamedTag(nbt,dataStream);
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
					NBTTagList pos = nbt.getTagList("Pos");
					NBTTagDouble posx = (NBTTagDouble)pos.tagAt(0);
					NBTTagDouble posy = (NBTTagDouble)pos.tagAt(1);
					NBTTagDouble posz = (NBTTagDouble)pos.tagAt(2);
					posx.data=posx.data-minx;
					posy.data=posy.data-miny;
					posz.data=posz.data-minz;
					
					NBTBase.writeNamedTag(nbt,dataStream);
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
						int meta = dataStream.readByte();
						world.setBlock(basePos.x+ix, basePos.y+iy, basePos.z+iz, id, meta, 2);
					}
				}
			}
			//Tile Ents
			int numTileEnts = dataStream.readInt();
			for (int i=0;i<numTileEnts;i++) {
				NBTTagCompound nbt = (NBTTagCompound)NBTBase.readNamedTag(dataStream);
				int px=nbt.getInteger("x")+basePos.x;
				int py=nbt.getInteger("y")+basePos.y;
				int pz=nbt.getInteger("z")+basePos.z;
				
				nbt.setInteger("x",px);
				nbt.setInteger("y",py);
				nbt.setInteger("z",pz);
				TileEntity tileEnt = TileEntity.createAndLoadEntity(nbt);
				world.setBlockTileEntity(px, py, pz, tileEnt); //addTileEntity(tileEnt);
			}
			//Normal Ents
			int numEnts = dataStream.readInt();
			ArrayList<Entity> entstoload=new ArrayList<Entity>();
			for (int i=0;i<numEnts;i++) {
				NBTTagCompound nbt = (NBTTagCompound)NBTBase.readNamedTag(dataStream);
				
				//Delocalize position
				NBTTagList pos = nbt.getTagList("Pos");
				NBTTagDouble posx = (NBTTagDouble)pos.tagAt(0);
				NBTTagDouble posy = (NBTTagDouble)pos.tagAt(1);
				NBTTagDouble posz = (NBTTagDouble)pos.tagAt(2);
				posx.data=posx.data+basePos.x;
				posy.data=posy.data+basePos.y;
				posz.data=posz.data+basePos.z;
				
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
