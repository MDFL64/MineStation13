package net.c0gg.ms13;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IIcon; //YOLO ~Pdan;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;

public class ItemIdCard extends Item {
	private IIcon[] icons=new IIcon[4];
	
	public ItemIdCard() {
		super();
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(ModMinestation.tabSpacestation);
		
		//ItemStack it = new ItemStack(par1, 1, 0);
		//it.setItemName("Gooby");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack itemStack)
    {
		NBTTagCompound nbt = itemStack.stackTagCompound;
		if (nbt == null||!nbt.hasKey("idName")) {
			return super.getItemStackDisplayName(itemStack);
		}
		
		return nbt.getString("idName")+"'s "+ super.getItemStackDisplayName(itemStack); //super.getItemDisplayName(itemStack);
    }
	
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) //YOLO ~Pdan
    {
        par3List.add(new ItemStack(par1, 1, 0));
        par3List.add(new ItemStack(par1, 1, 1));
        par3List.add(new ItemStack(par1, 1, 2));
        par3List.add(new ItemStack(par1, 1, 3));
        
        ItemStack test = new ItemStack(par1, 1, 0);
        
        test.setTagInfo("idName",new NBTTagString("Gooby")); //YOLO ~Pdan
        
        NBTTagList accessKeys = new NBTTagList();
        accessKeys.appendTag(new NBTTagString("teemo")); //YOLO ~Pdan
        test.setTagInfo("idAccessKeys",accessKeys);
        
        par3List.add(test);
    }
	
	@Override
	public IIcon getIconFromDamage(int dmg)
    {
    	return icons[dmg];
    }
	
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
    {
		icons[0] = par1IconRegister.registerIcon("ms13:idCard.crew");
		icons[1] = par1IconRegister.registerIcon("ms13:idCard.command");
		icons[2] = par1IconRegister.registerIcon("ms13:idCard.captain");
		icons[3] = par1IconRegister.registerIcon("ms13:idCard.centcom");
    }
}
