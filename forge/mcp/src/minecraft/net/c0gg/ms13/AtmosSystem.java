package net.c0gg.ms13;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.google.common.collect.ArrayListMultimap;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class AtmosSystem {
	static private HashMap<World,AtmosSystem> atmosSystems=new HashMap<World,AtmosSystem>();
	private HashMap<ChunkPosition,Zone> areaMap;
	private ArrayList<Zone> zones;
	private World world;
	
	public AtmosSystem(World w) {
		atmosSystems.put(w,this);
		areaMap=new HashMap<ChunkPosition,Zone>();
		zones=new ArrayList<Zone>();
	}
	
	static public AtmosSystem getForWorld(World w) {
		return atmosSystems.get(w);
	}
	
	static public void updateAll() {
		for (AtmosSystem atmos:atmosSystems.values()) {
			ArrayList<Zone> deadZones= new ArrayList<Zone>();
			for (Zone zone:atmos.zones) {
				if (zone.update()) {
					deadZones.add(zone);
				}
			}
			for (Zone zone:deadZones) {
				atmos.zones.remove(zone);
			}
		}
	}
	
	public void stationBlockAdd(int x, int y, int z) {
		ChunkPosition p = new ChunkPosition(x,y,z);
		Zone a = getZoneFromPosition(p);
		if (a!=null) {
			a.trySplit(p);
		}
	}
	
	public void stationBlockRemove(int x, int y, int z) {
		ChunkPosition p = new ChunkPosition(x,y,z);
		
		ChunkPosition[] nearPositions = new ChunkPosition[] {new ChunkPosition(x+1,y,z),new ChunkPosition(x-1,y,z),new ChunkPosition(x,y+1,z),new ChunkPosition(x,y-1,z),new ChunkPosition(x,y,z+1),new ChunkPosition(x,y,z-1)};
		
		for (ChunkPosition np:nearPositions) {
			Zone a = getZoneFromPosition(np);
			if (a!=null) {
				a.tryExpand(p);
				break;
			}
		}
	}
	
	public void testPut(ChunkPosition pos) {
		AtmosMix mix=new AtmosMix();
		mix.gasAmounts[GasType.N2.ordinal()]=800;
		mix.gasAmounts[GasType.O2.ordinal()]=200;
		put(pos,mix);
	}
	
	public void put(ChunkPosition pos,AtmosMix m) {
		Zone zone = getZoneFromPosition(pos);
		if (zone==null) {
			zone = new Zone(pos);
		}
		zone.mix.add(m);
	}
	
	public String testGet(ChunkPosition pos) {
		Zone zone = getZoneFromPosition(pos);
		if (zone==null) {
			return "No air here!";
		} else {
			return zone.mix.getInfo();
		}
	}
	
	public int zoneCount() {
		return zones.size();
	}
	
	private Zone getZoneFromPosition(ChunkPosition p) {
		return areaMap.get(p);
	}

	//DONT TOUCH ANYTHING BELOW HERE UNLESS YOU KNOW WHAT YOU ARE DOING
	//Access modifiers don't seem to really work here, but they should give you an idea of what you should and shouldn't be calling.
	private class Zone {
		private HashSet<ChunkPosition> positions;
		
		private Stack<ChunkPosition> expansionStack;
		
		private boolean dead;
		
		public AtmosMix mix;
		
		//Make a zone. Seed it from one pos.
		public Zone(ChunkPosition pos) {
			positions=new HashSet<ChunkPosition>();
			expansionStack = new Stack<ChunkPosition>();
			zones.add(this);
			mix=new AtmosMix();
			
			tryExpand(pos);
		}

		//Make a zone from a pre-determined set of positions. Does not automatically expand.
		public Zone(HashSet<ChunkPosition> pSet) {
			positions=pSet;
			for (ChunkPosition p:pSet) {
				Zone oldArea = areaMap.put(p,this);
				if (oldArea!=null) {
					oldArea.removePos(p);
				}
			}
			
			expansionStack = new Stack<ChunkPosition>();
			zones.add(this);
			mix=new AtmosMix();
		}
		
		public void tryExpand(ChunkPosition p) {
			expansionStack.push(p);
			System.out.println("Atmos expand attempt.");
		}
		
		//Use modified A* to determine if area needs splitting
		//THIS CODE IS FULL OF AIDS!
		public void trySplit(ChunkPosition pSplit) {
			removePos(pSplit);
			
			ChunkPosition[] positionsToCheckForStart=new ChunkPosition[] {new ChunkPosition(pSplit.x+1,pSplit.y,pSplit.z),new ChunkPosition(pSplit.x-1,pSplit.y,pSplit.z),new ChunkPosition(pSplit.x,pSplit.y+1,pSplit.z),new ChunkPosition(pSplit.x,pSplit.y-1,pSplit.z),new ChunkPosition(pSplit.x,pSplit.y,pSplit.z+1),new ChunkPosition(pSplit.x,pSplit.y,pSplit.z-1)};
			
			Stack<ChunkPosition> startingPositions= new Stack<ChunkPosition>();
			
			for (ChunkPosition ip:positionsToCheckForStart) {
				if (positions.contains(ip)) {
					startingPositions.push(ip);
				}
			}
			
			ArrayList<HashSet<ChunkPosition>> newZones = new ArrayList<HashSet<ChunkPosition>>();
			
			//If we have only one starting position left, we can leave it because it can't join with anything else.
			while (startingPositions.size()>1) {
				/* 
				 * openSet: Positions we need to look at.
				 * closedSet: Positions we're done looking at.
				 */
				HashSet<ChunkPosition> openSet = new HashSet<ChunkPosition>();
				HashSet<ChunkPosition> closedSet = new HashSet<ChunkPosition>();
				
				ChunkPosition start = startingPositions.pop();
				ChunkPosition goal = startingPositions.pop();
				
				openSet.add(start);
				
				for (;;) {
					//This would indicate that we filled an entire closed off section of the area.
					if (openSet.isEmpty()) {
						//Push the goal back since we didn't reach it this time.
						startingPositions.push(goal);
						//Make a new area.
						newZones.add(closedSet);
						break;
					}
					
					//Search the open set for the closest to both a) the start and b) the goal.
					ChunkPosition pWinner=null;
					int sCost=-1;
					
					for (ChunkPosition ip:openSet) {
						if (ip.equals(goal)) {
							pWinner=goal;
							if (startingPositions.isEmpty())
								goal=null;
							else
								goal=startingPositions.pop();
							
							break;
						}
						
						
						//Nothing to see here, just shitting math everywhere because I don't want to use Math.pow
						int sdx = start.x-ip.x;int sdy = start.y-ip.y;int sdz = start.z-ip.z;
						int gdx = goal.x-ip.x;int gdy = goal.y-ip.y;int gdz = goal.z-ip.z;
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
					
					//Attempt to remove current position, just in case it was a goal.
					startingPositions.remove(pWinner);
					
					//Break if we have ABSOLUTELY no more goals
					if (goal==null&&startingPositions.isEmpty()) {
						break;
					}
					
					//Move to the closed set so we don't have to look at this position again.
					openSet.remove(pWinner);
					closedSet.add(pWinner);
					
					//See if we can add surrounding positions to the open set.
					ChunkPosition[] positionsToCheckForOpen=new ChunkPosition[] {new ChunkPosition(pWinner.x+1,pWinner.y,pWinner.z),new ChunkPosition(pWinner.x-1,pWinner.y,pWinner.z),new ChunkPosition(pWinner.x,pWinner.y+1,pWinner.z),new ChunkPosition(pWinner.x,pWinner.y-1,pWinner.z),new ChunkPosition(pWinner.x,pWinner.y,pWinner.z+1),new ChunkPosition(pWinner.x,pWinner.y,pWinner.z-1)};
					
					for (ChunkPosition ip:positionsToCheckForOpen) {
						if (positions.contains(ip)&&!closedSet.contains(ip)) {
							openSet.add(ip);
						}
					}
				}
			}
			
			//Actually create new areas
			if (!newZones.isEmpty()) {
				System.out.println("Atmos split. "+newZones.size()+" new areas.");
				System.out.println("-	Starting: "+positions.size());
				int i=0;
				//HERE (the rest is stupid debug messages!)
				for (HashSet<ChunkPosition> z:newZones) {
					Zone newZone = new Zone(z);
					newZone.mix.add(mix.split(z.size()));
					System.out.println("-	"+i+++": "+z.size());
				}
				System.out.println("-	Ending: "+positions.size());
			}
		}
		
		public boolean update() {
			updateExpansion(3);
			mix.setVolume(positions.size());
			
			return dead;
		}
		
		//Actually do expansion. Will attempt to expand a specified number of times.
		private void updateExpansion(int steps) {
			for (int i=0;i<steps;i++) {
				if (expansionStack.isEmpty()) return;
				ChunkPosition p = expansionStack.pop();
				
				if (areaMap.containsKey(p)) {
					Zone a = areaMap.get(p);
					if (a==this) {
						continue;
					} else {
						a.kill(this);
						continue;
					}
				}
				
				
				Block b = Block.blocksList[MinecraftServer.getServer().worldServerForDimension(ModMinestation.dimensionIdAsteroid).getBlockId(p.x,p.y,p.z)];
				if (b instanceof BlockStation || b instanceof BlockStationGlass) {
					continue;
				}
				
				if (p.y<=0||p.y>=255) {
					kill();
					break;
				}
				
				addPos(p);
				
				expansionStack.add(new ChunkPosition(p.x+1,p.y,p.z));
				expansionStack.add(new ChunkPosition(p.x-1,p.y,p.z));
				expansionStack.add(new ChunkPosition(p.x,p.y,p.z+1));
				expansionStack.add(new ChunkPosition(p.x,p.y,p.z-1));
				
				//Check up/down so we can get to the top/bottom fast and kill the area if needed
				expansionStack.add(new ChunkPosition(p.x,p.y-1,p.z));
				expansionStack.add(new ChunkPosition(p.x,p.y+1,p.z));
			}
		}
		
		//PLEASE USE THESE TWO FUNCTIONS UNLESS YOU HAVE A GOOD REASON NOT TO.
		
		//Add position. This checks if the position is still owned and safely de-owns it.
		private void addPos(ChunkPosition p) {
			Zone oldArea = areaMap.put(p,this);
			if (oldArea!=null) {
				oldArea.removePos(p);
			}
			positions.add(p);
		}
		
		//Remove position. This double checks if we still technically own the position.
		private void removePos(ChunkPosition p) {
			if (areaMap.get(p)==this) {
				areaMap.remove(p);
			}
			positions.remove(p);
		}
		
		//Death
		private void kill() {
			if (dead) return;
			for (ChunkPosition p:positions) {
				areaMap.remove(p);
			}
			dead=true;
			System.out.println("Atmos area died.");
		}
		
		//Merge
		private void kill(Zone other) {
			if (dead) return;
			HashSet<ChunkPosition> pcopy = (HashSet<ChunkPosition>)positions.clone();
			for (ChunkPosition p:pcopy) {
				other.addPos(p);
			}
			other.mix.add(mix);
			dead=true;
			System.out.println("Atmos area merged.");
		}
	}
}

class AtmosMix {
	private static final int unitAtm=1000;
	
	private int volume;
	
	public int[] gasAmounts;
	
	public AtmosMix() {
		gasAmounts = new int[GasType.values().length];
	}
	
	public void setVolume(int v) {
		volume=v;
	}
	
	public float getPressure() {
		return (getAmtCombined()/(float)volume);
	}
	
	private int getAmtCombined() {
		int amt=0;
		
		for (GasType g:GasType.values()) {
			amt+= gasAmounts[g.ordinal()];
		}
		
		return amt;
	}
	
	public String getInfo() {
		String info="Pressure: "+getPressure();
		info=info+"\nTotal Gas Amount: "+getAmtCombined();
		
		for (GasType g:GasType.values()) {
			info=info+"\n"+g.name()+" Amount: "+gasAmounts[g.ordinal()];
		}
		
		return info;
	}
	
	public void add(AtmosMix other) {
		for (GasType g:GasType.values()) {
			gasAmounts[g.ordinal()]+=other.gasAmounts[g.ordinal()];
		}
	}
	
	public AtmosMix split(int newVolume) {
		float fraction=newVolume/(float)volume;
		System.out.println("Fraction:"+fraction);
		AtmosMix newMix = new AtmosMix();
		
		for (GasType g:GasType.values()) {
			newMix.gasAmounts[g.ordinal()]=(int)(gasAmounts[g.ordinal()]*fraction);
			System.out.println("New mix amount:"+newMix.gasAmounts[g.ordinal()]);
			gasAmounts[g.ordinal()]-=newMix.gasAmounts[g.ordinal()];
			System.out.println("Our mix amount:"+gasAmounts[g.ordinal()]);
		}
		
		return newMix;
	}
}
