package net.c0gg.ms13;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class MsInvGui extends GuiInventory {
	private float xSize_lo;
	private float ySize_lo;

	public MsInvGui(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
		xSize=226;
		ySize=185;
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float)par1;
        this.ySize_lo = (float)par2;
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {}
	
	private static final ResourceLocation guiTexture = new ResourceLocation("ms13:textures/gui/inventory.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        int k = this.guiLeft;
        int l = this.guiTop;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        //Draws the player on the GUI
        func_110423_a(k + 105, l + 110, 50, (float)(k + 105) - this.xSize_lo, (float)(l + 30) - this.ySize_lo, this.mc.thePlayer);
    }
}