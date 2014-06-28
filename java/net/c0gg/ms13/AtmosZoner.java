/* 
 * This code is stupidly complicated.
 * 
 * Is it efficient? No clue.
 * Is it good design? Probably not.
 * Will it work on a large scale? I sure as hell hope so.
 * 
 */

package net.c0gg.ms13;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

import net.c0gg.ms13.AtmosZoner.ZonerAccessor;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.ZombieEvent;

public class AtmosZoner {
	static private Hashtable<World,AtmosZoner> zoners=new Hashtable<World,AtmosZoner>();
	
	public static AtmosZoner getForWorld(World w) {
		return zoners.get(w);
	}
	
	public static void updateAll() {
		for (AtmosZoner zoner:zoners.values()) {
			zoner.update();
		}
	}
	
	private void update() {
		zones.addAll(newZones);
		newZones.clear();
		
		Iterator<Zone> iter = zones.iterator();
		
		//Clean the zone list and clear forces
		while (iter.hasNext()) {
			Zone zone = iter.next();
			if (zone!=zone.getTheZone())
				iter.remove();
			else
				zone.forces.clear();
		}
		
		for (Zone zone:zones) {
			if (zone instanceof HotZone)
				((HotZone)zone).update(1);
		}
		
		//Do them physics
		for (Object obj:world.getLoadedEntityList()) {
			Entity e = (Entity)obj;
			Vec3 entPos=Vec3.fakePool.getVecFromPool(e.posX,e.posY,e.posZ);
			Zone z = getZoneAt(new ChunkPosition(entPos));
			if (z!=null) {
				for (ForcePoint forcepoint:z.forces) {
					Vec3 dir = forcepoint.pos.subtract(entPos).normalize();
					System.out.println(dir.toString()+" "+forcepoint.force);
					e.setVelocity(dir.xCoord*forcepoint.force, dir.yCoord*forcepoint.force, dir.zCoord*forcepoint.force);
				}
			}
		}
	}
	
	private World world;
	private Hashtable<ChunkPosition, Zone> zoneMap;
	private ArrayList<Zone> zones=new ArrayList<Zone>();
	private ArrayList<Zone> newZones=new ArrayList<Zone>();
	
	public AtmosZoner(World world) {
		zoners.put(world, this);
		this.world=world;
		zoneMap=new Hashtable<ChunkPosition,Zone>();
	}
	
	protected Zone getZoneAt(ChunkPosition pos) {
		Zone zone = zoneMap.get(pos);
		if (zone!=null) {
			Zone zone2 = zone.getTheZone();
				
			if (zone!=zone2) {
				if (zone2!=null)
					zoneMap.put(pos, zone2);
				else
					zoneMap.remove(pos);
				zone=zone2;
			}
		}
		return zone;
	}
	
	public void blockRemove(ChunkPosition pos) {
		for (ChunkPosition adjPos:getAdjacentPositions(pos)) {
			Zone zone = getZoneAt(adjPos);
			if (zone!=null) {
				new HotZone(new ZonerAccessor(),pos);
				break;
			}
		}
	}
	
	public void blockAdd(ChunkPosition pos) {
		Zone zone = getZoneAt(pos);
		if (zone!=null) {
			zone.trySplit(pos);
		}
	}
	
	public void testPut(ChunkPosition pos) {
		Zone zone = getZoneAt(pos);
		if (zone==null)
			zone = new HotZone(new ZonerAccessor(),pos);
		zone.air.addAmount(GasType.O2, 900);
		zone.air.addHeat(5000);
	}
	
	public String testGet(ChunkPosition pos) {
		Zone zone = getZoneAt(pos);
		if (zone==null)
			return "Nothing here!";
		
		float pressure=zone.air.getPressure();
		float temp=zone.air.getTemperatureC();
		
		String result="Zone: "+pressure+" KPA / "+temp+" \u00B0C";
		
		float[] makeup = zone.air.getMakeup();
		
		for (int i=0;i<makeup.length;i++)
			if (makeup[i]!=0)
				result+=("\n"+(makeup[i]*100)+"% "+GasType.values()[i].name());
			
		return result;
	}
	
