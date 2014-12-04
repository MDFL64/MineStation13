package rocks.cogg.ms13.mobsys;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
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

enum EMobAbilityType {
	
}

/**
 * Injects our new inventory classes into the player, and forces the player to use the new GUI.
 */
public class InventoryInjector { //this should probably be networked.
	//We should listen for players being created, inject the default inventory.
	public static void inject(EntityPlayer ply,EInventoryType t) {
		//dont worry about the type for now.
		InventoryMobBase inv = new InventoryMobHumanoid();
		ContainerMobBase c = new ContainerMobBase(inv);
		
		inv.player = ply;
		
		ply.inventory = inv;
		ply.inventoryContainer = c;
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
 * We will re-use slots from the vanilla inventory:
 *		mainInventory->Hotbar used for hands and special abilities. Rest of the inventory is for clothing, etc.
 *		armorInventory->Outer suits/armor for players, 
 *
 * Keep in mind that this is the base class, intended to be used in stuff like ghosts and the AI...
 * subclasses should do the fun stuff. (Actually we might just use it for everything.)
 * 
 */

abstract class InventoryMobBase extends InventoryPlayer {
	/** The ability type that the mob uses. Currently not used. */
	private EMobAbilityType abilityType;
	/** The width of the hotbar that can be freely used by base code and by the player */
	private int freeHotbarWidth;
	
	public InventoryMobBase() {
		super(null);  
	}
	
	public abstract void setupContainer(ContainerMobBase c);
	
	public int getFullHotbarWidth() {
		return abilityType==null?freeHotbarWidth:9;
	}
	
	public void setFreeHotbarWidth(int w) {
		if (w<0)
			w=0;
		else if (w>4)
			w=4;
		freeHotbarWidth=w; //TODO make sure items are in right slots, networking
	} //TODO base this shit off of an item in a specific slot?
	
	public int getFreeHotbarWidth() {
		return freeHotbarWidth;
	}
	
	/**
     * Returns the item stack currently held by the player.
     */
	@Override
    public ItemStack getCurrentItem()
    {
    	return this.currentItem < getFullHotbarWidth() && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    /**
     * Returns the first item stack that is empty.
     */
	@Override
    public int getFirstEmptyStack()
    {
		for (int i = 0; i < freeHotbarWidth; ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }
	
	/**
     * Switch the current item to the next one or the previous one
     * Used for the hotbar.
     * The way this was implemented before was somehow even dumber than the way I did it!
     */
	@Override
    @SideOnly(Side.CLIENT)
    public void changeCurrentItem(int i) //TODO arbitrary slots can still be selected with number keys.
    {
		int hotbarwidth = getFullHotbarWidth();
        if (hotbarwidth==0)
        	return;
		
		if (i > 0) {
			if (currentItem>=hotbarwidth-1)
				currentItem=0;
			else
				currentItem++;
		} else if (i < 0) {
			if (currentItem<=0)
				currentItem=hotbarwidth-1;
			else
				currentItem--;
		}
    }
	
	@Override
    public int getInventoryStackLimit()
    {
		return 16;
    }
	
	//Should we implement this? -> isItemValidForSlot 
}

class InventoryMobHumanoid extends InventoryMobBase {
	public InventoryMobHumanoid() {
		setFreeHotbarWidth(2);
	}

	@Override
	public void setupContainer(ContainerMobBase c) {
		// TODO Auto-generated method stub
	}
}

/**
 * This is the most basic container in the mod.
 * Like the inventory class, it is shared. It controls actual item transfers and works hand-in-hand with the GUI.
 * 
 * Unlike the inventory class, we will not be making subclasses for every mob type.
 * This is because subclasses must be used for containers that interact with OTHER inventories.
 * We may also choose to make this abstract and make specific classes for individual mobs anyway...
 */
class ContainerMobBase extends Container {
	public ContainerMobBase(InventoryMobBase inv) {
		for (int i=0;i<9;i++) //TODO special slot type that filters stuff based on hotbar slot type...
			addSlotToContainer(new Slot(inv,i,8+i*18,142));
		inv.setupContainer(this);
	}
	
	public void insertSlot(Slot s) {
		addSlotToContainer(s);
	}
	
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