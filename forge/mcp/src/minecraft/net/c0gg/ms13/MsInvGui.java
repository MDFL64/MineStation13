package net.c0gg.ms13;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class MsInvGui extends GuiInventory {
	private float xSize_lo;
	private float ySize_lo;

	public MsInvGui(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
		xSize=256;
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float)par1;
        this.ySize_lo = (float)par2;
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), 170, 16, 4210752);
    }
	
	private static final ResourceLocation guiTexture = new ResourceLocation("ms13:textures/gui/inventory.png");
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        int k = this.guiLeft;
        int l = this.guiTop;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        //TODO draw the damn player on the damn gui...I think they changed the function and MCP has yet to catch up.
        //drawPlayerOnGui(this.mc, k + 130, l + 75, 30, (float)(k + 130) - this.xSize_lo, (float)(l + 75 - 50) - this.ySize_lo);
    }
}