package net.c0gg.ms13;

import java.awt.image.BufferedImage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;

public class ItemShirt extends ItemClothing {
	public ItemShirt(int par1) {
		super(par1);
	}

	@Override
	public BufferedImage getClothingTexture(ItemStack itemStack) {
		return imgShirt;
	}
}