package net.c0gg.ms13;

import java.awt.image.BufferedImage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;

public abstract class ItemClothing extends net.minecraft.item.Item {
	
	protected static BufferedImage imgShirt;
	protected static BufferedImage imgPants;
	protected static BufferedImage imgGloves;
	protected static BufferedImage imgShoes;
	protected static BufferedImage imgBoots;
	
	public static void loadImages() { //TODO move to the player texture class
		imgShirt = TexturePlayer.loadImage("ms13:textures/mob/shirt_generic.png");
		imgPants= TexturePlayer.loadImage("ms13:textures/mob/pants_generic.png");
		imgGloves= TexturePlayer.loadImage("ms13:textures/mob/gloves_generic.png");
		imgShoes= TexturePlayer.loadImage("ms13:textures/mob/shoes_generic.png");
		imgBoots= TexturePlayer.loadImage("ms13:textures/mob/boots_generic.png");
	}
	
	public ItemClothing(int par1) {
//		super(par1);
		setMaxStackSize(1);
		setCreativeTab(ModMinestation.tabSpacestation);
	}
	
	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("clothingColor")) {
        	return par1ItemStack.getTagCompound().getInteger("clothingColor");
        }
		
		return 0xAAAAAA;
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
		par1ItemStack.setTagInfo("clothingColor", new NBTTagInt((int)(Math.random()*0xFFFFFF)));
	
		return par1ItemStack;
    }
	
	public abstract BufferedImage getClothingTexture(ItemStack itemStack);
}