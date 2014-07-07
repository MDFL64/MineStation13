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
import net.minecraft.network.NetHandlerPlayServer;
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

//TODO see if should use SimpleNetworkWrapper

public class PacketHandlerMinestation {
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
		FMLProxyPacket packet = event.packet;
		EntityPlayerMP entPlayer = ((NetHandlerPlayServer)packet.handler()).playerEntity;
		PacketBuffer buffer = new PacketBuffer(packet.payload());
		
		//Select the correct packet subtype
		switch (Client2ServerSubtypes.values()[buffer.readInt()]) {
			case GRAB:
				int id = buffer.readInt();
				
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
				break;
			case AIRLOCKSETUP:
				int x = buffer.readInt();
				int y = buffer.readInt();
				int z = buffer.readInt();
				
				World world = entPlayer.worldObj;
				
				TileEntity ent = world.getTileEntity(x, y, z);
				
				if (ent!=null&&ent instanceof TileEntityAirlock&&PlayerUtil.isInRange(entPlayer,new ChunkPosition(ent.xCoord,ent.yCoord,ent.zCoord))) {
					TileEntityAirlock entAirlock = (TileEntityAirlock)ent;
					if (!entAirlock.getFlag(AirlockFlag.ACTIVATED)) {
						try {
							entAirlock.setKey(buffer.readStringFromBuffer(100));
						} catch (IOException e) {
							//string was too long, bail out
							return;
						}
						entAirlock.activate();
					}
				}
				break;
			case HACK:
				byte hackabletype = buffer.readByte();
				
				Hackable hackable=null;
				
				world = entPlayer.worldObj;
				
				if (hackabletype==0) {
					x = buffer.readInt();
					y = buffer.readInt();
					z = buffer.readInt();
					
					ent = world.getTileEntity(x, y, z);
					
					if (ent!=null&&ent instanceof Hackable&&PlayerUtil.isInRange(entPlayer,new ChunkPosition(ent.xCoord,ent.yCoord,ent.zCoord))) {
						hackable = (Hackable)ent;
					}
				} else {
					//throw new Exception("Unsupported hackable subtype: "+hackabletype);
					return;
				}
				
				byte action = buffer.readByte();
				
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
				break;
		}
	}
	
	@SubscribeEvent
	public void clReceiveHandler(FMLNetworkEvent.ClientCustomPacketEvent event) {
		FMLProxyPacket packet = event.packet;
		PacketBuffer buffer = new PacketBuffer(packet.payload());
		
		//Select the correct packet subtype
		switch (Server2ClientSubtypes.values()[buffer.readInt()]) {
			case ATMOSDBG_START:
				System.out.println("ATMOS DEBUG START");
				AtmosDebugger.start();
				break;
			case ATMOSDBG_STOP:
				System.out.println("ATMOS DEBUG STOP");
				AtmosDebugger.stop();
				break;
			case ATMOSDBG_SET:
				int hash=buffer.readInt();
				AtmosDebugger.map.put(new ChunkPosition(buffer.readInt(),buffer.readInt(),buffer.readInt()),hash);
				break;
			case ATMOSDBG_SETMANY:
				hash = buffer.readInt();
				int count=buffer.readInt();
				for (int i=0;i<count;i++)
					AtmosDebugger.map.put(new ChunkPosition(buffer.readInt(),buffer.readInt(),buffer.readInt()),hash);
				break;
			case ATMOSDBG_CLEAR:
				AtmosDebugger.map.remove(new ChunkPosition(buffer.readInt(),buffer.readInt(),buffer.readInt()));
				break;
			case ATMOSDBG_CLEARZONE:
				hash = buffer.readInt();
				Iterator<Entry<ChunkPosition,Integer>> iterator = AtmosDebugger.map.entrySet().iterator();
				while (iterator.hasNext()) {
					if (iterator.next().getValue().intValue()==hash)
						iterator.remove();
				}
				break;
			case ATMOSDBG_TRANSFER:
				int hash1 = buffer.readInt();
				int hash2 = buffer.readInt();
				iterator = AtmosDebugger.map.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<ChunkPosition,Integer> entry= iterator.next();
					if (entry.getValue().intValue()==hash1)
						entry.setValue(hash2);
				}
				break;
		}
	}
}