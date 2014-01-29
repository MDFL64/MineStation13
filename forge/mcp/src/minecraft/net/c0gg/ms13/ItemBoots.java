package net.c0gg.ms13;

import java.awt.image.BufferedImage;

import net.minecraft.item.ItemStack;

public class ItemBoots extends ItemShoes {

	public ItemBoots(int par1) {
		super(par1);
	}

	@Override
	public BufferedImage getClothingTexture(ItemStack itemStack) {
		return imgBoots;
	}

}
