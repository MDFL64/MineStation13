package net.c0gg.ms13;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockAirlockFrame implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,RenderBlocks renderer) {}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,Block block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
        boolean hpos=(meta&1)==1;
        boolean vpos=(meta&2)==2;
        boolean dir=(meta&4)==4;
        boolean closed=(meta&8)==8;
        
        if (!vpos) {//bottom
        	renderer.setRenderBounds(0,0,0,1,.1f,1);
        } else {//top
        	renderer.setRenderBounds(0,.9f,0,1,1,1);
        }
        renderer.renderStandardBlock(block, x, y, z);
        
        if (!dir) {
        	if (!hpos) {
        		renderer.setRenderBounds(0,0,0,.1f,1,1);
        	} else {
        		renderer.setRenderBounds(.9f,0,0,1,1,1);
        	}
        } else {
        	if (!hpos) {
        		renderer.setRenderBounds(0,0,0,1,1,.1f);
        	} else {
        		renderer.setRenderBounds(0,0,.9f,1,1,1);
        	}
        }
        renderer.renderStandardBlock(block, x, y, z);
        
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return ModMinestation.renderBlockAirlockFrameId;
	}
}