	public static ChunkPosition[] getAdjacentPositions(ChunkPosition pos) {
		return new ChunkPosition[] {
			new ChunkPosition(pos.chunkPosX+1,pos.chunkPosY,pos.chunkPosZ),
			new ChunkPosition(pos.chunkPosX-1,pos.chunkPosY,pos.chunkPosZ),
			new ChunkPosition(pos.chunkPosX,pos.chunkPosY,pos.chunkPosZ+1),
			new ChunkPosition(pos.chunkPosX,pos.chunkPosY,pos.chunkPosZ-1),
			new ChunkPosition(pos.chunkPosX,pos.chunkPosY+1,pos.chunkPosZ),
			new ChunkPosition(pos.chunkPosX,pos.chunkPosY-1,pos.chunkPosZ)
		};
	}
	
	public class ZonerAccessor {
		private ZonerAccessor() {}
		
		public void addZone(Zone zone) {
			newZones.add(zone);
		}
		public World getWorld() {
			return world;
		}
		public Hashtable<ChunkPosition, Zone> getZoneMap() {
			return zoneMap;
		}
	}
}

class Zone implements GasContainer {
	protected AtmosZoner.ZonerAccessor zonerAccess;
	protected int volume;
	public AtmosMix air;
	public ArrayList<ForcePoint> forces=new ArrayList<ForcePoint>();
	
	/* This is a goofy system and I'm not really sure if it's good practice.
	 * Each zone holds a self reference. Whenever a zone is destroyed, this reference is set to null.
	 * If a zone is consumed, the reference is set to the consumer and the volume of the consumed zone is transferred.
	 * This way, the zone getter functions can do the work of cleaning up the zone map over time.
	 */

	private Zone theZone;
	
	public Zone(AtmosZoner.ZonerAccessor zonerAccess) {
		theZone=this;
		this.zonerAccess=zonerAccess;
		zonerAccess.addZone(this);
	}
	
	//This is used for convoluted system detailed above.
	public Zone getTheZone() {
		if (theZone==null||theZone==this)
			return theZone;
		return theZone.getTheZone();
	}
	
	public boolean addPos(ChunkPosition pos) {
		Zone oldzone = getZoneAt(pos);
		if (oldzone!=null) {
			if (oldzone==this)
				return false;
			oldzone.volume--;
		}
		
		zonerAccess.getZoneMap().put(pos, this);
		volume++;
		PacketHandlerMinestation.svSendAtmosDebugSetPos(this.hashCode(), pos);
		return true;
	}
	
	public void removePos(ChunkPosition pos) {
		Zone oldzone = getZoneAt(pos);
		if (oldzone==this) {
			zonerAccess.getZoneMap().remove(pos);
			volume--;
			PacketHandlerMinestation.svSendAtmosDebugClearPos(pos);
		}
	}
	
	@Override
	public float getVolume() {
		return volume;
	}
	
