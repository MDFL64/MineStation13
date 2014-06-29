package net.c0gg.ms13;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

public class PlayerUtil {
	public static MovingObjectPosition lookTraceBlocks(EntityPlayer ply,int dist) {
		Vec3 startPos = ply.getPosition(1).addVector(0,ply.getEyeHeight(),0);
		Vec3 look = ply.getLook(1);
		Vec3 endPos = startPos.addVector(look.xCoord*dist,look.yCoord*dist,look.zCoord*dist);
		
		return ply.worldObj.rayTraceBlocks(startPos,endPos,true);
	}
	
	public static boolean isInRange(EntityPlayerMP ply,ChunkPosition pos) {
		return ply.getDistanceSq(pos.x+.5d,pos.y+.5d,pos.z+.5d) < 36d;
	}
}
