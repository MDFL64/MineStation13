package net.c0gg.ms13;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBeach;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;

public class WorldProviderAsteroid extends WorldProvider {
	@Override
	protected void registerWorldChunkManager()
    {
        BiomeGenBase biome = new BiomeGenAsteroid(23).setBiomeName("Asteroid");
		worldChunkMgr = new WorldChunkManagerHell(biome,0);
        dimensionId = ModMinestation.dimensionIdAsteroid;
        hasNoSky = true;
    }
	
	@Override
	protected void generateLightBrightnessTable() {
		for (int i=0;i<16;i++) {
			lightBrightnessTable[i]=i/15f;
		}
		lightBrightnessTable[0]= 0.04f;
	}
	
	@Override
	public Vec3 getFogColor(float par1, float par2)
    {
        return this.worldObj.getWorldVec3Pool().getVecFromPool(0,0,0);
    }
	
	@Override
	public float calculateCelestialAngle(long par1, float par3)
    {
        return 0.5f;
    }
	
	@Override
	public String getDimensionName() {
		return "Asteroid";
	}
	
	@Override
	public IChunkProvider createChunkGenerator()
    {
        return new ChunkProviderAsteroid(this.worldObj, this.worldObj.getSeed());
    }
	
	@Override
	public ChunkCoordinates getEntrancePortalLocation()
    {
        return new ChunkCoordinates(0,200, 0);
    }
	
	@Override
	public boolean isSurfaceWorld()
    {
        return false;
    }
	
	@Override
	public boolean canRespawnHere() {
		return false;
	}
}

class BiomeGenAsteroid extends BiomeGenBase {
	public BiomeGenAsteroid(int par1) {
		super(par1);
		
		spawnableCaveCreatureList.clear();
		spawnableCreatureList.clear();
		spawnableMonsterList.clear();
		spawnableWaterCreatureList.clear();
		
		theBiomeDecorator = new BiomeAsteroidDecorator(this);
	}
}

class BiomeAsteroidDecorator extends BiomeDecorator
{
    protected WorldGenerator oreCopperGen;
    protected WorldGenerator oreIronGen;
    protected WorldGenerator orePlasmaGen;
    protected WorldGenerator oreSilverGen;
    protected WorldGenerator oreGoldGen;
    protected WorldGenerator oreUraniumGen;
    protected WorldGenerator oreDiamondGen;
    protected WorldGenerator oreBananaGen;

    public BiomeAsteroidDecorator(BiomeGenBase par1BiomeGenBase)
    {
        //super(par1BiomeGenBase);
        oreCopperGen = new WorldGenMinable(ModMinestation.blockAsteroid,1,20,ModMinestation.blockAsteroid);
        oreIronGen = new WorldGenMinable(ModMinestation.blockAsteroid,2,20,ModMinestation.blockAsteroid);
        
        orePlasmaGen = new WorldGenMinable(ModMinestation.blockAsteroid,3,15,ModMinestation.blockAsteroid);
        oreSilverGen = new WorldGenMinable(ModMinestation.blockAsteroid,4,15,ModMinestation.blockAsteroid);
        oreGoldGen = new WorldGenMinable(ModMinestation.blockAsteroid,5,15,ModMinestation.blockAsteroid);
        
        oreUraniumGen = new WorldGenMinable(ModMinestation.blockAsteroid,6,10,ModMinestation.blockAsteroid);
        oreDiamondGen = new WorldGenMinable(ModMinestation.blockAsteroid,7,10,ModMinestation.blockAsteroid);
        
        oreBananaGen = new WorldGenMinable(ModMinestation.blockAsteroid,8,5,ModMinestation.blockAsteroid);
    }
    
    @Override
    protected void genDecorations(BiomeGenBase par1BiomeGenBase)
    {
    	genStandardOre1(3,oreCopperGen,0,64);
    	genStandardOre1(3,oreIronGen,0,64);
    	
    	genStandardOre1(2,orePlasmaGen,0,64);
    	genStandardOre1(2,oreSilverGen,0,64);
    	genStandardOre1(2,oreGoldGen,0,64);
    	
    	genStandardOre1(1,oreUraniumGen,0,64);
    	genStandardOre1(1,oreDiamondGen,0,64);
    	
    	genStandardOre1(1,oreBananaGen,0,64);
    }
}
