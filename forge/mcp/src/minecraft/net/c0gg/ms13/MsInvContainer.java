package net.c0gg.ms13;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

/* This is one of the clientside parts of the inventory.
 * It displays the slots on the gui and sends changes to the server.
 * Or something. The first version used a modified COPY of ContainerPlayer,
 * but it should be safe to create a subclass of ContainerPlayer.
 */

public class MsInvContainer extends ContainerPlayer {
	public MsInvSlot slotPants;
	public MsInvSlot slotShirt;
	
	public MsInvContainer(EntityPlayer ply) {
		super(ply.inventory,!ply.worldObj.isRemote,ply);
		
		//Undo what the parent constructor did.
		inventorySlots.clear();
        inventoryItemStacks.clear();
        
        //The entire hotbar must be kept... Too much vanilla code relies on the hotbar having 9 slots.
        //We can probably just make the other x slots unusable.
        for (int i = 0; i < ((MsInvInventory)ply.inventory).getUsableSlots(); ++i) {
            addSlotToContainer(new Slot(ply.inventory, i, 6 + i * 18, 164));
        }
        
        addSlotToContainer(new MsInvSlot(ply.inventory, 9, 6, 6, ItemGloves.class));
        addSlotToContainer(new MsInvSlot(ply.inventory, 10, 26, 6));
        addSlotToContainer(new MsInvSlot(ply.inventory, 11, 46, 6));
        
        addSlotToContainer(new MsInvSlot(ply.inventory, 12, 6, 26));
        addSlotToContainer(new MsInvSlot(ply.inventory, 13, 26, 26));
        addSlotToContainer(new MsInvSlot(ply.inventory, 14, 46, 26));
        
        addSlotToContainer(new MsInvSlot(ply.inventory, 15, 6, 46));
        addSlotToContainer(slotShirt= new MsInvSlot(ply.inventory, 16, 26, 46, ItemShirt.class));
        addSlotToContainer(new MsInvSlot(ply.inventory, 17, 46, 46, ItemIdCard.class, slotShirt));
        
        addSlotToContainer(new MsInvSlot(ply.inventory, 18, 6, 66));
        addSlotToContainer(slotPants= new MsInvSlot(ply.inventory, 19, 26, 66, ItemPants.class));
        addSlotToContainer(new MsInvSlot(ply.inventory, 20, 46, 66));
        
        addSlotToContainer(new MsInvSlot(ply.inventory, 21, 6, 86));
        addSlotToContainer(new MsInvSlot(ply.inventory, 22, 26, 86, ItemShoes.class));
        addSlotToContainer(new MsInvSlot(ply.inventory, 23, 46, 84, Item.class, slotPants));
        
        addSlotToContainer(new MsInvSlot(ply.inventory, 24, 46, 102, Item.class, slotPants));
        
        for (int x = 0;x<12;x++) {
        	for (int y = 0;y<2;y++) { //This will probably use a seperate slot class when it actually works
        		addSlotToContainer(new MsInvSlot(ply.inventory, 25+x+y*12, 6+x*18, 124+y*18));
            }
        }
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
    }
}