	public void trySplit(ChunkPosition pos) { //TODO it would probably be more efficient to just floodfill from each base position.
		removePos(pos);
		
		//Get adjacent positions that belong to our zone.
		ChunkPosition[] positionsToCheckForStart=AtmosZoner.getAdjacentPositions(pos);
		
		Stack<ChunkPosition> basePositions= new Stack<ChunkPosition>();
		
		for (ChunkPosition ip:positionsToCheckForStart) {
			if (getZoneAt(ip)==this) {
				basePositions.push(ip);
			}
		}
		
		ArrayList<HashSet<ChunkPosition>> newZones = new ArrayList<HashSet<ChunkPosition>>();
		
		//If we have only one starting position left, we can leave it because it can't join with anything else.
		while (basePositions.size()>1) {
			/* 
			 * openSet: Positions we need to look at.
			 * closedSet: Positions we're done looking at.
			 */
			HashSet<ChunkPosition> openSet = new HashSet<ChunkPosition>();
			HashSet<ChunkPosition> closedSet = new HashSet<ChunkPosition>();
			
			ChunkPosition start = basePositions.pop();
			ChunkPosition goal = basePositions.pop();
			
			openSet.add(start);
			
			for (;;) {
				//This would indicate that we filled an entire closed off section of the zone.
				if (openSet.isEmpty()) {
					//Push the goal back since we didn't reach it this time.
					basePositions.push(goal);
					//Make a new zone.
					newZones.add(closedSet);
					break;
				}
				
				//Search the open set for the closest to both a) the start and b) the goal.
				ChunkPosition pWinner=null;
				int sCost=-1;
				
				for (ChunkPosition ip:openSet) {
					//The position is the winner if it's a base position
					if (basePositions.contains(ip)) {
						pWinner=ip;
						
						//Remove it from bases
						basePositions.remove(pWinner);
						
						//We need a new goal if that was one
						if (basePositions.isEmpty()) {
							goal=null;
						} else {
							if (ip.equals(goal))
								goal=basePositions.pop();
						}
						
						break;
					}
					
					
					//Compute cost using crud math
					int sdx = start.chunkPosX-ip.chunkPosX;int sdy = start.chunkPosY-ip.chunkPosY;int sdz = start.chunkPosZ-ip.chunkPosZ;
					int gdx = goal.chunkPosX-ip.chunkPosX;int gdy = goal.chunkPosY-ip.chunkPosY;int gdz = goal.chunkPosZ-ip.chunkPosZ;
					int cost = (sdx*sdx+sdy*sdy+sdz*sdz)+(gdx*gdx+gdy*gdy+gdz*gdz);
					
					if (sCost==-1) {
						pWinner=ip;
						sCost = cost;
					} else {
						if (cost<sCost) {
							pWinner=ip;
							sCost = cost;
						}
					}
				}
				
				//Break if we have ABSOLUTELY no more goals
				if (goal==null) {
					break;
				}
				
				//Move to the closed set so we don't have to look at this position again.
				openSet.remove(pWinner);
				closedSet.add(pWinner);
				
				//See if we can add surrounding positions to the open set.
				ChunkPosition[] positionsToCheckForOpen=AtmosZoner.getAdjacentPositions(pWinner);
				
				for (ChunkPosition ip:positionsToCheckForOpen) {
					if (getZoneAt(ip)==this&&!closedSet.contains(ip)) {
						openSet.add(ip);
					}
				}
			}
		}
		
		doSplit(newZones);
	}

	//Internal type-specific method for doing the split based on a list of sets to turn into new zones
	protected void doSplit(ArrayList<HashSet<ChunkPosition>> newZones) {
		for (HashSet<ChunkPosition> posSet:newZones) {
			Zone newZone = new Zone(zonerAccess);
			for (ChunkPosition pos:posSet)
				newZone.addPos(pos);
			newZone.air= air.take(newZone);
			System.out.println("Split made zone with volume of "+posSet.size());
		}
	}

	public void kill() {
		theZone=null;
		PacketHandlerMinestation.svSendAtmosDebugClearZone(this.hashCode());
	}
	
	public void transferTo(Zone zone) {
		theZone=zone;
		zone.volume+=volume;
		air.transferTo(zone.air);
		PacketHandlerMinestation.svSendAtmosDebugTransfer(this.hashCode(),zone.hashCode());
	}
	
	protected Zone getZoneAt(ChunkPosition pos) {
		Zone zone = zonerAccess.getZoneMap().get(pos);
		if (zone!=null) {
			Zone zone2 = zone.getTheZone();
				
			if (zone!=zone2) {
				if (zone2!=null)
					zonerAccess.getZoneMap().put(pos, zone2);
				else
					zonerAccess.getZoneMap().remove(pos);
				zone=zone2;
			}
		}
		return zone;
	}

	//Never, ever call this
	public static final java.util.concurrent.atomic.AtomicIntegerFieldUpdater<java.awt.datatransfer.UnsupportedFlavorException> letKanyeIn(String password,boolean imSure,boolean imPositive) {
		return null;
	}
}

