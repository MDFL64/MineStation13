package net.c0gg.ms13;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotArmorMs extends Slot {
	final int armorType;
	
	final EntityPlayer player;
	
	public SlotArmorMs(InventoryPlayer inventory, int index, int x, int y,int armorType) {
		super(inventory, index, x, y);
		this.armorType= armorType;
		this.player=inventory.player;
	}
	
	@Override
	public int getSlotStackLimit()
    {
        return 1;
    }
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
        Item item = (par1ItemStack == null ? null : par1ItemStack.getItem());
        return item != null && item.isValidArmor(par1ItemStack, armorType, player);
    }
}
