package net.c0gg.ms13;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;

public class GuiAirlockSetup extends GuiScreen {
	private TileEntityAirlock entityAirlock;
	private GuiTextField inputField;
	
	private int guiLeft;
	private int guiTop;
	
	private int xSize = 240;
	private int ySize = 40;
	
	public GuiAirlockSetup(TileEntityAirlock airlock) {
		entityAirlock = airlock;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
        
        inputField = new GuiTextField(fontRenderer, guiLeft,guiTop+20, xSize, 12);
        inputField.setFocused(true);
        inputField.setCanLoseFocus(false);
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void updateScreen()
    {
		if (entityAirlock.getFlag(AirlockFlag.ACTIVATED)) {
			mc.displayGuiScreen((GuiScreen)null);
		}
        inputField.updateCursorCounter();
    }
	
	@Override
	protected void keyTyped(char par1, int par2)
    {
		if (inputField.textboxKeyTyped(par1, par2)) {
        	//SWAG
        } else if (par2 == 1) {
        	mc.displayGuiScreen((GuiScreen)null);
        } else if (par2 == Keyboard.KEY_RETURN) {
        	PacketHandlerMinestation.clSendAirlockSetup(entityAirlock,inputField.getText());
        	mc.displayGuiScreen((GuiScreen)null);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.inputField.mouseClicked(par1, par2, par3);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
    	super.drawScreen(par1, par2, par3); //this draws buttons, if we have any
        //GL11.glDisable(GL11.GL_LIGHTING);
    	
        fontRenderer.drawString(StatCollector.translateToLocal("container.airlocksetup"), guiLeft+15, guiTop+5, 0xFFFFFF);
        inputField.drawTextBox();
    }
	
	@Override
	public boolean doesGuiPauseGame()
    {
        return false;
    }
}
