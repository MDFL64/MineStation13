package net.c0gg.ms13;

import java.util.HashMap;
import java.util.Map.Entry;

import joptsimple.util.KeyValuePair;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class AtmosDebugger {
	private static AtmosDebugger instance=new AtmosDebugger();
	public static HashMap<ChunkPosition, Integer> map= new HashMap<ChunkPosition, Integer>();
	
	public static void start() {
		MinecraftForge.EVENT_BUS.register(instance);
	}
	
	public static void stop() {
		MinecraftForge.EVENT_BUS.unregister(instance);
		map.clear();
	}
	
	@SubscribeEvent
    public void onDraw(RenderWorldLastEvent event) {
		Vec3 pos = Minecraft.getMinecraft().thePlayer.getPosition(0);
		
		GL11.glPushMatrix();
		GL11.glTranslated(-pos.xCoord+.5,-pos.yCoord+.5,-pos.zCoord+.5);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		for (Entry<ChunkPosition, Integer> entry: map.entrySet()) {
			int c=entry.getValue().intValue();
			int mx=entry.getKey().chunkPosX;
			int my=entry.getKey().chunkPosY;
			int mz=entry.getKey().chunkPosZ;
			
			GL11.glColor3ub((byte)((c>>16)&255),(byte)((c>>8)&255),(byte)(c&255));
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(mx+.5f,my+.5f,mz+.5f);
			GL11.glVertex3f(mx-.5f,my-.5f,mz-.5f);
			GL11.glVertex3f(mx-.5f,my+.5f,mz+.5f);
			GL11.glVertex3f(mx+.5f,my-.5f,mz-.5f);
			GL11.glVertex3f(mx+.5f,my-.5f,mz+.5f);
			GL11.glVertex3f(mx-.5f,my+.5f,mz-.5f);
			GL11.glVertex3f(mx+.5f,my+.5f,mz-.5f);
			GL11.glVertex3f(mx-.5f,my-.5f,mz+.5f);
			GL11.glEnd();
		}
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glPopMatrix();
    }
}
