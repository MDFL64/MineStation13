package rocks.cogg.ms13;

import java.awt.image.BufferedImage;

import net.minecraft.item.ItemStack;


public class ItemGloves extends ItemClothing {
	public ItemGloves(int par1) {
		super(par1);
	}

	@Override
	public BufferedImage getClothingTexture(ItemStack itemStack) {
		return imgGloves;
	}
}