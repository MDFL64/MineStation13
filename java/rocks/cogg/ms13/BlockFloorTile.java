package rocks.cogg.ms13;

import java.util.List;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister; 
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon; //YOLO ~Pdan;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFloorTile extends Block implements ToolableCrowbar {
	public static final String[] subTypes = new String[] {"grey", "white", "black", "red","green","blue","yellow","purple","darkred","darkgreen","darkblue","brown"};
	
	public BlockFloorTile(int par1) {
		super(Material.rock);
		setBlockUnbreakable().setResistance(3).setStepSound(Block.soundTypeStone);
		
		setBlockBounds(0,.8f,0,1,1,1);
        setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
	public int getRenderColor(int meta)
    {
		switch (meta) {
		case 0: return 0x808080; //Grey
		case 1: return 0xFFFFFF; //White
		case 2: return 0x202020; //Black
		case 3: return 0xFF8080; //Red
		case 4: return 0xA7FF87; //Green
		case 5: return 0x87B5FF; //Blue
		case 6: return 0xFFE387; //Yellow
		case 7: return 0xCB87FF; //Purple
		case 8: return 0x802020; //Dark Red
		case 9: return 0x208020; //Dark Green
		case 10: return 0x202080; //Dark Blue
		case 11: return 0xA17A3B; //Brown
		}
		return 0xffffff;
    }
	
	@Override
	public int colorMultiplier(IBlockAccess access, int x, int y, int z)
    {
		return getRenderColor(access.getBlockMetadata(x, y, z));
    }
	
	@Override
	public boolean isOpaqueCube()
    {
        return false;
    }
	
	@Override
	public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	@Override
	public void getSubBlocks(Item id, CreativeTabs creativeTab, List list) //TODO: Maybe this should be Block ID? Doubtful. ~Pdan
    {
		for (int i=0;i<subTypes.length;i++) {
			list.add(new ItemStack(id,1,i));
		}
    }
	
	@Override
	public int damageDropped(int m)
    {
        return m;
    }

	@Override
	public void onUseCrowbar(World world, int x, int y, int z, int dir) {
		int meta = world.getBlockMetadata(x,y,z);
		dropBlockAsItem(world, x, y, z, meta, 0);
		world.setBlockToAir(x, y, z);
	}
}