//A 'Hot' zone is a zone that is still expanding. Normal zones will never expand.
//They will decay into normal zones when finished expanding or merge with 'Cold' zones when pressures & temperatures equalize.
class HotZone extends Zone {
	private Stack<ChunkPosition> expansionStack= new Stack<ChunkPosition>(); //Stack should contain only positions we own
	private Hashtable<ChunkPosition,HashSet<Zone>> connectionPoints=new Hashtable<ChunkPosition,HashSet<Zone>>(); //Positions we own->cold zones next to them (Hot zones should automerge)
	private ChunkPosition dissipatePos;
	
	public HotZone(ZonerAccessor zonerAccess,ChunkPosition pos) {
		super(zonerAccess);
		addExpandPos(pos);
		air=new AtmosMix(this);
	}
	
	private HotZone(ZonerAccessor zonerAccess) {
		super(zonerAccess);
	}
	
	public void addExpandPos(ChunkPosition pos) {
		if (addPos(pos))
			expansionStack.push(pos);
	}
	
	public void update(int iterationBudget) {
		//Part 1: Do Expansion
		for (int i=0;i<iterationBudget;i++) {
			if (expansionStack.isEmpty()||dissipatePos!=null)
				break;
			ChunkPosition pos = expansionStack.pop();
			if (getZoneAt(pos)!=this)
				continue;
			HashSet<Zone> connectedZones=new HashSet<Zone>();
			for (ChunkPosition neighbor:AtmosZoner.getAdjacentPositions(pos)) {
				Zone z = getZoneAt(neighbor);
				if (z==this)
					continue;
				if (z!=null) {
					if (z instanceof HotZone) { //Merge with other hot zones right away
						consume(z);
					} else {
						connectedZones.add(z);
					}
					continue;
				}
				
				Block b = zonerAccess.getWorld().getBlock(neighbor.chunkPosX, neighbor.chunkPosY, neighbor.chunkPosZ);
				
				if (blockBlocksAtmos(b))
					continue;
				
				addExpandPos(neighbor);
				
				if (neighbor.chunkPosY>=255||neighbor.chunkPosY<=0)
					dissipatePos=neighbor;
			}
			if (!connectedZones.isEmpty())
				connectionPoints.put(pos,connectedZones);
		}
		
		//Decay if we no longer need to be a hot zone
		if (expansionStack.isEmpty()&&connectionPoints.isEmpty()&&dissipatePos==null) {
			decay();
			return;
		}
		
		//Set amount and temp to zero if we are dissipating
		if (dissipatePos!=null) {
			air.clear();
		}
		
		//Part 2: Iterate through connected zones, removing invalid ones, and determining flow areas for each adjacent zone
		Hashtable<Zone,ForcePoint> flowAreas = new Hashtable<Zone,ForcePoint>();
		Iterator<Entry<ChunkPosition,HashSet<Zone>>> iteratorStarts= connectionPoints.entrySet().iterator();
		while (iteratorStarts.hasNext()) {
			Entry<ChunkPosition,HashSet<Zone>> entryConnections = iteratorStarts.next();
			if (getZoneAt(entryConnections.getKey())!=this) {
				iteratorStarts.remove();
				continue;
			}
			
			Iterator<Zone> iteratorEnds = entryConnections.getValue().iterator();
			
			while (iteratorEnds.hasNext()) {
				Zone z = iteratorEnds.next().getTheZone();
				if (z==null) {
					iteratorEnds.remove();
					//Attempt to expand into the removed zone
					if (expansionStack.peek()!=entryConnections.getKey())
						expansionStack.push(entryConnections.getKey());
					continue;
				}
				
				//Forcepoints are used here to track the contact area of the connected zones and the average connection point
				ForcePoint oldval = flowAreas.get(z);
				ChunkPosition pos = entryConnections.getKey();
				if (oldval!=null) {
					oldval.force++;
					oldval.pos.addVector(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
				} else {
					flowAreas.put(z,new ForcePoint(1,Vec3.fakePool.getVecFromPool(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ)));
				}
			}
		}
		
		//Kill ourselves if we are dissipating and have no connections
		if (dissipatePos!=null && flowAreas.isEmpty()) {
			kill();
			return;
		}
		
		//Fix our average force positions
		for (ForcePoint f:flowAreas.values()) {
			f.pos.xCoord/=f.force;
			f.pos.yCoord/=f.force;
			f.pos.zCoord/=f.force;
		}
		
		//Part 3: Flow calculations
		boolean allEqualized= !flowAreas.isEmpty();
		Zone currentZone=this;
		for (Entry<Zone,ForcePoint> flowEntry: flowAreas.entrySet()) {
			float flowForce=currentZone.air.flow(flowEntry.getKey().air, flowEntry.getValue().force);
			if (flowForce!=flowForce) {
				if (dissipatePos==null) {
					currentZone.transferTo(flowEntry.getKey());
					currentZone=flowEntry.getKey();
				}
			} else {
				currentZone.forces.add(new ForcePoint(flowForce, flowEntry.getValue().pos));
				flowEntry.getKey().forces.add(new ForcePoint(-flowForce, flowEntry.getValue().pos));
				allEqualized=false;
			}
		}
		if (dissipatePos!=null&&allEqualized) {
			for (Zone zone: flowAreas.keySet()) {
				zone.kill();
			}
			kill();
		}
	}
	
	@Override
	public void transferTo(Zone zone) {
		super.transferTo(zone);
		//We need to transfer extra info to hot zones.
		if (zone instanceof HotZone) {
			HotZone hz = (HotZone) zone;
			hz.dissipatePos=dissipatePos;
			for (ChunkPosition pos: expansionStack)
				if (!hz.expansionStack.contains(pos))
					hz.expansionStack.push(pos);
			for (Entry<ChunkPosition,HashSet<Zone>> entryConnections: connectionPoints.entrySet()) {
				HashSet<Zone> connectSet = hz.connectionPoints.get(entryConnections.getKey());
				if (connectSet==null)
					hz.connectionPoints.put(entryConnections.getKey(), entryConnections.getValue());
				else
					connectSet.addAll(entryConnections.getValue());
			}
		}
	}

	private void consume(Zone z) {
		z.transferTo(this);
	}
	
	//Replace with normal zone
	private void decay() {
		Zone zone= new Zone(zonerAccess);
		zone.air=new AtmosMix(zone);
		transferTo(zone);
	}
	
	private boolean blockBlocksAtmos(Block b) {
		return (b instanceof BlockStation || b instanceof BlockStationGlass);
	}

	//Internal type-specific method for doing the split based on a list of sets to turn into new zones
	protected void doSplit(ArrayList<HashSet<ChunkPosition>> newZones) {
		for (HashSet<ChunkPosition> posSet:newZones) {
			HotZone newZone = new HotZone(zonerAccess);
			
			for (ChunkPosition pos:posSet) {
				newZone.addPos(pos);
				if (pos==dissipatePos) {
					newZone.dissipatePos=pos;
					dissipatePos=null;
				} else if (expansionStack.contains(pos)) {
					newZone.expansionStack.push(pos);
					expansionStack.remove(pos);
				} else if (connectionPoints.containsKey(pos)) {
					newZone.connectionPoints.put(pos, connectionPoints.get(pos));
					connectionPoints.remove(pos);
				}
			}
			newZone.air= air.take(newZone);
			System.out.println("Split made zone with volume of "+posSet.size());
		}
	}
}

interface GasContainer {
	public float getVolume();
}

class AtmosMix {
	private float[] gasAmounts;
	private float totalAmount;
	
