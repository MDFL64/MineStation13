package net.c0gg.ms13;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;


public class PacketHandlerMinestation implements IPacketHandler {
	
	enum Client2ServerSubtypes {
		GRAB,AIRLOCKSETUP,HACK;
	}
	
	public static void clSendPlyGrab(Entity grabEnt) {
		ByteBuffer buffer=ByteBuffer.allocate(8);
		
		buffer.putInt(Client2ServerSubtypes.GRAB.ordinal());
		
		int id=-1;
		if (grabEnt!=null) {
			id=grabEnt.entityId;
		}
		
		buffer.putInt(id);
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13",buffer.array());
		PacketDispatcher.sendPacketToServer(packet);
	}
	
	public static void clSendAirlockSetup(TileEntityAirlock airlock,String key) {
		ByteBuffer buffer=ByteBuffer.allocate(17+key.length());
		
		buffer.putInt(Client2ServerSubtypes.AIRLOCKSETUP.ordinal());
		
		buffer.putInt(airlock.xCoord);
		buffer.putInt(airlock.yCoord);
		buffer.putInt(airlock.zCoord);
		
		buffer.put((byte)key.length());
		buffer.put(key.getBytes());
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13",buffer.array());
		PacketDispatcher.sendPacketToServer(packet);
		
		System.out.println("~~ "+buffer.array().length);
	}
	
	public static void clSendHack(Hackable target, int i) {
		ByteBuffer buffer=ByteBuffer.allocate(18);
		
		buffer.putInt(Client2ServerSubtypes.HACK.ordinal());
		
		if (target instanceof TileEntity) {
			TileEntity targetEnt = (TileEntity)target;
			buffer.put((byte)0);
			buffer.putInt(targetEnt.xCoord);
			buffer.putInt(targetEnt.yCoord);
			buffer.putInt(targetEnt.zCoord);
		} else if (target instanceof Entity) {
			Entity targetEnt = (Entity)target;
			buffer.put((byte)1);
			buffer.putInt(targetEnt.entityId);
			buffer.putInt(0);
			buffer.putInt(0);
		} else {
			//Invalid hackable
			return;
		}
		
		buffer.put((byte)i);
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13",buffer.array());
		PacketDispatcher.sendPacketToServer(packet);
	}
	
	@Override
	public void onPacketData(INetworkManager manager,Packet250CustomPayload packet, Player player) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		try { //I don't trust people to send the right shit.
			ByteBuffer buffer = ByteBuffer.wrap(packet.data);
			int subtype = buffer.getInt();
			
			if (side == Side.CLIENT) {
				
			} else if (side == Side.SERVER) {
				EntityPlayerMP entPlayer = ((EntityPlayerMP)player);
				
				if (subtype==Client2ServerSubtypes.GRAB.ordinal()) {	
					int id = buffer.getInt();
					
					//If we already have a grab, just release it
					if (TickerPhysExt.grabs.containsKey(entPlayer)) {
						TickerPhysExt.grabs.remove(entPlayer);
					} else if (id!=-1) {
						Entity entGrabbed = entPlayer.worldObj.getEntityByID(id);
						
						//Grabbing in a loop is bad. This only checks the immediate target... TODO in future we may need to check for a larger loop.
						if (TickerPhysExt.grabs.get(entGrabbed)!=entPlayer) {
							TickerPhysExt.grabs.put(entPlayer,entGrabbed);
						}
					}
				} else if (subtype==Client2ServerSubtypes.AIRLOCKSETUP.ordinal()) {
					int x = buffer.getInt();
					int y = buffer.getInt();
					int z = buffer.getInt();
					
					World world = ((EntityPlayerMP)player).worldObj;
					
					TileEntity ent = world.getBlockTileEntity(x, y, z);
					
					if (ent!=null&&ent instanceof TileEntityAirlock&&PlayerUtil.isInRange(entPlayer,new ChunkPosition(ent.xCoord,ent.yCoord,ent.zCoord))) {
						TileEntityAirlock entAirlock = (TileEntityAirlock)ent;
						if (!entAirlock.getFlag(AirlockFlag.ACTIVATED)) {
							byte[] keyBytes=new byte[buffer.get()];
	
							buffer.get(keyBytes);
							entAirlock.setKey(new String(keyBytes));
							entAirlock.activate();
						}
					}
				} else if (subtype==Client2ServerSubtypes.HACK.ordinal()) {
					byte hackabletype = buffer.get();
					
					Hackable hackable=null;
					
					World world = entPlayer.worldObj;
					
					if (hackabletype==0) {
						int x = buffer.getInt();
						int y = buffer.getInt();
						int z = buffer.getInt();
						
						TileEntity ent = world.getBlockTileEntity(x, y, z);
						
						if (ent!=null&&ent instanceof Hackable&&PlayerUtil.isInRange(entPlayer,new ChunkPosition(ent.xCoord,ent.yCoord,ent.zCoord))) {
							hackable = (Hackable)ent;
						}
					} else {
						throw new Exception("Unsupported hackable subtype: "+hackabletype);
					}
					
					byte action = buffer.get();
					
					if (hackable!=null) {
						byte wires = hackable.getWires();
						
						if (action<8) {
							boolean cut = (wires & (1<<action)) ==0;
							if (cut?entPlayer.inventory.hasItem(ModMinestation.itemWireCutters.itemID):entPlayer.inventory.hasItem(ModMinestation.itemSolderGun.itemID)) {
								hackable.setWires((byte)(wires^(1<<action)));
								byte function = hackable.getGroup().getFunction(action);
								if (cut) {
									hackable.functionDisabled(function);
								} else {
									hackable.functionRestored(function);
								}
							}
						} else if (action<16) {
							action-=8;
							boolean cut = (wires & (1<<action)) ==0;
							if (entPlayer.inventory.hasItem(ModMinestation.itemMultitool.itemID)&&cut) {
								byte function = hackable.getGroup().getFunction((byte)(action));
								hackable.functionPulsed(function);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Warning! Error reading packet from "+((EntityPlayer)player).username+": "+e.getMessage());
		}
	}
}
