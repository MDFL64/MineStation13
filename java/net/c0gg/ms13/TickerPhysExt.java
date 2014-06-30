package net.c0gg.ms13;

import java.util.EnumSet;
import java.util.HashMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

//import cpw.mods.fml.common.ITickHandler;
//import cpw.mods.fml.common.TickType;
//import cpw.mods.fml.common.network.Player;

//Physical extensions...or something like that.
public class TickerPhysExt { //CLIENTSIDE? SERVERSIDE? NOT SURE -- I THINK IT's SERVERSIDE!
	public static HashMap<Entity,Entity> grabs = new HashMap<Entity,Entity>();
	
	@SubscribeEvent
	public void onTick(WorldTickEvent tick) {
		if (tick.phase==Phase.START) {
			for (Entity grabber:grabs.keySet()) {
				Entity grabbed=grabs.get(grabber);
				
				double mx=grabber.posX-grabbed.posX;
				double my=grabber.posY-grabbed.posY;
				double mz=grabber.posZ-grabbed.posZ;
				
				if ((mx*mx+my*my+mz*mz)>3) {
					grabbed.setVelocity(mx*.1f,my,mz*.1f);
				}
				//Make it impossible to die of fall damage - TODO bad! what if grabber dies of fall damage!?!
				grabbed.fallDistance=0;
			}
		}
	}
	
	
	/*public TickerPhysExt() {
		
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		for (Entity grabber:grabs.keySet()) {
			Entity grabbed=grabs.get(grabber);
			
			double mx=grabber.posX-grabbed.posX;
			double my=grabber.posY-grabbed.posY;
			double mz=grabber.posZ-grabbed.posZ;
			
			if ((mx*mx+my*my+mz*mz)>3) {
				grabbed.setVelocity(mx*.1f,my,mz*.1f);
			}
			//Make it impossible to die of fall damage - TODO bad! what if grabber dies of fall damage!?!
			grabbed.fallDistance=0;
		}
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		//LOL DICKS
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Spacestation Physics Extensions";
	}*/
}