	private float heatEnergy;
	
	private GasContainer container;
	
	public AtmosMix(GasContainer container) {
		gasAmounts = new float[GasType.values().length];
		this.container=container;
	}

	public void clear() {
		for (GasType g: GasType.values())
			gasAmounts[g.ordinal()]=0;
		totalAmount=0;
		heatEnergy=0;
	}

	public void addHeat(float h) {
		heatEnergy+=h;
		if (heatEnergy<0)
			heatEnergy=0;
	}

	public float getTemperatureC() {
		return getTemperatureK()-273.15f;
	}
	
	public float getTemperatureK() {
		return heatEnergy/.02079f/totalAmount;
	}
	
	public AtmosMix take(GasContainer newcontainer) {
		float totalvolume= (container.getVolume()+newcontainer.getVolume());
		float multiplier=newcontainer.getVolume()/totalvolume;
		AtmosMix takenMix = new AtmosMix(newcontainer);
		for (GasType g: GasType.values()) {
			float takeAmt = gasAmounts[g.ordinal()]*multiplier;
			takenMix.setAmount(g,takeAmt);
			addAmount(g, -takeAmt);
		}
		
		//float takenHeat= takenMix.totalAmount*heatEnergy/totalAmount;
		float takenHeat= heatEnergy*multiplier;
		
		takenMix.addHeat(takenHeat);
		addHeat(-takenHeat);
		
		return takenMix;
	}
	
