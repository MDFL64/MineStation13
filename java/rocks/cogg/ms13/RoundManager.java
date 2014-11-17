package rocks.cogg.ms13;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import paulscode.sound.FilenameURL;

//import cpw.mods.fml.common.ITickHandler;
//import cpw.mods.fml.common.TickType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

enum RoundState {
	WORLD_GENERATED
}

public class RoundManager { // implements ITickHandler {
	RoundState rs;
	
	public RoundManager() {
		
	}
	
	public void newRound() {
		System.out.println("Ending this round...");
		
		System.out.println("	-Ejecting players.");
		
		WorldServer world = MinecraftServer.getServer().worldServerForDimension(ModMinestation.dimensionAsteroidId);
		
		Iterator i = world.playerEntities.iterator();
		
		while (i.hasNext()) {
			EntityPlayerMP ply = (EntityPlayerMP)i.next();
			i.remove();
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(ply,0,new TeleporterSpaceStation());
		}
		
		System.out.println("	-Unloading dimension...");
		
		DimensionManager.unregisterDimension(ModMinestation.dimensionAsteroidId);
		DimensionManager.unloadWorld(ModMinestation.dimensionAsteroidId);
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				System.out.println("	-Deleting dimension files.");
				
				File saveDir = DimensionManager.getCurrentSaveRootDirectory();
				File dimDir = new File(saveDir,"DIM"+ModMinestation.dimensionAsteroidId);
				
				deleteDir(dimDir);
			}
		};
		new Timer().schedule(task,5000);
	}
	
	private static void deleteDir(File dir) {
		File[] children = dir.listFiles();
		for (File child:children) {
			if (child.isDirectory()) {
				deleteDir(child);
			} else {
				child.delete();
			}
		}
		dir.delete();
	}
	/*
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		//TEEMO
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "Spacestation Round Manager";
	}*/
}
