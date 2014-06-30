package net.c0gg.ms13;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon; //YOLO ~Pdan
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Inner frame for an airlock.
 * 
 * @author Parakeet
 *
 */
public class BlockAirlockFrame extends BlockStation implements ToolableScrewdriver, ToolableWelder, ToolableCrowbar {
	public BlockAirlockFrame(int par1) {
		super(par1);
		setResistance(30).setCreativeTab(null);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List boxes, Entity entity)
    {
        int meta = world.getBlockMetadata(x, y, z);
        boolean hpos=(meta&1)==1;
        boolean vpos=(meta&2)==2;
        boolean dir=(meta&4)==4;
        boolean closed=(meta&8)==8;
        
        if (!vpos) {//bottom
        	setBlockBounds(0,-.1f,0,1,0,1);
        } else {//top
        	setBlockBounds(0,1,0,1,1.1f,1);
        }
        super.addCollisionBoxesToList(world,x,y,z,mask,boxes,entity);
        
        if (!dir) {
        	if (!hpos) {
        		setBlockBounds(0,0,0,.1f,1,1);
        	} else {
        		setBlockBounds(.9f,0,0,1,1,1);
        	}
        } else {
        	if (!hpos) {
        		setBlockBounds(0,0,0,1,1,.1f);
        	} else {
        		setBlockBounds(0,0,.9f,1,1,1);
        	}
        }
        super.addCollisionBoxesToList(world,x,y,z,mask,boxes,entity);
        
        if (closed) {
	        if (!dir) {
	        	setBlockBounds(0,0,.375f,1,1,.625f);
	        } else {
	        	setBlockBounds(.375f,0,0,.625f,1,1);
	        }
	        super.addCollisionBoxesToList(world,x,y,z,mask,boxes,entity);
    	}
    }
	
	/**
	 * @todo never seen meta before.
	 */
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
    {
		int meta = blockAccess.getBlockMetadata(x, y, z);
        boolean vpos=(meta&2)==2;
        boolean dir=(meta&4)==4;
        boolean closed=(meta&8)==8;
        
        if (closed) {
	        if (!dir) {
	        	setBlockBounds(0,0,.375f,1,1,.625f);
	        } else {
	        	setBlockBounds(.375f,0,0,.625f,1,1);
	        }
	        return;
    	}
        
        if (!dir) {
        	if (!vpos) {
        		setBlockBounds(0,0,.375f,1,.2f,.625f);
        	} else {
        		setBlockBounds(0,.8f,.375f,1,1,.625f);
        	}
        } else {
        	if (!vpos) {
        		setBlockBounds(.375f,0,0,.625f,.2f,1);
        	} else {
        		setBlockBounds(.375f,.8f,0,.625f,1,1);
        	}
        }
    }
	
	/**
	 * When the block is activated, something needs to happen.
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int dir, float par7, float par8, float par9)
    {
		ItemStack curItemStack = player.inventory.getCurrentItem();
		if (curItemStack!=null&&(curItemStack.getItem()==ModMinestation.itemScrewdriver||curItemStack.getItem()==ModMinestation.itemWelder||curItemStack.getItem()==ModMinestation.itemCrowbar||curItemStack.getItem()==ModMinestation.itemAirlockDoor)) {
			return false;
		} else {
			TileEntityAirlock ent = getTileEnt(world, x, y, z);
			if (ent!=null) ent.interact(player);
		}
        return true;
    }
	
	@Override
	public int getRenderType()
    {
        return ModMinestation.renderBlockAirlockFrameId;
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
	public IIcon getIcon(int side, int meta) {
		return ModMinestation.blockStationBlock.getIcon(side,0);
	}
	
	/**
	 * Get the base tile of the airlock frame.
	 * 
	 * @param w The World
	 * @param x x pos
	 * @param y y pos
	 * @param z z pos
	 * @return null
	 */
	private TileEntityAirlock getTileEnt(World w,int x,int y,int z) {
		if (w.getBlock(x, y, z)!=this) return null;
		int meta = w.getBlockMetadata(x, y, z);
		meta&=7;
		ChunkPosition pos=null;
		switch (meta) {
		case 0:
			return (TileEntityAirlock)w.getTileEntity(x,y,z);
		case 1:
			return (TileEntityAirlock)w.getTileEntity(x-1,y,z);//TODO wrong?
		case 2:
			return (TileEntityAirlock)w.getTileEntity(x,y-1,z);//Eclipse suggests getTileEntity instead of getBlockTileEntity ~Pdan
		case 3:
			return (TileEntityAirlock)w.getTileEntity(x-1,y-1,z);
		case 4:
			return (TileEntityAirlock)w.getTileEntity(x,y,z);
		case 5:
			return (TileEntityAirlock)w.getTileEntity(x,y,z-1);
		case 6:
			return (TileEntityAirlock)w.getTileEntity(x,y-1,z);
		case 7:
			return (TileEntityAirlock)w.getTileEntity(x,y-1,z-1);
		}
		
		return null;
	}
	
