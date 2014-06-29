package net.c0gg.ms13;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCrowbar extends Item {
	public ItemCrowbar(int id) {
//		super(id);
		
		setMaxStackSize(1);
		setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int dir, float lx, float ly, float lz)
    {
		Block block = world.getBlock(x,y,z);
        
        if (block instanceof ToolableCrowbar) {
        	((ToolableCrowbar)block).onUseCrowbar(world, x, y, z, dir);
        	
        	return true;
        }
		
		return false;
    }
}
