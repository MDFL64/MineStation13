package net.c0gg.ms13;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

//Currently does not do anything

public class MsInvSlot extends Slot {
	private Class itemRestriction;
	
	public MsInvSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
	public MsInvSlot(IInventory par1iInventory, int par2, int par3, int par4, Class restriction) {
		this(par1iInventory, par2, par3, par4);
		itemRestriction= restriction;
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
		if (itemRestriction==null || !itemRestriction.isInstance(par1ItemStack.getItem()))
			return false;
		
		return true;
    }
	
	@Override
	public void onSlotChanged() {
		//TODO client-server networking
		//TODO check if item is clothing item so we know if we should refresh player texture
		if (((MsInvInventory)inventory).texture!=null) {
			((MsInvInventory)inventory).texture.rebuild();
		}
	}
}
