package rocks.cogg.ms13;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.Event.*;
import net.minecraftforge.event.terraingen.*;

public class ChunkProviderAsteroid implements IChunkProvider
{
    private Random endRNG;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;
    public NoiseGeneratorOctaves noiseGen5;
    private World endWorld;
    private double[] densities;
    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;
    int[][] field_73203_h = new int[32][32];
    private static final String __OBFID = "CL_00000397";
    

    public ChunkProviderAsteroid(World par1World, long par2)
    {
    	
        this.endWorld = par1World;
        this.endRNG = new Random(par2);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);

        NoiseGenerator[] noiseGens = {noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5};
        noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, this.endRNG, noiseGens);
        this.noiseGen1 = (NoiseGeneratorOctaves)noiseGens[0];
        this.noiseGen2 = (NoiseGeneratorOctaves)noiseGens[1];
        this.noiseGen3 = (NoiseGeneratorOctaves)noiseGens[2];
        this.noiseGen4 = (NoiseGeneratorOctaves)noiseGens[3];
        this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
    }

    public void func_147420_a(int p_147420_1_, int p_147420_2_, Block[] p_147420_3_, BiomeGenBase[] p_147420_4_)
    {
        byte b0 = 2;
        int k = b0 + 1;
        byte b1 = 33;
        int l = b0 + 1;
        this.densities = this.initializeNoiseField(this.densities, p_147420_1_ * b0, 0, p_147420_2_ * b0, k, b1, l);

        for (int i1 = 0; i1 < b0; ++i1)
        {
            for (int j1 = 0; j1 < b0; ++j1)
            {
                for (int k1 = 0; k1 < 32; ++k1)
                {
                    double d0 = 0.25D;
                    double d1 = this.densities[((i1 + 0) * l + j1 + 0) * b1 + k1 + 0];
                    double d2 = this.densities[((i1 + 0) * l + j1 + 1) * b1 + k1 + 0];
                    double d3 = this.densities[((i1 + 1) * l + j1 + 0) * b1 + k1 + 0];
                    double d4 = this.densities[((i1 + 1) * l + j1 + 1) * b1 + k1 + 0];
                    double d5 = (this.densities[((i1 + 0) * l + j1 + 0) * b1 + k1 + 1] - d1) * d0;
                    double d6 = (this.densities[((i1 + 0) * l + j1 + 1) * b1 + k1 + 1] - d2) * d0;
                    double d7 = (this.densities[((i1 + 1) * l + j1 + 0) * b1 + k1 + 1] - d3) * d0;
                    double d8 = (this.densities[((i1 + 1) * l + j1 + 1) * b1 + k1 + 1] - d4) * d0;

                    for (int l1 = 0; l1 < 4; ++l1)
                    {
                        double d9 = 0.125D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i2 = 0; i2 < 8; ++i2)
                        {
                            int j2 = i2 + i1 * 8 << 11 | 0 + j1 * 8 << 7 | k1 * 4 + l1;
                            short short1 = 128;
                            double d14 = 0.125D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int k2 = 0; k2 < 8; ++k2)
                            {
                                Block block = null;

                                if (d15 > 0.0D)
                                {
                                    block = ModMinestation.blockAsteroid;
                                }

                                p_147420_3_[j2] = block;
                                j2 += short1;
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void func_147421_b(int p_147421_1_, int p_147421_2_, Block[] p_147421_3_, BiomeGenBase[] p_147421_4_)
    {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, p_147421_1_, p_147421_2_, p_147421_3_, p_147421_4_);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return;

        for (int k = 0; k < 16; ++k)
        {
            for (int l = 0; l < 16; ++l)
            {
                byte b0 = 1;
                int i1 = -1;
                Block block = ModMinestation.blockAsteroid;
                Block block1 = ModMinestation.blockAsteroid;

                for (int j1 = 127; j1 >= 0; --j1)
                {
                    int k1 = (l * 16 + k) * 128 + j1;
                    Block block2 = p_147421_3_[k1];

                    if (block2 != null && block2.getMaterial() != Material.air)
                    {
                        if (block2 == Blocks.stone)
                        {
                            if (i1 == -1)
                            {
                                if (b0 <= 0)
                                {
                                    block = null;
                                    block1 = ModMinestation.blockAsteroid;
                                }

                                i1 = b0;

                                if (j1 >= 0)
                                {
                                    p_147421_3_[k1] = block;
                                }
                                else
                                {
                                    p_147421_3_[k1] = block1;
                                }
                            }
                            else if (i1 > 0)
                            {
                                --i1;
                                p_147421_3_[k1] = block1;
                            }
                        }
                    }
                    else
                    {
                        i1 = -1;
                    }
                }
            }
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int par1, int par2)
    {
        return this.provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int par1, int par2)
    {
        this.endRNG.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
        Block[] ablock = new Block[32768];
        this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
        this.func_147420_a(par1, par2, ablock, this.biomesForGeneration);
        this.func_147421_b(par1, par2, ablock, this.biomesForGeneration);
        Chunk chunk = new Chunk(this.endWorld, ablock, par1, par2);
        byte[] abyte = chunk.getBiomeArray();

        for (int k = 0; k < abyte.length; ++k)
        {
            abyte[k] = (byte)this.biomesForGeneration[k].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(this, par1ArrayOfDouble, par2, par3, par4, par5, par6, par7);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return event.noisefield;

        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;
        this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
        this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
        d0 *= 2.0D;
        this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, par2, par3, par4, par5, par6, par7, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
        this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, par2, par3, par4, par5, par6, par7, d0, d1, d0);
        this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, par2, par3, par4, par5, par6, par7, d0, d1, d0);
        int k1 = 0;
        int l1 = 0;

        for (int i2 = 0; i2 < par5; ++i2)
        {
            for (int j2 = 0; j2 < par7; ++j2)
            {
                double d2 = (this.noiseData4[l1] + 256.0D) / 512.0D;

                if (d2 > 1.0D)
                {
                    d2 = 1.0D;
                }

                double d3 = this.noiseData5[l1] / 4000.0D; //8000

                if (d3 < 0.0D)
                {
                    d3 = -d3 * 0.3D;
                }

                d3 = d3 * 3.0D - 2.0D;
                float f = (float)(i2 + par2 - 0) / 1.0F;
                float f1 = (float)(j2 + par4 - 0) / 1.0F;
                float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

                if (f2 > 80.0F)
                {
                    f2 = 80.0F;
                }

                if (f2 < -100.0F)
                {
                    f2 = -100.0F;
                }

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                d3 /= 8.0D;
                d3 = 0.0D;

                if (d2 < 0.0D)
                {
                    d2 = 0.0D;
                }

                d2 += 0.5D;
                d3 = d3 * (double)par6 / 16.0D;
                ++l1;
                double d4 = (double)par6 / 2.0D;

                for (int k2 = 0; k2 < par6; ++k2)
                {
                    double d5 = 0.0D;
                    double d6 = ((double)k2 - d4) * 8.0D / d2;

                    if (d6 < 0.0D)
                    {
                        d6 *= -1.0D;
                    }

                    double d7 = this.noiseData2[k1] / 256.0D; //512
                    double d8 = this.noiseData3[k1] / 256.0D;
                    double d9 = (this.noiseData1[k1] / 20.0D + 1.0D) / 2.0D; //First was 10

                    if (d9 < 0.0D)
                    {
                        d5 = d7;
                    }
                    else if (d9 > 1.0D)
                    {
                        d5 = d8;
                    }
                    else
                    {
                        d5 = d7 + (d8 - d7) * d9;
                    }

                    d5 -= 8.0D;
                    d5 += (double)f2;
                    byte b0 = 2;
                    double d10;

                    if (k2 > par6 / 2 - b0)
                    {
                        d10 = (double)((float)(k2 - (par6 / 2 - b0)) / 64.0F);

                        if (d10 < 0.0D)
                        {
                            d10 = 0.0D;
                        }

                        if (d10 > 1.0D)
                        {
                            d10 = 1.0D;
                        }

                        d5 = d5 * (1.0D - d10) + -3000.0D * d10;
                    }

                    b0 = 8;

                    if (k2 < b0)
                    {
                        d10 = (double)((float)(b0 - k2) / ((float)b0 - 1.0F));
                        d5 = d5 * (1.0D - d10) + -30.0D * d10;
                    }

                    par1ArrayOfDouble[k1] = d5;
                    ++k1;
                }
            }
        }

        return par1ArrayOfDouble;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    /**
     * Populates chunk with ores and //secret rooms ~Pdan
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        BlockFalling.fallInstantly = true;

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(par1IChunkProvider, endWorld, endWorld.rand, par2, par3, false));
        int k1=0;
        int l1=0;
        int i2=0;
        int k = par2 * 16;
        int l = par3 * 16;
        BiomeGenBase biomegenbase = this.endWorld.getBiomeGenForCoords(k + 16, l + 16);
        biomegenbase.decorate(this.endWorld, this.endWorld.rand, k, l);
        
        boolean flag = false;
        boolean doGen = TerrainGen.populate(par1IChunkProvider, endWorld, endRNG, par2, par3, flag, DUNGEON);
        for (k1 = 0; doGen && k1 < 12; ++k1)
        {
            l1 = k + this.endRNG.nextInt(16) + 80;
            i2 = this.endRNG.nextInt(256);
            int j2 = l + this.endRNG.nextInt(16) + 80;
            (new GenStructureAsteroidRoom()).generate(this.endWorld, this.endRNG, k1, l1, i2);
        }
        
        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(par1IChunkProvider, endWorld, endWorld.rand, par2, par3, false));

        BlockFalling.fallInstantly = false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "RandomLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        BiomeGenBase biomegenbase = this.endWorld.getBiomeGenForCoords(par2, par4);
        return biomegenbase.getSpawnableList(par1EnumCreatureType);
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(int par1, int par2) {}
}