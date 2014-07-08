package net.c0gg.ms13;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.naming.BinaryRefAddr;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="ms13", name=ModMinestation.nameFancy, version="0")
//@NetworkMod(clientSideRequired=true, serverSideRequired=false,channels={"ms13"},packetHandler=PacketHandlerMinestation.class)
public class ModMinestation {
	static final String nameFancy="MineStation13";
	static final int baseGenIndex=240; //Keep generated blocks within the 1 byte limit. It might be cruddy, but it makes it easier to make the world generator work.
	static final int baseBlockIndex=500;
	static final int baseItemIndex=2500;
	static final int dimensionIdAsteroid=13;
	
	static final int renderBlockAirlockFrameId = 50;
	
	static final RoundManager roundManager = new RoundManager();
	
	//Creative tab
	static final CreativeTabs tabSpacestation = new CreativeTabs("mineStation") {
        @Override
		public ItemStack getIconItemStack() {
        	return new ItemStack(blockStationBlock,1,0);
        }

		@Override
		public Item getTabIconItem() {
			// TODO add stuff
			return null;
		}
	};
	
	static final Material materialStationMetal=new Material(MapColor.ironColor);
	static final SoundType soundStationFootstep= new SoundTypeMs("stationMetal", 1, 1); //new StepSoundMs("stationMetal",1,1); //replaced with soundtype ??? TODO FIX StepsoundMs
	
	//Blocks
	static final Block blockAsteroid=new BlockAsteroid(baseGenIndex);
	
	static final Block blockStationBlockFree=new BlockStationFree(baseBlockIndex).setHardness(.5f).setResistance(10).setStepSound(soundStationFootstep);
	static final Block blockStationBlock=new BlockStationWelded(baseBlockIndex+1).setResistance(20).setStepSound(soundStationFootstep);
	static final Block blockStationReinforcedFree=new BlockStationFree(baseBlockIndex+2).setHardness(1).setResistance(20).setStepSound(soundStationFootstep);
	static final Block blockStationReinforced=new BlockStationWelded(baseBlockIndex+3).setResistance(40).setStepSound(soundStationFootstep);
	
	static final Block blockStationGlass=new BlockStationGlass();//	(baseBlockIndex+4);
	
	static final Block blockAirlockAssembly=new BlockAirlockAssembly(baseBlockIndex+5).setStepSound(soundStationFootstep);
	static final Block blockAirlockFrame=new BlockAirlockFrame(baseBlockIndex+6).setStepSound(soundStationFootstep);
	
	static final Block blockFloorTile=new BlockFloorTile(baseBlockIndex+7);
	
	static final Block blockLightbulb= new BlockLightbulb();//	(baseBlockIndex+8);
	static final Block blockLadder= new BlockStationLadder();//	(baseBlockIndex+9);
	
	static final Block blockTeleport= new BlockTeleport(baseBlockIndex+100,Material.wood);
	
	//Items
	static final Item itemScrewdriver=new ItemScrewdriver();//	(baseItemIndex);
	static final Item itemWelder=new ItemWelder(baseItemIndex+1);
	static final Item itemIdCard=new ItemIdCard();//				(baseItemIndex+2);
	static final Item itemAntagonistCard=new ItemAntagonistCard(baseItemIndex+3);
	static final Item itemWireCutters=new ItemHackingtool();//	(baseItemIndex+4);
	static final Item itemSolderGun=new ItemHackingtool();//		(baseItemIndex+5);
	static final Item itemMultitool=new ItemHackingtool();//		(baseItemIndex+6);
	static final Item itemCrowbar=new ItemCrowbar(baseItemIndex+7);
	static final Item itemAirlockDoor=new ItemAirlockDoor(baseItemIndex+8);
	static final Item itemPants=new ItemPants(baseItemIndex+9);
	static final Item itemShirt=new ItemShirt(baseItemIndex+10);
	static final Item itemGloves=new ItemGloves(baseItemIndex+11);
	static final Item itemShoes=new ItemShoes(baseItemIndex+12);
	static final Item itemBoots=new ItemBoots(baseItemIndex+13);
	
