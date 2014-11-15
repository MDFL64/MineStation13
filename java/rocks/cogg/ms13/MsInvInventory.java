package rocks.cogg.ms13;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.MinecraftForge;

/* The shared part of the inventory system.
 * This is the only part that runs on the server.
 * It can be safely based off of the player's inventory class.
 * 
 * 
 */


//WARNING -- THE NEW INVENTORY WILL BE EMPTY..NO LOADING!

public class MsInvInventory extends InventoryPlayer {
	private int usableSlotCount;
	public TexturePlayer texture;
	
	public MsInvInventory(EntityPlayer player) {
		super(player);
		
		//Hotbar - Worn Stuff - Pack
		mainInventory = new ItemStack[9+16+24];
		//We don't actually use this
		armorInventory= new ItemStack[4];
		
		usableSlotCount=2;
		
		if (player.worldObj.isRemote)
			texture = new TexturePlayer(this);
	}
	
	public int getUsableSlots() {
		return usableSlotCount;
	}
	
	@Override
	public int getSizeInventory()
    {
        return mainInventory.length + armorInventory.length;
    }
	
	//The following four functions are all for inserting items into the inventory.
	//They were mostly copied from the base class and could probably be simplified dramatically.
	//Especially considering how the inventory is way simpler and items probably shouldn't be picked up automatically
	@Override
	public boolean addItemStackToInventory(ItemStack par1ItemStack) {
        if (par1ItemStack == null)
        {
            return false;
        }
        else if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else
        {
            try
            {
                int i;

                if (par1ItemStack.isItemDamaged())
                {
                    i = this.getFirstEmptyStack();

                    if (i >= 0)
                    {
                        this.mainInventory[i] = ItemStack.copyItemStack(par1ItemStack);
                        this.mainInventory[i].animationsToGo = 5;
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else if (this.player.capabilities.isCreativeMode)
                    {
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    do
                    {
                        i = par1ItemStack.stackSize;
                        par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack);
                    }
                    while (par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < i);

                    if (par1ItemStack.stackSize == i && this.player.capabilities.isCreativeMode)
                    {
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return par1ItemStack.stackSize < i;
                    }
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item name", Integer.valueOf(par1ItemStack.getDisplayName())); //Yolo ~Pdan
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(par1ItemStack.getItemDamage()));
                //crashreportcategory.addCrashSectionCallable("Item name", new CallableItemName(this, par1ItemStack));
                throw new ReportedException(crashreport);
            }
        }
    }
	
	private int storePartialItemStack(ItemStack par1ItemStack)
    {
        ItemStack i = par1ItemStack;
        Item v = i.getItem();
        int j = par1ItemStack.stackSize;
        int k;

        if (par1ItemStack.getMaxStackSize() == 1)
        {
            k = this.getFirstEmptyStack();

            if (k < 0)
            {
                return j;
            }
            else
            {
                if (this.mainInventory[k] == null)
                {
                    this.mainInventory[k] = ItemStack.copyItemStack(par1ItemStack);
                }

                return 0;
            }
        }
        else
        {
            k = this.storeItemStack(par1ItemStack);

            if (k < 0)
            {
                k = this.getFirstEmptyStack();
            }

            if (k < 0)
            {
                return j;
            }
            else
            {
                if (this.mainInventory[k] == null)
                {
                    this.mainInventory[k] = new ItemStack(v, 0, par1ItemStack.getItemDamage());

                    if (par1ItemStack.hasTagCompound())
                    {
                        this.mainInventory[k].setTagCompound((NBTTagCompound)par1ItemStack.getTagCompound().copy());
                    }
                }

                int l = j;

                if (j > this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize)
                {
                    l = this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize;
                }

                if (l > this.getInventoryStackLimit() - this.mainInventory[k].stackSize)
                {
                    l = this.getInventoryStackLimit() - this.mainInventory[k].stackSize;
                }

                if (l == 0)
                {
                    return j;
                }
                else
                {
                    j -= l;
                    this.mainInventory[k].stackSize += l;
                    this.mainInventory[k].animationsToGo = 5;
                    return j;
                }
            }
        }
    }
	
	private int storeItemStack(ItemStack par1ItemStack)
    {
        Item v = par1ItemStack.getItem();
        for (int i = 0; i < usableSlotCount; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == v && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getItemDamage() == par1ItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i], par1ItemStack))
            {
                return i;
            }
        }

        return -1;
    }
	
	public int getFirstEmptyStack()
    {
        for (int i = 0; i < usableSlotCount; ++i)
        {
            if (this.mainInventory[i] == null)
            {
                return i;
            }
        }

        return -1;
    }
	
	//Selecting which hotbar slot to use - push back to our usable slots
	@Override
	public void changeCurrentItem(int par1)
    {
        super.changeCurrentItem(par1);
        
        if (currentItem==8) {
        	currentItem = 1;
        } else {
        	currentItem %= usableSlotCount;
        }
    }
	
	//TODO fix
	public boolean hasAccessKey(String accessType) {
		ItemStack card = mainInventory[0];
		
		if (card==null||card.getItem()!=ModMinestation.itemIdCard||card.stackTagCompound == null||!card.stackTagCompound.hasKey("idAccessKeys")) {
			return false;
		}
		
		NBTTagCompound nbt = card.stackTagCompound;
		
		/*NBTTagList accessKeys = nbt.getTagList("idAccessKeys"); TODO they did something stupid to NBT lists that I don't want to deal with at the moment.
		for (int i=0;i<accessKeys.tagCount();i++) {
			if (((NBTTagString)accessKeys.tagAt(i)).data.equals(accessType))
				return true;
		}*/
		
		return false;
	}
}