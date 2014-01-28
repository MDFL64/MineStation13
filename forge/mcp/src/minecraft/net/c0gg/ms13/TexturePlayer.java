package net.c0gg.ms13;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.Resource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TexturePlayer {
	static BufferedImage txBaseM = loadImage("ms13:textures/mob/base_m.png");
	static BufferedImage txBaseF = loadImage("ms13:textures/mob/base_f.png");
	
	private int id;
	
	public boolean gender;
	
	public int colorSkin;
	public int colorEyes;
	
	public BufferedImage txPants;
	
	public int colorPants;
	
	MsInvInventory inventory;
	
	public TexturePlayer(MsInvInventory inventory) {
		id = TextureUtil.glGenTextures();
		TextureUtil.allocateTexture(id, 64, 32);
		
		//Most of the stuff in here should eventually be determined by the player inventory class.
		
		//True for female
		gender=false;
		
		colorSkin=0xFFE3CEB1;
		colorEyes=0xFF525ED9;
		
		this.inventory=inventory;
		
		rebuild();
	}
	
	public void rebuild() {
		BufferedImage img = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
		for (int x=0;x<64;x++) {
			for (int y=0;y<32;y++) {
				if (x<32 || y>15)
					img.setRGB(x, y, colorSkin);
			}
		}
		
		if (gender)
			addLayer(img,txBaseF);
		else
			addLayer(img,txBaseM);
		
		img.setRGB(10, 12, colorEyes);
		img.setRGB(13, 12, colorEyes);
		
		//Pants
		addLayer(img,inventory.mainInventory[19]);
		//Shirt
		addLayer(img,inventory.mainInventory[16]);
		
		if (inventory.mainInventory[16]!= null && inventory.mainInventory[16].getItem() instanceof ItemClothing) {
			ItemClothing item = (ItemShirt)inventory.mainInventory[16].getItem();
			addLayer(img,item.getClothingTexture(inventory.mainInventory[16]),item.getColorFromItemStack(inventory.mainInventory[16],0)+0x88000000);
		}
		
		TextureUtil.uploadTextureImageSub(id, img, 0, 0, false, false);
	}
	
	//Add layer from a clothing item
	private void addLayer(BufferedImage base,ItemStack stack) {
		if (stack!= null && stack.getItem() instanceof ItemClothing) {
			ItemClothing item = (ItemClothing)stack.getItem();
			addLayer(base,item.getClothingTexture(stack),item.getColorFromItemStack(stack,0)+0x11000000);
		}
	}
	
	private void addLayer(BufferedImage base,BufferedImage layer,int color) {
		float[] compMod= new Color(color,true).getComponents(null);
		
		for (int x=0;x<64;x++) {
			for (int y=0;y<32;y++) {
				float[] compBase= new Color(base.getRGB(x, y),true).getComponents(null);
				float[] compLayer= new Color(layer.getRGB(x, y),true).getComponents(null);
				
				compBase[0]= compLayer[0]*compMod[0]*compLayer[3]+compBase[0]*(1-compLayer[3]);
				compBase[1]= compLayer[1]*compMod[1]*compLayer[3]+compBase[1]*(1-compLayer[3]);
				compBase[2]= compLayer[2]*compMod[2]*compLayer[3]+compBase[2]*(1-compLayer[3]);
				compBase[3]= Math.max(compBase[3], compLayer[3]);
				
				base.setRGB(x, y, new Color(compBase[0],compBase[1],compBase[2],compBase[3]).getRGB());
			}
		}
	}
	
	private void addLayer(BufferedImage base,BufferedImage layer) {
		addLayer(base,layer,0xFFFFFFFF);
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
	public static BufferedImage loadImage(String resourceName) {
		InputStream inputstream = null;

        try {
            Resource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(resourceName));
            inputstream = resource.getInputStream();
            BufferedImage img = ImageIO.read(inputstream);
            inputstream.close();
            return img;
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (inputstream != null)
            {
                try {
					inputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        return null;
	}
}