	//I honestly don't know what goes here...It does tend to complain about anonymous items if you don't register items and blocks here, though.
	@EventHandler
	public void preload(FMLPreInitializationEvent event) {

		//Block registry
		blockAsteroid.setBlockName("ms13.asteroid");
		GameRegistry.registerBlock(blockAsteroid,null,"asteroid");
		GameRegistry.registerItem(new ItemMultiTexture(blockAsteroid,blockAsteroid,BlockAsteroid.subTypes),"asteroid");
		
		//Item.itemsList[blockFloorTile.blockID]= (new ItemColored(blockFloorTile, true)).setBlockNames(BlockFloorTile.subTypes);
		
		blockFloorTile.setBlockName("ms13.floortile");
		blockFloorTile.setBlockTextureName("ms13:floortile");
		GameRegistry.registerBlock(blockFloorTile,null,"floortile");
		GameRegistry.registerItem((new ItemColored(blockFloorTile, true)).func_150943_a(BlockFloorTile.subTypes),"floortile");
		
		registerBlock(blockStationBlockFree,"stationBlockFree","Station Hull Block");
    	registerBlock(blockStationBlock,"stationBlock","Station Hull Block (Welded)");
    	registerBlock(blockStationReinforcedFree,"stationReinforcedFree","Reinforced Station Hull Block");
    	registerBlock(blockStationReinforced,"stationReinforced","Reinforced Station Hull Block (Welded)");
    	registerBlock(blockStationGlass,"stationGlass","Reinforced Glass");
    	registerBlock(blockAirlockAssembly,"airlockAssembly","Airlock Assembly Component");
    	registerBlock(blockAirlockFrame,"airlockFrame","Airlock Frame");
    	registerBlock(blockLightbulb,"lightbulb","Light Bulb");
    	registerBlock(blockLadder,"ladder","Ladder");
    	registerBlock(blockTeleport,"tempTeleport","TELEPORTAL");
		
    	//Item registry
    	registerItem(itemScrewdriver,"screwdriver","Screwdriver");
    	registerItem(itemWelder,"weldgun","Welding Gun");
    	registerItem(itemIdCard,"idCard","ID Card");
    	registerItem(itemAntagonistCard,"antagonistCard","Antagonist Card");
    	registerItem(itemWireCutters,"wirecutters","Wirecutters");
    	registerItem(itemSolderGun,"soldergun","Soldering Gun");
    	registerItem(itemMultitool,"multitool","Multitool");
    	registerItem(itemCrowbar,"crowbar","Crowbar");
    	registerItem(itemAirlockDoor,"airlockdoor","Airlock Door");
    	registerItem(itemPants,"pantsGeneric","Pants");
    	registerItem(itemShirt,"shirtGeneric","Shirt");
    	
    	registerItem(itemGloves,"glovesGeneric","Gloves");
    	registerItem(itemShoes,"shoesGeneric","Shoes");
    	registerItem(itemBoots,"bootsGeneric","Boots");
	}
	
    @EventHandler
    public void load(FMLInitializationEvent event) {
    	//Init networking
    	new PacketHandlerMinestation();
    	
    	//Register player textures.
    	if (event.getSide()==Side.CLIENT) {
	    	ItemClothing.loadImages();
	    	TexturePlayer.loadImages();
	    }
    	
    	//Dimension registry
    	DimensionManager.registerProviderType(dimensionIdAsteroid,WorldProviderAsteroid.class,false);
    	DimensionManager.registerDimension(dimensionIdAsteroid,dimensionIdAsteroid);
    	
    	//Ticker registry TODO These classes no longer need to exist, tick listening code can be moved elsewhere.
    	if (event.getSide()==Side.SERVER) {
    		FMLCommonHandler.instance().bus().register(new TickerAtmos());
    		FMLCommonHandler.instance().bus().register(new TickerPhysExt());
	    } else {
	    	FMLCommonHandler.instance().bus().register(new TickerInvSwapper());
	    }
    	
    	//Language registry
    	
    	//LanguageRegistry langReg = LanguageRegistry.instance();
    	//langReg.addStringLocalization("itemGroup.tabMineStation",nameFancy);
    	//langReg.addStringLocalization("container.airlocksetup","What access key should this airlock use?");
    	
    	//Block rendering registry
    	RenderingRegistry.registerBlockHandler(new RenderBlockAirlockFrame());
    	
    	//Tile entity registry
    	GameRegistry.registerTileEntity(TileEntityAirlock.class,"airlock");
    	
    	//Tile entity rendering registry
    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAirlock.class, new RenderTileAirlock());
    	
