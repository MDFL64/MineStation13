package net.c0gg.ms13;

import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.Transmitter;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class TileEntityAirlock extends TileEntity implements Hackable {
	
	private static final float animSpeed =.1f;
	
	private int typeA;
	private int typeB;
	
	//only used clientside
	private float openFraction;
	
	private int counterUpdate;
	private int counterGreen;
	private int counterYellow;
	private int counterRed;
	private int counterBlue;
	
	private int flags;
	private String key;
	private byte hackwires;
	
	private void setFlag(AirlockFlag flag,boolean value) {
		if (worldObj.isRemote) return;
		if (value) {
			flags |= (1<<flag.ordinal());
		} else {
			flags &= ~(1<<flag.ordinal());
		}
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModMinestation.blockAirlockFrame.blockID,2,flags);
	}
	
	public boolean getFlag(AirlockFlag flag) {
		return (flags & (1<<flag.ordinal())) !=0;
	}
	
	public void toggleFlag(AirlockFlag flag) {
		if (worldObj.isRemote) return;
		flags ^= (1<<flag.ordinal());
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModMinestation.blockAirlockFrame.blockID,2,flags);
	}
	
	public TileEntityAirlock() {
		key="";
		//TODO should the powered flag default on?
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("typeA",typeA);
		nbt.setInteger("typeB",typeB);
		nbt.setInteger("flags",flags);
		nbt.setString("key", key);
		nbt.setByte("hackwires", hackwires);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		typeA=nbt.getInteger("typeA");
		typeB=nbt.getInteger("typeB");
		flags=nbt.getInteger("flags");
		key=nbt.getString("key");
		hackwires=nbt.getByte("hackwires");
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote) {
			if (getClosed()) {
				if (openFraction>0) {
					openFraction-=animSpeed;
					if (openFraction<0) {
						openFraction=0;
					}
				}
			} else {
				if (openFraction<1) {
					openFraction+=animSpeed;
					if (openFraction>1) {
						openFraction=1;
					}
				}
			}
			counterUpdate++;
			if (counterGreen>0) counterGreen--;
			if (counterYellow>0) counterYellow--;
			if (counterRed>0) counterRed--;
			if (counterBlue>0) counterBlue--;
		}
	}
	
	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
    {
        return newID!=ModMinestation.blockAirlockFrame.blockID;
    }
	
	public float getOpenFraction() {
		return openFraction;
	}
	
	private boolean getClosed() {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return (meta&8)==8;
	}
	
	@Override
	public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
    }
	
	@Override
	public boolean receiveClientEvent(int id, int param)
    {
		if (id==0) {
        	typeA=param;
        	return true;
        } else if (id==1) {
        	typeB=param;
        	return true;
        } else if (id==2) {
        	flags=param;
        	return true;
        } else if (id==3) {
        	hackwires=(byte)param;
        	return true;
        } else if (id==4) {
        	if (param==0) {
        		counterGreen=10;
        	} else if (param==1) {
        		counterYellow=10;
        	} else if (param==2) {
        		counterRed=10;
        	} else if (param==3) {
        		counterBlue=10;
        	}
        	return true;
        }
		
		return false;
    }
	
	@Override
	public void onDataPacket(INetworkManager netManager,Packet132TileEntityData  packet) {
		readFromNBT(packet.data);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if (!getDirection()) {
			return AxisAlignedBB.getAABBPool().getAABB(xCoord,yCoord,zCoord,xCoord+2,yCoord+2,zCoord+1);
		} else {
			return AxisAlignedBB.getAABBPool().getAABB(xCoord,yCoord,zCoord,xCoord+1,yCoord+2,zCoord+2);
		}
	}
	
	public boolean getDirection() {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		meta&=7;
		return meta>3;
	}

	public int getIndicatorState() {
		if (getFlag(AirlockFlag.UNPOWERED)) return 0;
		
		if (getFlag(AirlockFlag.BOLTS)) {
			return 3;
		}
		
		if (!getFlag(AirlockFlag.ACTIVATED)) {
			return counterUpdate%30>15?2:3;
		}
		
		if (counterGreen>0) {
			return 1;
		}
		if (counterYellow>0) {
			return 2;
		}
		if (counterRed>0) {
			return 3;
		}
		
		return 0;
	}
	
	private void sendIndicator(int n) {
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModMinestation.blockAirlockFrame.blockID,4,n);
	}
	
	@Override
	public void getIndicators(byte[] indicators) {
		indicators[0]=(byte)getIndicatorState();
		
		if (counterBlue>0) {
			indicators[1]=4;
		}
	}
	
	public AirlockType getDoorType(boolean doorB) {
		if (!doorB) {
			if (typeA==-1) return null;
			return AirlockType.values()[typeA];
		} else {
			if (typeB==-1) return null;
			return AirlockType.values()[typeB];
		}
	}
	
	public boolean addDoor(boolean doorB,AirlockType type) {
		//if (worldObj.isRemote) return true;
		
		if (!doorB) {
			if (typeA==0) {
				if (worldObj.isRemote) return true;
				typeA=type.ordinal();
				worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModMinestation.blockAirlockFrame.blockID,0,typeA);
				return true;
			}
		} else {
			if (typeB==0) {
				if (worldObj.isRemote) return true;
				typeB=type.ordinal();
				worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModMinestation.blockAirlockFrame.blockID,1,typeB);
				return true;
			}
		}
		
		return false;
	}
	
	public void toggleOpenAs(EntityPlayer ply) {
		if (worldObj.isRemote) return;
		if (getFlag(AirlockFlag.UNPOWERED)||HackGroup.AIRLOCK.isFunctionDisabled((byte)2,hackwires))
			return;
		
		if (key.equals("")) {
			toggleOpen();
			sendIndicator(0);
		} else if (!HackGroup.AIRLOCK.isFunctionDisabled((byte)6,hackwires)&&((MsInvInventory)ply.inventory).hasAccessKey(key)) {
				toggleOpen();
				sendIndicator(0);
		} else {
			ply.addChatMessage("This airlock requires the access key '"+key+"'.");
			sendIndicator(2);
		}
	}
	
	public void toggleOpen() {
		if (worldObj.isRemote) return;
		if (getFlag(AirlockFlag.WELDED)||getFlag(AirlockFlag.BOLTS)||!getFlag(AirlockFlag.ACTIVATED))
			return;
		ArrayList<ChunkPosition> positions = getAllBlocks();
		boolean open=getClosed();
		for (ChunkPosition pos:positions) {
			int meta = worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
			if (open) {
				meta &=7;
			} else {
				meta |=8;
			}
			worldObj.setBlockMetadataWithNotify(pos.x, pos.y, pos.z,meta,2); //setBlock(pos.x, pos.y, pos.z,ModSpaceStation.blockAirlockFrame.blockID,meta,2);
		}
	}

	private ArrayList<ChunkPosition> getAllBlocks() {
		ArrayList<ChunkPosition> list = new ArrayList<ChunkPosition>();
		list.add(new ChunkPosition(xCoord, yCoord, zCoord));
		list.add(new ChunkPosition(xCoord, yCoord+1, zCoord));
		if (!getDirection()) {
			list.add(new ChunkPosition(xCoord+1, yCoord, zCoord));
			list.add(new ChunkPosition(xCoord+1, yCoord+1, zCoord));
		} else {
			list.add(new ChunkPosition(xCoord, yCoord, zCoord+1));
			list.add(new ChunkPosition(xCoord, yCoord+1, zCoord+1));
		}
		return list;
	}

	public void interact(EntityPlayer ply) {
		if (typeA==0||typeB==0) {
			return;
		}
		
		if (!getFlag(AirlockFlag.ACTIVATED)) {
			if (worldObj.isRemote) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiAirlockSetup(this));
			}
			return;
		}
		
		toggleOpenAs(ply);
	}

	public void screwdiver() {
		if (worldObj.isRemote) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiHacking(this));
		}
	}

	public void weld() {
		toggleFlag(AirlockFlag.WELDED);
	}
	
	public void crowbar() {
		if (getFlag(AirlockFlag.UNPOWERED)) {
			toggleOpen();
		}
	}
	
	public void setKey(String key) {
		if (worldObj.isRemote) return;
		this.key = key;
	}
	
	public void activate() {
		setFlag(AirlockFlag.ACTIVATED,true);
	}
	
	public void updatePowerState() {
		boolean isOff=false;
		if (HackGroup.AIRLOCK.isFunctionDisabled((byte)0,hackwires)&&HackGroup.AIRLOCK.isFunctionDisabled((byte)1,hackwires)) {
			isOff=true;
		}
		if (isOff!=getFlag(AirlockFlag.UNPOWERED)) {
			setFlag(AirlockFlag.UNPOWERED,isOff);
		}
	}
	
	public void toggleBolts() {
		worldObj.playSoundEffect(xCoord+.5f, yCoord+.5f, zCoord+.5f,getFlag(AirlockFlag.BOLTS)?"random.door_open":"random.door_close", 1, .5f);
		toggleFlag(AirlockFlag.BOLTS);
	}
	
	public void toggleTiming() {
		worldObj.playSoundEffect(xCoord+.5f, yCoord+.5f, zCoord+.5f,"note.harp", 1, getFlag(AirlockFlag.FAST_TIMER)?.5f:2);
		toggleFlag(AirlockFlag.FAST_TIMER);
	}

	@Override
	public byte getWires() {
		return hackwires;
	}

	@Override
	public void setWires(byte wires) {
		hackwires=wires;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, ModMinestation.blockAirlockFrame.blockID,3,hackwires);
	}

	@Override
	public void functionDisabled(byte f) {
		if (f<2) {
			updatePowerState();
		}
	}

	@Override
	public void functionRestored(byte f) {
		if (f<2) {
			updatePowerState();
		}
	}

	@Override
	public void functionPulsed(byte f) {
		if (getFlag(AirlockFlag.UNPOWERED)||!getFlag(AirlockFlag.ACTIVATED))
			return;
		if (f<2) { //power wires
			sendIndicator(1);
		} else if (f==2) { //open
			if (key.equals("")) {
				toggleOpen();
			}
		} else if (f==3) {//bolts
			toggleBolts();
		} else if (f==4) {//electrify
			System.out.println("BUZZs!"); //TODO real damage
		} else if (f==5) {//timing
			toggleTiming();
		} else if (f==6) {//Id control
			sendIndicator(2);
		} else if (f==7) {//External control
			sendIndicator(3);
		}
	}

	@Override
	public HackGroup getGroup() {
		return HackGroup.AIRLOCK;
	}
}