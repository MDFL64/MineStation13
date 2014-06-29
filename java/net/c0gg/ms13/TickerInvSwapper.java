package net.c0gg.ms13;

import java.util.EnumSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;

//import cpw.mods.fml.common.ITickHandler;
//import cpw.mods.fml.common.TickType;

public class TickerInvSwapper { //CLIENTSIDE
	@SubscribeEvent
	public void onTick(TickEvent tick) {
		if (tick.phase==Phase.END && tick.type==Type.CLIENT) {
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if (gui!=null&&gui.getClass()==GuiInventory.class) {
				Minecraft.getMinecraft().displayGuiScreen(new MsInvGui(Minecraft.getMinecraft().thePlayer));
			}
		}
	}
}
	
	
/*
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		/*GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui!=null&&gui.getClass()==GuiInventory.class) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryMs(Minecraft.getMinecraft().thePlayer));
		}*
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui!=null&&gui.getClass()==GuiInventory.class) {
			Minecraft.getMinecraft().displayGuiScreen(new MsInvGui(Minecraft.getMinecraft().thePlayer));
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "Spacestation Client Extensions";
	}*/