	/**
	 * Creates a door at the base of the airlock
	 * 
	 * @param w The World
	 * @param x x pos
	 * @param y y pos
	 * @param z z pos
	 * @param doorB 
	 * @param type
	 * @return
	 */
	public boolean addDoor(World w,int x,int y,int z,     boolean doorB,AirlockType type) {
		TileEntityAirlock ent = getTileEnt(w, x, y, z);
		if (ent==null) return false;
		return ent.addDoor(doorB, type);
	}
	
	@Override
	public boolean hasTileEntity(int metadata)
    {
		metadata&=7;
		if (metadata==0||metadata==4) {
			return true;
		}
		return false;
    }
	
	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		
		TileEntityAirlock tileEnt = new TileEntityAirlock();
		return tileEnt;
	}
	
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {}
	
	@Override
	public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }
	
	/**
	 * Break all associated blocks when we break one.
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int meta) {
		super.breakBlock(world, x, y, z, par5, meta);
		
		boolean hpos=(meta&1)==1;
        boolean vpos=(meta&2)==2;
        boolean dir=(meta&4)==4;
        
        int y2;
        if (!vpos) {//bottom
        	y2=y+1;
        } else {//top
        	y2=y-1;
        }
        
    	if (world.getBlock(x,y2,z)==this) {
    		world.setBlockToAir(x,y2,z);
    	}
        
        if (!dir) {
        	if (!hpos) {
        		if (world.getBlock(x+1,y,z)==this)
        			world.setBlockToAir(x+1,y,z);
        		if (world.getBlock(x+1,y2,z)==this)
        			world.setBlockToAir(x+1,y2,z);
        	} else {
        		if (world.getBlock(x-1,y,z)==this)
        			world.setBlockToAir(x-1,y,z);
        		if (world.getBlock(x-1,y2,z)==this)
        			world.setBlockToAir(x-1,y2,z);
        	}
        } else {
        	if (!hpos) {
        		if (world.getBlock(x,y,z+1)==this)
        			world.setBlockToAir(x,y,z+1);
        		if (world.getBlock(x,y2,z+1)==this)
        			world.setBlockToAir(x,y2,z+1);
        	} else {
        		if (world.getBlock(x,y,z-1)==this)
        			world.setBlockToAir(x,y,z-1);
        		if (world.getBlock(x,y2,z-1)==this)
        			world.setBlockToAir(x,y2,z-1);
        	}
        }
	}

	@Override
	public void onUseScrewdriver(World world, int x, int y, int z, int dir) {
		getTileEnt(world, x, y, z).screwdiver();
	}

	@Override
	public void onUseWelder(World world, int x, int y, int z, int dir) {
		getTileEnt(world, x, y, z).weld();
	}

	@Override
	public void onUseCrowbar(World world, int x, int y, int z, int dir) {
		getTileEnt(world, x, y, z).crowbar();
	}
}