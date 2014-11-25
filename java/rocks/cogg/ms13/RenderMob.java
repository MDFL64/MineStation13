package rocks.cogg.ms13;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderMob extends RendererLivingEntity {
	private ModelBiped modelBiped;
	private ModelBiped modelSuit;
	
	public RenderMob() {
		super(new ModelBiped(), .5f);
		
		modelBiped= (ModelBiped)this.mainModel;
		modelSuit= new ModelBiped(.5f);
		renderManager= RenderManager.instance;
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z,float yaw, float delta) {
		float f = 1.0F;
	    GL11.glColor3f(f,f,f);
	    
	    if (entity instanceof EntityPlayer) {
	    	EntityPlayer ply = (EntityPlayer)entity;
	    	
	    	ItemStack stackL = ply.inventory.mainInventory[0];
			if (stackL!=null)
				modelBiped.heldItemLeft=1;
			
			ItemStack stackR = ply.inventory.mainInventory[1];
			if (stackR!=null)
				modelBiped.heldItemRight=1;
	    }
	    
	    modelBiped.isSneak = entity.isSneaking();
	    
	    double realY = y - (double)entity.yOffset;

        if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)); //TODO we handle mob instead of player now, is this correct?
        {
            realY -= 0.125D;
        }
        
        doRender((EntityLivingBase)entity, x, realY, z, yaw, delta); //was doRenderLiving
        
        modelBiped.isSneak=false;
        modelBiped.heldItemLeft= modelBiped.heldItemRight= 0;
	}
	
	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float par3)
    {
		if (pass==0) {
			this.setRenderPassModel(modelSuit);
			if (entity instanceof EntityPlayer) {
				EntityPlayer ply = (EntityPlayer)entity;
				MsInvInventory inv = (MsInvInventory)ply.inventory;
				inv.texture.bindSuit();
			}
			return 1;
		}
        return -1;
    }
	
	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float par2)
    {
		if (entity instanceof EntityPlayer) {
			EntityPlayer ply = (EntityPlayer)entity;
			//I assume this won't network well
			ItemStack stackL = ply.inventory.mainInventory[0];
			if (stackL!=null) {
				int c = stackL.getItem().getColorFromItemStack(stackL, 0);
	            float r = (float)(c >> 16 & 255) / 255.0F;
	            float g = (float)(c >> 8 & 255) / 255.0F;
	            float b = (float)(c & 255) / 255.0F;
	            GL11.glColor4f(r, g, b, 1);
				
				GL11.glPushMatrix();
				modelBiped.bipedLeftArm.postRender(0);
				GL11.glTranslatef(.4f, .8f, -.05f);
				GL11.glRotatef(45, 0, 0, 1);
				GL11.glRotatef(-120, 1, 0, 0);
				GL11.glScalef(.3f, .3f, .3f);
				renderManager.itemRenderer.renderItem(ply, stackL, 0);
				GL11.glPopMatrix();
			}
			
			ItemStack stackR = ply.inventory.mainInventory[1];
			if (stackR!=null) {
				int c = stackR.getItem().getColorFromItemStack(stackR, 0);
	            float r = (float)(c >> 16 & 255) / 255.0F;
	            float g = (float)(c >> 8 & 255) / 255.0F;
	            float b = (float)(c & 255) / 255.0F;
	            GL11.glColor4f(r, g, b, 1);
				
				GL11.glPushMatrix();
				modelBiped.bipedRightArm.postRender(0);
				GL11.glTranslatef(-.4f, .8f, -.05f);
				GL11.glRotatef(45, 0, 0, 1);
				GL11.glRotatef(-120, 1, 0, 0);
				GL11.glScalef(.3f, .3f, .3f);
				renderManager.itemRenderer.renderItem(ply, stackR, 0);
				GL11.glPopMatrix();
			}
		}
    }

	@Override
	protected void bindEntityTexture(Entity entity)
    {
		if (entity instanceof EntityPlayer) {
			EntityPlayer ply = (EntityPlayer)entity;
			//MsInvInventory inv = (MsInvInventory)ply.inventory;
			//inv.texture.bind();
		}
		
        //Grab texture from entity and do coolstuff
    }
	
	@Override //This does nothing but we have to override it anyway
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
