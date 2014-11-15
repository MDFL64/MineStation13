package rocks.cogg.ms13;

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

/**
 * Item for an airlock
 * 
 * @author Parakeet
 *
 */
public class ItemAirlockDoor extends Item {
	private IIcon[] icons1=new IIcon[AirlockType.values().length-1];
	private IIcon[] icons2=new IIcon[AirlockType.values().length-1];
	
	public ItemAirlockDoor(int par1) {
//		super(par1);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int dir, float lx, float ly, float lz)
    {
		int dmg = stack.getItemDamage();
		boolean used = ((BlockAirlockFrame)ModMinestation.blockAirlockFrame).addDoor(world, x, y, z, dmg<100,AirlockType.values()[dmg%100+1]);
		
		if (used && !world.isRemote) {
			stack.splitStack(1);
		}
		
		return used;
    }
	
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i=0;i<AirlockType.values().length-1;i++) {
        	par3List.add(new ItemStack(par1, 1, i));
        	par3List.add(new ItemStack(par1, 1, 100+i));
        }
    }
	
	@Override
	public IIcon getIconFromDamage(int dmg)
    {
		if(dmg>=100) {
			return icons1[dmg-100];
		}
    	return icons2[dmg];
    }
	
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
    {
		for (int i=1;i<AirlockType.values().length;i++) {
			icons1[i-1] = par1IconRegister.registerIcon("ms13:airlockdoor."+AirlockType.values()[i]+"1");
			icons2[i-1] = par1IconRegister.registerIcon("ms13:airlockdoor."+AirlockType.values()[i]+"2");
		}
    }
}
