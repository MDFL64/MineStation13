package net.c0gg.ms13;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

//Currently does not do anything

public class MsInvSlot extends Slot {
	private Class itemRestriction;
	private MsInvSlot parent;
	private ArrayList<MsInvSlot> children;
	
	public MsInvSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		children= new ArrayList<MsInvSlot>();
	}
	
	public MsInvSlot(IInventory par1iInventory, int par2, int par3, int par4, Class restriction) {
		this(par1iInventory, par2, par3, par4);
		itemRestriction= restriction;
	}
	
	public MsInvSlot(IInventory par1iInventory, int par2, int par3, int par4, MsInvSlot parentSlot) {
		this(par1iInventory, par2, par3, par4);
		parent= parentSlot;
		parent.children.add(this);
	}
	
	public MsInvSlot(IInventory par1iInventory, int par2, int par3, int par4, Class restriction, MsInvSlot parentSlot) {
		this(par1iInventory, par2, par3, par4, parentSlot);
		itemRestriction= restriction;
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
		if (itemRestriction==null || !itemRestriction.isInstance(par1ItemStack.getItem()))
			return false;
		
		if (parent!=null && parent.getStack()==null)
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
		
		//TODO eject rather than delete
		if (this.getStack()==null) {
			for (MsInvSlot slot: children) {
				//if (FMLCommonHandler.instance().getEffectiveSide()==Side.SERVER) { this doesnt seem necessary...leaving it here in case it is needed for mp
					ItemStack childstack = slot.getStack();
					if (childstack!=null)
						((MsInvInventory)inventory).player.dropItem(childstack.getItem(), childstack.stackSize); //~Pdan
				//} 
				slot.putStack(null);
			}
		}
	}
}