	public float flow(AtmosMix other,float area) {
		float FLOWRATE=.25f;
		float MERGE_THRESHOLD=.1f;
		FLOWRATE*=area;
		
		boolean merge=true;
		
		for (GasType g: GasType.values()) {
			float transferAmt= (gasAmounts[g.ordinal()]/container.getVolume()-other.gasAmounts[g.ordinal()]/other.container.getVolume());
			if (Math.abs(transferAmt)>MERGE_THRESHOLD)
				merge=false;
			transferAmt*=FLOWRATE;
			
			addAmount(g,-transferAmt);
			other.addAmount(g,transferAmt);
		}
		
		float transferHeat= (heatEnergy/totalAmount)-(other.heatEnergy/other.totalAmount);
		if (Math.abs(transferHeat)>MERGE_THRESHOLD)
			merge=false;
		transferHeat*=FLOWRATE;
		
		addHeat(-transferHeat);
		other.addHeat(transferHeat);
		
		if (merge) {
			System.out.println("MERGE / PRESSURE: ("+getPressure()+","+other.getPressure()+") / TEMP: ("+getTemperatureC()+","+getTemperatureC()+")");
			return Float.NaN;
		}
		
		float flowForce=other.getPressure()-getPressure();
		flowForce*=FLOWRATE*.002f;
		
		return flowForce;
	}
	
	public void transferTo(AtmosMix other) {
		for (GasType g: GasType.values())
			other.addAmount(g,gasAmounts[g.ordinal()]);
		
		other.addHeat(heatEnergy);
	}
	
	public void setAmount(GasType gas, float amt) {
		totalAmount+= amt-gasAmounts[gas.ordinal()];
		
		gasAmounts[gas.ordinal()]= amt;
	}
	
	public void addAmount(GasType gas, float amt) {
		totalAmount+= amt;
		
		gasAmounts[gas.ordinal()]+= amt;
	}
	
	public float[] getMakeup() {
		float[] makeup = new float[GasType.values().length];
		
		for (GasType g: GasType.values())
			makeup[g.ordinal()]= gasAmounts[g.ordinal()]/totalAmount;
		
		return makeup;
	}
	
	public float getPressure() {
		float R=.083144621f;
		return (totalAmount*R*getTemperatureK()/container.getVolume());
	}
	
	//Force pressure to a specific value, changing the gas amounts.
	public void setPressure(float p) { //TODO should we preserve the temperature?
		float multiplier = p/getPressure();
		
		for (GasType g: GasType.values()) {
			setAmount(g,gasAmounts[g.ordinal()]*multiplier);
		}
	}
}

class ForcePoint {
	public ForcePoint(float force,Vec3 point) {
		this.pos=point;
		this.force=force;
	}
	public Vec3 pos;
	public float force;
}