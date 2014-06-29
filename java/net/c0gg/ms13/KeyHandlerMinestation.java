package net.c0gg.ms13;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class KeyHandlerMinestation extends KeyHandler { //wat?
	private static final KeyBinding[] keyBindings = {new KeyBinding("Grab",Keyboard.KEY_G, "key.minestation.teemo")}; //TODO:rename "teemo" ~Pdan
	private static final boolean[] repeatings = {false};
	public KeyHandlerMinestation() {
		super(keyBindings, repeatings);
	}
	
	//@Override ~Pdan
	public String getLabel() {
		return "Minestation";
	}

	//@Override ~Pdan
	public void keyDown(EnumSet<TickType> types,KeyBinding kb,boolean tickEnd, boolean isRepeat) {
		if (tickEnd) return;
		
		if (kb.keyDescription=="Grab") {
			MovingObjectPosition obj = Minecraft.getMinecraft().objectMouseOver;
			PacketHandlerMinestation.clSendPlyGrab(obj!=null?obj.entityHit:null);
		}
	}

	//@Override Damn mass yoloing up in here ~Pdan
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (tickEnd) return;
		
		
	}

	//@Override Damn mass yoloing up in here ~Pdan
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}
}
