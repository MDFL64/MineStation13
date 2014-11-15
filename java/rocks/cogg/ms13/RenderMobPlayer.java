package rocks.cogg.ms13;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;

//Wrapper for our generic mob renderer

public class RenderMobPlayer extends RenderPlayer {
	private static final RenderMob renderMob= new RenderMob();
	
	/*public RenderMobPlayer() {
		//renderMob= new RenderMob();
		//mainModel= modelBiped= new ModelBiped(-1);
		//modelBiped= (ModelBiped)mainModel;
	}*/
	
	/*public void renderFirstPersonArm(EntityPlayer par1EntityPlayer)
    {
        System.out.println("yes");
		float f = 1.0F;
        GL11.glColor3f(f, f, f);
        modelBiped.onGround = 0.0F;
        modelBiped.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, par1EntityPlayer);
        modelBiped.bipedRightArm.render(0.0625F);
        modelBiped.bipedLeftArm.render(0.0625F);
    }*/
	
	
	@Override
	public void doRender(Entity entity, double x, double y, double z,float yaw, float delta) {
		renderMob.doRender(entity, x, y, z, yaw, delta);
        //super.doRenderLiving(par1EntityLivingBase, par2, par4, par6, par8, par9);
	    
	}

	/*@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}*/
}
