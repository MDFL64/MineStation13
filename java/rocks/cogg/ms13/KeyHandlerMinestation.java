package rocks.cogg.ms13;

import java.util.EnumSet;

import javax.swing.plaf.basic.BasicSplitPaneUI.KeyboardUpLeftHandler;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
//TODO this is now an event listener! Clientside ONLY!
public class KeyHandlerMinestation {
	private static final KeyBinding[] keyBindings = {new KeyBinding("key.ms13.grab",Keyboard.KEY_G, "key.ms13._category")};
	private static final boolean[] repeatings = {false};
	public KeyHandlerMinestation() {
		for (KeyBinding key : keyBindings) {
			ClientRegistry.registerKeyBinding(key);
		}
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (keyBindings[0].isPressed()) { //This new system is just stupid.
			MovingObjectPosition obj = Minecraft.getMinecraft().objectMouseOver;
			PacketHandlerMinestation.clSendPlyGrab(obj!=null?obj.entityHit:null);
		}
	}
}
