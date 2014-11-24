package rocks.cogg.ms13.mobsys;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/*
 * This code is a huge WIP. It will replace the old inventory code.
 * By birdbrainswagtrain.
 */

//This will be a public class

enum EInventoryType {
	VANILLA, CREATIVE, //(?)
	BLANK
}

/**
 * Injects our new inventory classes into the player, and forces the player to use the new GUI.
 */
public class InventoryInjector { //this should probably be networked.
	//We should listen for players being created, inject the default inventory.
	public static void inject(EntityPlayer ply,EInventoryType t) {
		//dont worry about the type for now.
		InventoryMobBase inv = new InventoryMobBase(ply,ply.inventory);
		ContainerMobBase c = new ContainerMobBase();
		
		ply.inventory = inv;
		ply.inventoryContainer = new ContainerMobBase();
		ply.closeScreen();
		//inv.setContainer(c); ... do not do this.
	}
}

/**
 * This is the part that actually stores items.
 * Both it and the container are shared between the client and server (as far as I am aware).
 * Both are injected into the player.
 * 
 * Although this is a subclass of InventoryPlayer, we will be using it in both Player and Non-Player mobs.
 * It should be safe to do so.
 * 
 * We remove a lot of base functionality from this-- there shouldn't be any need to
 * randomly shove items into the inventory in our game.
 * 
 * We will re-use the following parts of the inventory:
 *		mainInventory->Only the hotbar, used for hands and special abilities.
 *		armorInventory->Outer suits/armor for players, 
 *
 * Keep in mind that this is the base class, intended to be used in stuff like ghosts and the AI...
 * subclasses should do the fun stuff.
 * 
 * (?) Might just recycle some of the main inventory for everything else... would simplify saving/slot manipulation/everything.
 */

class InventoryMobBase extends InventoryPlayer {
	//private ContainerMobBase container;
	
	public InventoryMobBase(EntityPlayer par1EntityPlayer,InventoryPlayer old) {
		super(par1EntityPlayer);
		mainInventory = new ItemStack[9];
	}
	
	private int getRealHotbarSize() {
		return 2;
	}
	
	/**
     * Returns the item stack currently held by the player.
     */
	@Override
    public ItemStack getCurrentItem()
    {
    	return this.currentItem < getRealHotbarSize() && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    /**
     * STUB!
     * Returns the first item stack that is empty.
     * We don't need this functionality.
     */
	@Override
    public int getFirstEmptyStack()
    {
        return -1;
    }
	
	/**
	 * STUB!
	 * Clears inventory using some kind of filter.
	 * Returns number of items removed.
	 * Only used in one chat command, we don't need it.
	 */
	@Override
	public int clearInventory(Item p_146027_1_, int p_146027_2_) {
		return 0;
	}
	
	/**
     * Switch the current item to the next one or the previous one
     * Used for the hotbar.
     * The way this was implemented before was somehow even dumber than the way I did it!
     */
	@Override
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int i)
    {
		int hotbarSize = getRealHotbarSize();
        if (hotbarSize==0)
        	return;
		
		if (i > 0) {
			if (this.currentItem==hotbarSize-1)
				this.currentItem=0;
			else
				this.currentItem++;
		} else if (i < 0) {
			if (i==0)
				this.currentItem=hotbarSize-1;
			else
				this.currentItem--;
		}
    }
	
	/**
	 * STUB!
     * Remove an item, might need to implement or revert to base functionality later...
     */
	@Override
    public boolean consumeInventoryItem(Item p_146026_1_)
    {
        return false;
    }
	
	/**
	 * STUB!
     * Checks if a specified Item is inside the inventory
     * Might need to implement or fevert later...
     */
    @Override
	public boolean hasItem(Item p_146028_1_)
    {
        return false;
    }
    
    /**
     * STUB!
     * Pretty safe to assume we don't need this.
     */
    @Override
    public boolean addItemStackToInventory(final ItemStack par1ItemStack)
    {
    	return false;
    }
    
    /**
     * STUB!
     * "Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack."
     * 
     * Will probably need this later...
     */
    @Override
    public ItemStack decrStackSize(int par1, int par2) {
    	return null;
    }
    
    /**
     * STUB!
     * 
     * "When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI."
     * 
     * This is probably important.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
       return null;
    }

    /**
     * STUB!
     * 
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     * 
     * This is almost certainly important.
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        
    }
	
	/*
	 * cleared up to addItemStackToInventory
	 */
	
	
}

/**
 * This is the most basic container in the mod.
 * Like the inventory class, it is shared. It controls actual item transfers and works hand-in-hand with the GUI.
 * 
 * Unlike the inventory class, we will not be making subclasses for every mob type.
 * This is because subclasses must be used for containers that interact with OTHER inventories.
 * We may also choose to make this abstract and make specific classes for individual mobs anyway...
 */
//Container to use for self-inventory
class ContainerMobBase extends Container {
	/*public ContainerBase(EntityPlayer entPlayer,boolean isLocal) {
		figure out what the hell to do here
	}*/
	
	/**
	 * This is called every tick or so to see if we should keep the container open. Very useful!
	 */
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		// TODO verify this is the right thing to be doing.
		//System.out.println("test");
		return true;
	}
	
	/**
     * This is for loading the inventory or something.
     */
    @Override
	@SideOnly(Side.CLIENT)
    public void putStacksInSlots(ItemStack[] par1ArrayOfItemStack)
    {
    	/* we had to override this to prevent a crash. should probably make it perform as advertised on subclasses...
    	for (int i = 0; i < par1ArrayOfItemStack.length; ++i)
        {
            this.getSlot(i).putStack(par1ArrayOfItemStack[i]);
        }*/
    }
}