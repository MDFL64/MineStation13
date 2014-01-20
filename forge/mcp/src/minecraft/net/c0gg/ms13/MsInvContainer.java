package net.c0gg.ms13;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class MsInvContainer extends Container {
	/** The crafting matrix inventory. */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
	
    /** Determines if inventory manipulation should be handled. */
    public boolean isLocalWorld = false;
    protected final EntityPlayer thePlayer;
    
	public MsInvContainer(InventoryPlayer inventory, boolean par2, EntityPlayer par3EntityPlayer) {
		this.isLocalWorld = par2;
        this.thePlayer = par3EntityPlayer;
        
        addSlotToContainer(new SlotCrafting(inventory.player, this.craftMatrix, this.craftResult, 0, 224, 36));
        
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 168 + j * 18, 26 + i * 18));
            }
        }
        
        //Armor-Vanilla
        addSlotToContainer(new SlotArmorMs(inventory,39, 88, 8,0)); // 0 helm
        addSlotToContainer(new SlotArmorMs(inventory,38, 88, 26,1)); // 1 suit body
        addSlotToContainer(new SlotArmorMs(inventory,37, 88, 44,2)); // 2 suit legs
        addSlotToContainer(new SlotArmorMs(inventory,36, 88, 62,3)); // 3 shoes

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                addSlotToContainer(new Slot(inventory, j + (i + 1) * 9, 88 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            addSlotToContainer(new Slot(inventory, i, 88 + i * 18, 142));
        }
        
        //Armor-Custom
        addSlotToContainer(new SlotArmorMs(inventory,40, 70, 8,4)); // 4 face
        addSlotToContainer(new SlotArmorMs(inventory,41, 70, 26,5)); // 5 shirt
        addSlotToContainer(new SlotArmorMs(inventory,42, 70, 44,6)); // 6 pants
        
        addSlotToContainer(new SlotArmorMs(inventory,43, 52, 8,7)); // 7 eye
        addSlotToContainer(new SlotArmorMs(inventory,44, 52, 26,8)); // 8 back
        addSlotToContainer(new SlotArmorMs(inventory,45, 52, 44,9)); // 9 belt
        
        addSlotToContainer(new SlotArmorMs(inventory,46, 34, 8,10)); // 10 eye
        addSlotToContainer(new SlotArmorMs(inventory,47, 34, 35,11)); // 11 back
        
        onCraftMatrixChanged(this.craftMatrix);
	}
	
	/**
     * Callback for when the crafting matrix is changed.
     */
	@Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

            if (itemstack != null)
            {
                par1EntityPlayer.dropPlayerItem(itemstack);
            }
        }

        this.craftResult.setInventorySlotContents(0, (ItemStack)null);
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 == 0)
            {
                if (!this.mergeItemStack(itemstack1, 17, 53, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (par2 >= 1 && par2 < 5)
            {
                if (!this.mergeItemStack(itemstack1, 17, 53, false))
                {
                    return null;
                }
            }
            else if (par2 >= 5 && par2 < 17)
            {
                if (!this.mergeItemStack(itemstack1, 17, 53, false))
                {
                    return null;
                }
            }
            else if (itemstack.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)itemstack.getItem()).armorType)).getHasStack())
            {
                int j = 5 + ((ItemArmor)itemstack.getItem()).armorType;

                if (!this.mergeItemStack(itemstack1, j, j + 1, false))
                {
                    return null;
                }
            }
            else if (par2 >= 17 && par2 < 44)
            {
                if (!this.mergeItemStack(itemstack1, 44, 53, false))
                {
                    return null;
                }
            }
            else if (par2 >= 44 && par2 < 53)
            {
                if (!this.mergeItemStack(itemstack1, 17, 44, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 17, 53, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }
	
	//Not sure what this does, but
	@Override
	public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }
}