    	//Entity Rendering registry
    	RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderMobPlayer());
    	
    	//Key bind registry
    	//KeyBindingRegistry.registerKeyBinding(new KeyHandlerMinestation()); TODO update
    	
    	//Register this for events
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void loadSounds(SoundLoadEvent event) {
    	//These sounds are pretty terrible...I was originally stealing stuff from HL2 but then decided it would be
    	//a good idea not to commit that so I made my own placeholders.
    	/*
    	event.manager.unloadSoundSystem() addSound("ms13:step/stationMetal1.wav"); //TODO fix this garbage
    	event.manager.addSound("ms13:step/stationMetal2.wav");
    	event.manager.addSound("ms13:step/stationMetal3.wav");
    	event.manager.addSound("ms13:step/stationMetal4.wav");
    	
    	event.manager.addSound("ms13:dig/stationMetal.wav");*/
    }
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
    	//Command
    	ServerCommandManager serverCmd = ((ServerCommandManager)MinecraftServer.getServer().getCommandManager());
    	serverCmd.registerCommand(new CommandAtmos());
    	serverCmd.registerCommand(new CommandRound());
    	serverCmd.registerCommand(new CommandStructure());
    	
    	//Add atmos system for map
    	new AtmosZoner(MinecraftServer.getServer().worldServerForDimension(dimensionIdAsteroid));
    }
    
    public static void registerBlock(Block b, String name, String fancyName) {
    	b.setBlockName("ms13."+name);
	    b.setBlockTextureName("ms13:"+name);
    	
    	GameRegistry.registerBlock(b,"ms13."+name);
    }
    
    public static void registerItem(Item i,String name, String fancyName) {
	    i.setUnlocalizedName("ms13."+name);
	    i.setTextureName("ms13:"+name);
    	
    	/*if (fancyName!=null) {
    		LanguageRegistry.instance().addStringLocalization(i.getUnlocalizedName()+".name",fancyName);
    	}*/
    	
    	GameRegistry.registerItem(i,i.getUnlocalizedName());
    }
    
    @SubscribeEvent
    public void onEntJoined(EntityJoinWorldEvent event) {
    	if (event.entity instanceof EntityPlayer) {
    		EntityPlayer ply = (EntityPlayer)event.entity;
    		
    		ply.inventory = new MsInvInventory(ply);
    		ply.inventoryContainer=new MsInvContainer(ply);
    		ply.openContainer=ply.inventoryContainer;
    	}
    }
    
    @SubscribeEvent
    public void onEntDamaged(LivingHurtEvent event) {
    	event.setCanceled(true);
    	DamageManager.applyDamage(event.source,event.ammount);
    }
    
    /*@SubscribeEvent //something should probably be done with this later...
    public void onDrawHotbar(RenderGameOverlayEvent event) {
    	if (event.type==ElementType.HOTBAR && event.isCancelable()) { //Only PRE events are can be canceled.
    		event.setCanceled(true);
    	}
    }*/
}

class BlockTeleport extends Block {
	public BlockTeleport(int par1, Material par2Material) {
		super(par2Material);
		setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
		if (player instanceof EntityPlayerMP) {
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)player,ModMinestation.dimensionIdAsteroid,new TeleporterSpaceStation());
		}
        return true;
    }
}

class TeleporterSpaceStation extends Teleporter {
	public TeleporterSpaceStation() {
		super(MinecraftServer.getServer().worldServerForDimension(0));
	}
	
	@Override
	public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
    {
        par1Entity.setLocationAndAngles(0,80,0,par1Entity.rotationYaw,0.0f);
        par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0d;
    }
}

class CommandAtmos extends CommandBase {

	@Override
	public String getCommandName() {
		return "atmos";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if(sender instanceof EntityPlayerMP) { //TODO not sure if okay to force MP version of player, will it work in SP?
			EntityPlayer player = (EntityPlayer)sender;
			AtmosZoner atmos = AtmosZoner.getForWorld(player.worldObj);
			if (astring.length>0&&atmos!=null) {
				ChunkPosition pos = new ChunkPosition(player.getPosition(1));
				if (astring[0].equals("put")) {
					atmos.testPut(pos);
				} else if (astring[0].equals("get")) {
					player.addChatMessage(new ChatComponentText(atmos.testGet(pos)));
				} else if (astring[0].equals("debug")) {
					PacketHandlerMinestation.svSendAtmosDebugToggle((EntityPlayerMP)player); //Yolo ~Pdan
				} else {
					player.addChatMessage(new ChatComponentText("Function '"+astring[0]+"' does not exist. Valid functions are: put get debug"));
				}
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "This is the atmospheric diagnostic/debug command. See source for usage.";
	}
}

class CommandRound extends CommandBase {

	@Override
	public String getCommandName() {
		return "round";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if (astring.length>0) {
			if (astring[0].equals("new")) {
				ModMinestation.roundManager.newRound();
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "This command is supposed to set up/reset the space station game. It currently does not do much.";
	}
}

class CommandStructure extends CommandBase {

	@Override
	public String getCommandName() {
		return "struct";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if(sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)sender;
			World world = player.worldObj;
			if (astring.length>0) {
				if (astring[0].equals("save")&&astring.length>1) {
					String saveName=astring[1];
					
					MovingObjectPosition hit = PlayerUtil.lookTraceBlocks(player,50);
					if (hit!=null)
						StructureManager.save(saveName,player.worldObj,new ChunkPosition(hit.blockX,hit.blockY,hit.blockZ));
				} else if (astring[0].equals("load")&&astring.length>1) {
					String saveName=astring[1];
					
					MovingObjectPosition hit = PlayerUtil.lookTraceBlocks(player,50);
					if (hit!=null)
						StructureManager.load(saveName, player.worldObj, new StructurePlacerSimple(hit.blockX,hit.blockY,hit.blockZ));
				}
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "This command saves and loads structures. See source for usage.";
	}
}