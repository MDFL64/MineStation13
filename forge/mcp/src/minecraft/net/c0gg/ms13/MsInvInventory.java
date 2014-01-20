package net.c0gg.ms13;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

//WARNING -- THE NEW INVENTORY WILL BE EMPTY..NO LOADING!

public class MsInvInventory extends InventoryPlayer {
	public MsInvInventory(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
		armorInventory= new ItemStack[12];
	}
	
	@Override
	public int getSizeInventory()
    {
        return mainInventory.length + armorInventory.length;
    }
	
	public boolean hasAccessKey(String accessType) {
		ItemStack card = mainInventory[0];
		
		if (card==null||card.itemID!=ModMinestation.itemIdCard.itemID||card.stackTagCompound == null||!card.stackTagCompound.hasKey("idAccessKeys")) {
			return false;
		}
		
		NBTTagCompound nbt = card.stackTagCompound;
		
		NBTTagList accessKeys = nbt.getTagList("idAccessKeys");
		for (int i=0;i<accessKeys.tagCount();i++) {
			if (((NBTTagString)accessKeys.tagAt(i)).data.equals(accessType))
				return true;
		}
		
		return false;
	}
}