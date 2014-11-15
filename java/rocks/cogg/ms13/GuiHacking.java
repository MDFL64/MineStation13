package rocks.cogg.ms13;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiHacking extends GuiScreen {
	private Hackable target;
	
	private int guiLeft;
	private int guiTop;
	
	private int xSize = 152;
	private int ySize = 42;
	
	public GuiHacking(Hackable target) {
		this.target = target;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
	}
	
	@Override
	protected void keyTyped(char par1, int par2)
	{
        if (par2 == 1 || par2 == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
        	mc.displayGuiScreen((GuiScreen)null);
        }
	}
	
	@Override
	protected void mouseClicked(int x, int y, int btn)
    {
        super.mouseClicked(x, y, btn);
        if (btn==0) {
        	x-=guiLeft+5;
        	y-=guiTop+5;
        	
        	if (x<0||y<0) {
        		return;
        	}
        	
        	x/=16;
        	y/=16;
        	
        	if (x>7||y>1) {
        		return;
        	}
        	
        	PacketHandlerMinestation.clSendHack(target,x+y*8);
        }
    }
	
	private static final ResourceLocation guiTexture = new ResourceLocation("ms13:textures/gui/hacking.png");

	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
    	super.drawScreen(par1, par2, par3); //this draws buttons, if we have any
    	
    	this.mc.getTextureManager().bindTexture(guiTexture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        byte wires = target.getWires();
        for (int i=0;i<8;i++) {
        	if ((wires & (1<<i)) !=0) {
        		drawTexturedModalRect(this.guiLeft+5+i*16, this.guiTop+5, 0, 42, 16, 16);
        	}
        }
        
        byte[] indicators = new byte[8];
        target.getIndicators(indicators);
        
        for (int i=0;i<4;i++) {
        	byte ind=indicators[i];
        	if (ind>0) {
        		ind--;
        		drawTexturedModalRect(this.guiLeft+137, this.guiTop+6+i*6, ind*4, 58, 4, 4);
        	}
        }
        for (int i=0;i<4;i++) {
        	byte ind=indicators[i+4];
        	if (ind>0) {
        		ind--;
        		drawTexturedModalRect(this.guiLeft+143, this.guiTop+6+i*6, ind*4, 58, 4, 4);
        	}
        }
    }
	
	@Override
	public boolean doesGuiPauseGame()
    {
        return false;
    }
}
