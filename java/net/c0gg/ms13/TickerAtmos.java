package net.c0gg.ms13;

import java.util.EnumSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

//import cpw.mods.fml.common.ITickHandler;
//import cpw.mods.fml.common.TickType;

public class TickerAtmos { //ONLY RUN ON SERVER PLEASE
	/*public TickerAtmos() {
		//TEEMO PLZ
	}*/

	@SubscribeEvent
	public void onTick(WorldTickEvent tick) {
		if (tick.phase==Phase.START)
			AtmosZoner.updateAll();
	}

	/*@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		//LOL DICKS
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "Spacestation Atmos";
	}*/
}