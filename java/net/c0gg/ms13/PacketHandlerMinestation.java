package net.c0gg.ms13;

import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;


public class PacketHandlerMinestation /*implements IPacketHandler {
	
	enum Client2ServerSubtypes {
		GRAB,AIRLOCKSETUP,HACK;
	}
	
	enum Server2ClientSubtypes {
		ATMOSDBG_START,ATMOSDBG_STOP,ATMOSDBG_SET,ATMOSDBG_SETMANY,ATMOSDBG_CLEAR,ATMOSDBG_CLEARZONE, ATMOSDBG_TRANSFER
	}
	
	private static ArrayList<EntityPlayer> debuggingPlayers= new ArrayList<EntityPlayer>(); //SWAGYOLO ~Pdan
	
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
	
	public static void svSendAtmosDebugToggle(EntityPlayer target) { //Megayolo ~Pdan
		if (debuggingPlayers.remove(target)) {
			ByteBuffer buffer=ByteBuffer.allocate(4);
			
			buffer.putInt(Server2ClientSubtypes.ATMOSDBG_STOP.ordinal());
			
			Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
			PacketDispatcher.sendPacketToPlayer(packet, target);
		} else {
			debuggingPlayers.add(target);
			
			ByteBuffer buffer=ByteBuffer.allocate(4);
			
			buffer.putInt(Server2ClientSubtypes.ATMOSDBG_START.ordinal());
			
			Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
			PacketDispatcher.sendPacketToPlayer(packet, target);
			
			//AtmosSystem.sendDebugSetup(target);
		}
	}
	
	public static void svSendAtmosDebugSetPos(int zonehash,ChunkPosition pos) {
		ByteBuffer buffer=ByteBuffer.allocate(20);
		
		buffer.putInt(Server2ClientSubtypes.ATMOSDBG_SET.ordinal());
		buffer.putInt(zonehash);
		buffer.putInt(pos.chunkPosX);
		buffer.putInt(pos.chunkPosY);
		buffer.putInt(pos.chunkPosZ);
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
		for (EntityPlayer ply:debuggingPlayers) {
			PacketDispatcher.sendPacketToPlayer(packet, ply);
		}
	}
	
	public static void svSendAtmosDebugSetMany(int zonehash,HashSet<ChunkPosition> positions) {
		ByteBuffer buffer=ByteBuffer.allocate(12+positions.size()*12);
		
		buffer.putInt(Server2ClientSubtypes.ATMOSDBG_SETMANY.ordinal());
		buffer.putInt(zonehash);
		buffer.putInt(positions.size());
		
		for (ChunkPosition pos:positions) {
			buffer.putInt(pos.chunkPosX);
			buffer.putInt(pos.chunkPosY);
			buffer.putInt(pos.chunkPosZ);
		}
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
		for (EntityPlayer ply:debuggingPlayers) {
			PacketDispatcher.sendPacketToPlayer(packet, ply);
		}
	}
	
	//Best function name 2014
	public static void svSendAtmosDebugSetManyUnicast(EntityPlayer target, int zonehash,HashSet<ChunkPosition> positions) {
		ByteBuffer buffer=ByteBuffer.allocate(12+positions.size()*12);
		
		buffer.putInt(Server2ClientSubtypes.ATMOSDBG_SETMANY.ordinal());
		buffer.putInt(zonehash);
		buffer.putInt(positions.size());
		
		for (ChunkPosition pos:positions) {
			buffer.putInt(pos.chunkPosX);
			buffer.putInt(pos.chunkPosY);
			buffer.putInt(pos.chunkPosZ);
		}
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
		PacketDispatcher.sendPacketToPlayer(packet, target);
	}
	
	public static void svSendAtmosDebugClearPos(ChunkPosition pos) {
		ByteBuffer buffer=ByteBuffer.allocate(16);
		
		buffer.putInt(Server2ClientSubtypes.ATMOSDBG_CLEAR.ordinal());
		buffer.putInt(pos.chunkPosX);
		buffer.putInt(pos.chunkPosY);
		buffer.putInt(pos.chunkPosZ);
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
		for (EntityPlayer ply:debuggingPlayers) {
			PacketDispatcher.sendPacketToPlayer(packet, ply);
		}
	}
	
	public static void svSendAtmosDebugClearZone(int zonehash) {
		ByteBuffer buffer=ByteBuffer.allocate(8);
		
		buffer.putInt(Server2ClientSubtypes.ATMOSDBG_CLEARZONE.ordinal());
		buffer.putInt(zonehash);
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
		for (EntityPlayer ply:debuggingPlayers) {
			PacketDispatcher.sendPacketToPlayer(packet, ply);
		}
	}

	public static void svSendAtmosDebugTransfer(int zonehash1, int zonehash2) {
		ByteBuffer buffer=ByteBuffer.allocate(12);
		
		buffer.putInt(Server2ClientSubtypes.ATMOSDBG_TRANSFER.ordinal());
		buffer.putInt(zonehash1);
		buffer.putInt(zonehash2);
		
		Packet250CustomPayload packet=new Packet250CustomPayload("ms13", buffer.array());
		for (EntityPlayer ply:debuggingPlayers) {
			PacketDispatcher.sendPacketToPlayer(packet, ply);
		}
	}

	@Override
	public void onPacketData(INetworkManager manager,Packet250CustomPayload packet, EntityPlayer player) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		try { //I don't trust people to send the right shit.
			ByteBuffer buffer = ByteBuffer.wrap(packet.data);
			int subtype = buffer.getInt();
			
			if (side == Side.CLIENT) {
				if (subtype==Server2ClientSubtypes.ATMOSDBG_START.ordinal()) {
					System.out.println("ATMOS DEBUG START");
					AtmosDebugger.start();
				} else if (subtype==Server2ClientSubtypes.ATMOSDBG_STOP.ordinal()) {
					System.out.println("ATMOS DEBUG STOP");
					AtmosDebugger.stop();
				} else if (subtype==Server2ClientSubtypes.ATMOSDBG_SET.ordinal()) {
					int hash=buffer.getInt();
					AtmosDebugger.map.put(new ChunkPosition(buffer.getInt(),buffer.getInt(),buffer.getInt()),hash);
				} else if (subtype==Server2ClientSubtypes.ATMOSDBG_SETMANY.ordinal()) {
					int hash = buffer.getInt();
					int count=buffer.getInt();
					for (int i=0;i<count;i++)
						AtmosDebugger.map.put(new ChunkPosition(buffer.getInt(),buffer.getInt(),buffer.getInt()),hash);
				} else if (subtype==Server2ClientSubtypes.ATMOSDBG_CLEAR.ordinal()) {
					AtmosDebugger.map.remove(new ChunkPosition(buffer.getInt(),buffer.getInt(),buffer.getInt()));
				} else if (subtype==Server2ClientSubtypes.ATMOSDBG_CLEARZONE.ordinal()) {
					int hash = buffer.getInt();
					Iterator<Entry<ChunkPosition,Integer>> iterator = AtmosDebugger.map.entrySet().iterator();
					while (iterator.hasNext()) {
						if (iterator.next().getValue().intValue()==hash)
							iterator.remove();
					}
				} else if (subtype==Server2ClientSubtypes.ATMOSDBG_TRANSFER.ordinal()) {
					int hash1 = buffer.getInt();
					int hash2 = buffer.getInt();
					Iterator<Entry<ChunkPosition,Integer>> iterator = AtmosDebugger.map.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<ChunkPosition,Integer> entry= iterator.next();
						if (entry.getValue().intValue()==hash1)
							entry.setValue(hash2);
					}
				}
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
						
						TileEntity ent = world.getTileEntity(x, y, z);
						
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
							if (cut?entPlayer.inventory.hasItem(ModMinestation.itemWireCutters):entPlayer.inventory.hasItem(ModMinestation.itemSolderGun)) {
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
							if (entPlayer.inventory.hasItem(ModMinestation.itemMultitool)&&cut) {
								byte function = hackable.getGroup().getFunction((byte)(action));
								hackable.functionPulsed(function);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Warning! Error reading packet from "+((EntityPlayer)player).getDisplayName()+": "+e.getMessage());
		}
	}
}*/
{
	private enum Server2ClientSubtypes {
		ATMOSDBG_START,
		ATMOSDBG_STOP,
		ATMOSDBG_SET,
		ATMOSDBG_SETMANY, //unused?
		ATMOSDBG_CLEAR,
		ATMOSDBG_CLEARZONE,
		ATMOSDBG_TRANSFER
	}
	
	private enum Client2ServerSubtypes {
		GRAB,
		AIRLOCKSETUP,
		HACK
	}
	
	private static PacketHandlerMinestation instance;
	private static String CHANNEL_ID = "ms13";
	
	private FMLEventChannel channel;
	
	//List of players using atmos debugging. Only used on server.
	private static ArrayList<EntityPlayerMP> debuggingPlayers= new ArrayList<EntityPlayerMP>();
	
	public PacketHandlerMinestation() {
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL_ID);
		channel.register(this);
		
		instance=this;
	}
	
	public static void svSendAtmosDebugToggle(EntityPlayerMP target) {
		if (debuggingPlayers.remove(target)) {
			PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
			
			buffer.writeInt(Server2ClientSubtypes.ATMOSDBG_STOP.ordinal());
			
			FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
			instance.channel.sendTo(packet,target);
		} else {
			debuggingPlayers.add(target);
			
			PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
			
			buffer.writeInt(Server2ClientSubtypes.ATMOSDBG_START.ordinal());
			
			FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
			instance.channel.sendTo(packet,target);
		}
	}
	
	public static void svSendAtmosDebugSetPos(int zonehash, ChunkPosition pos) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Server2ClientSubtypes.ATMOSDBG_SET.ordinal());
		buffer.writeInt(zonehash);
		buffer.writeInt(pos.chunkPosX);
		buffer.writeInt(pos.chunkPosY);
		buffer.writeInt(pos.chunkPosZ);
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		for (EntityPlayerMP ply:debuggingPlayers) {
			instance.channel.sendTo(packet, ply);
		}
	}

	public static void svSendAtmosDebugClearPos(ChunkPosition pos) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Server2ClientSubtypes.ATMOSDBG_CLEAR.ordinal());
		buffer.writeInt(pos.chunkPosX);
		buffer.writeInt(pos.chunkPosY);
		buffer.writeInt(pos.chunkPosZ);
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		for (EntityPlayerMP ply:debuggingPlayers) {
			instance.channel.sendTo(packet, ply);
		}
	}

	public static void svSendAtmosDebugClearZone(int zonehash) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Server2ClientSubtypes.ATMOSDBG_CLEARZONE.ordinal());
		buffer.writeInt(zonehash);
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		for (EntityPlayerMP ply:debuggingPlayers) {
			instance.channel.sendTo(packet, ply);
		}
	}

	public static void svSendAtmosDebugTransfer(int zonehash1, int zonehash2) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Server2ClientSubtypes.ATMOSDBG_TRANSFER.ordinal());
		buffer.writeInt(zonehash1);
		buffer.writeInt(zonehash2);
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		for (EntityPlayerMP ply:debuggingPlayers) {
			instance.channel.sendTo(packet, ply);
		}
	}
	
	public static void clSendPlyGrab(Entity grabEnt) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Client2ServerSubtypes.GRAB.ordinal());
		
		int id=-1;
		if (grabEnt!=null) {
			id=grabEnt.getEntityId();
		}
		
		buffer.writeInt(id);
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		instance.channel.sendToServer(packet);
	}

	public static void clSendAirlockSetup(TileEntityAirlock airlock, String key) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Client2ServerSubtypes.AIRLOCKSETUP.ordinal());
		
		buffer.writeInt(airlock.xCoord);
		buffer.writeInt(airlock.yCoord);
		buffer.writeInt(airlock.zCoord);
		
		try {
			buffer.writeStringToBuffer(key);
		} catch (IOException exception) {
			//string was too long, bail out
			return;
		}
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		instance.channel.sendToServer(packet);
	}

	public static void clSendHack(Hackable target, int i) {
		PacketBuffer buffer=new PacketBuffer(Unpooled.buffer());
		
		buffer.writeInt(Client2ServerSubtypes.HACK.ordinal());
		
		if (target instanceof TileEntity) {
			TileEntity targetEnt = (TileEntity)target;
			buffer.writeByte((byte)0);
			buffer.writeInt(targetEnt.xCoord);
			buffer.writeInt(targetEnt.yCoord);
			buffer.writeInt(targetEnt.zCoord);
		} else if (target instanceof Entity) {
			Entity targetEnt = (Entity)target;
			buffer.writeByte((byte)1);
			buffer.writeInt(targetEnt.getEntityId());
			buffer.writeInt(0);
			buffer.writeInt(0);
		} else {
			//Invalid hackable
			return;
		}
		
		buffer.writeByte((byte)i);
		
		FMLProxyPacket packet = new FMLProxyPacket(buffer,CHANNEL_ID);
		instance.channel.sendToServer(packet);
	}
	
	@SubscribeEvent
	public void svReceiveHandler(FMLNetworkEvent.ServerCustomPacketEvent event) {
		
	}
	
	@SubscribeEvent
	public void clReceiveHandler(FMLNetworkEvent.ClientCustomPacketEvent event) {
		
	}
}