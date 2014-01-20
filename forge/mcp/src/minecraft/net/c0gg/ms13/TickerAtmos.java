package net.c0gg.ms13;

import java.util.EnumSet;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickerAtmos implements ITickHandler {
	public TickerAtmos() {
		//TEEMO PLZ
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		AtmosSystem.updateAll();
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
		return "Spacestation Atmos";
	}
}