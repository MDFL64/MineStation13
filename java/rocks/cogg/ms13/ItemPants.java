package rocks.cogg.ms13;

import java.awt.image.BufferedImage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPants extends ItemClothing {
	public ItemPants(int par1) {
		super(par1);
	}

	@Override
	public BufferedImage getClothingTexture(ItemStack itemStack) {
		return imgPants;
	}
}
