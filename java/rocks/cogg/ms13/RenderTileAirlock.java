package rocks.cogg.ms13;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderTileAirlock extends TileEntitySpecialRenderer {
	
	private static final ResourceLocation textureCommon = new ResourceLocation("ms13:textures/airlocks/common.png");
	private static final ResourceLocation textureAssembly = new ResourceLocation("ms13:textures/airlocks/assembly.png");
	private static final ResourceLocation textureInternal = new ResourceLocation("ms13:textures/airlocks/internal.png");
	private static final ResourceLocation textureExternal = new ResourceLocation("ms13:textures/airlocks/external.png");
	
	//This was hastily put together.. TODO fix
	private void bindDoorTexture(String name) {
		if (name=="internal") {
			bindTexture(textureInternal);
		} else if (name=="external") {
			bindTexture(textureExternal);
		} else if (name=="assembly") {
			bindTexture(textureAssembly);
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity ent, double x, double y,double z, float f) {
		TileEntityAirlock entAirlock=(TileEntityAirlock)ent;
		
		boolean direction = entAirlock.getDirection();
		boolean orientation = entAirlock.getFlag(AirlockFlag.SLIDES_SIDEWAYS);
		
		float openFraction=entAirlock.getOpenFraction();
		int textureIndex=entAirlock.getIndicatorState();
		
		String typeTop=entAirlock.getDoorType(false).name;
		String typeBottom=entAirlock.getDoorType(true).name;
		
		boolean welded = entAirlock.getFlag(AirlockFlag.WELDED);
		
		float offsetForIndex=textureIndex/4f;
		
		GL11.glPushMatrix();
			GL11.glTranslatef((float)x, (float)y, (float)z);
			if (direction) {
				GL11.glRotatef(90,0,1,0);
				GL11.glTranslatef(-2,0,0);
			}
			if (orientation) {
				GL11.glRotatef(-90,0,0,1);
				GL11.glTranslatef(-2,0,0);
			}
			
			if (openFraction>=.9f) {
				openFraction=.89f;
			} else {
				//Top
				bindDoorTexture(typeTop);
				GL11.glBegin(GL11.GL_QUADS);
					//Top Front
					GL11.glTexCoord2f(offsetForIndex+.25f,.5f*openFraction);	
					GL11.glVertex3f(0,2,.375f);
					GL11.glTexCoord2f(offsetForIndex,.5f*openFraction);
					GL11.glVertex3f(2,2,.375f);
					GL11.glTexCoord2f(offsetForIndex,.5f);
					GL11.glVertex3f(2,1+openFraction,.375f);
					GL11.glTexCoord2f(offsetForIndex+.25f,.5f);
					GL11.glVertex3f(0,1+openFraction,.375f);
					
					if (orientation) {//Make symmetrical with front if sideways
						GL11.glTexCoord2f(offsetForIndex,.5f*openFraction);
						GL11.glVertex3f(2,2,.625f);
						GL11.glTexCoord2f(offsetForIndex+.25f,.5f*openFraction);	
						GL11.glVertex3f(0,2,.625f);
						GL11.glTexCoord2f(offsetForIndex+.25f,.5f);
						GL11.glVertex3f(0,1+openFraction,.625f);
						GL11.glTexCoord2f(offsetForIndex,.5f);
						GL11.glVertex3f(2,1+openFraction,.625f);
					} else {
						GL11.glTexCoord2f(offsetForIndex+.25f,.5f*openFraction);
						GL11.glVertex3f(2,2,.625f);
						GL11.glTexCoord2f(offsetForIndex,.5f*openFraction);	
						GL11.glVertex3f(0,2,.625f);
						GL11.glTexCoord2f(offsetForIndex,.5f);
						GL11.glVertex3f(0,1+openFraction,.625f);
						GL11.glTexCoord2f(offsetForIndex+.25f,.5f);
						GL11.glVertex3f(2,1+openFraction,.625f);
					}
				GL11.glEnd();
				//Bottom
				bindDoorTexture(typeBottom);
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(offsetForIndex,1-.5f*openFraction);
					GL11.glVertex3f(2,0,.375f);
					GL11.glTexCoord2f(offsetForIndex+.25f,1-.5f*openFraction);
					GL11.glVertex3f(0,0,.375f);
					GL11.glTexCoord2f(offsetForIndex+.25f,.5f);
					GL11.glVertex3f(0,1-openFraction,.375f);
					GL11.glTexCoord2f(offsetForIndex,.5f);
					GL11.glVertex3f(2,1-openFraction,.375f);
					
					if (orientation) {//Make symmetrical with front if sideways
						GL11.glTexCoord2f(offsetForIndex+.25f,1-.5f*openFraction);
						GL11.glVertex3f(0,0,.625f);
						GL11.glTexCoord2f(offsetForIndex,1-.5f*openFraction);
						GL11.glVertex3f(2,0,.625f);
						GL11.glTexCoord2f(offsetForIndex,.5f);
						GL11.glVertex3f(2,1-openFraction,.625f);
						GL11.glTexCoord2f(offsetForIndex+.25f,.5f);
						GL11.glVertex3f(0,1-openFraction,.625f);
					} else {
						GL11.glTexCoord2f(offsetForIndex,1-.5f*openFraction);
						GL11.glVertex3f(0,0,.625f);
						GL11.glTexCoord2f(offsetForIndex+.25f,1-.5f*openFraction);
						GL11.glVertex3f(2,0,.625f);
						GL11.glTexCoord2f(offsetForIndex+.25f,.5f);
						GL11.glVertex3f(2,1-openFraction,.625f);
						GL11.glTexCoord2f(offsetForIndex,.5f);
						GL11.glVertex3f(0,1-openFraction,.625f);
					}
				GL11.glEnd();
				bindTexture(textureCommon);
				GL11.glBegin(GL11.GL_QUADS);
					float offsetForPanel=0;
					if (orientation) {
						offsetForPanel=.5f;
					}
					//Welds (Closed)
					if (welded) {
						GL11.glTexCoord2f(.25f,1);
						GL11.glVertex3f(2,0,.37f);
						GL11.glTexCoord2f(0,1);
						GL11.glVertex3f(0,0,.37f);
						GL11.glTexCoord2f(0,0);
						GL11.glVertex3f(0,2,.37f);
						GL11.glTexCoord2f(.25f,0);
						GL11.glVertex3f(2,2,.37f);
						
						GL11.glTexCoord2f(0,1);
						GL11.glVertex3f(0,0,.63f);
						GL11.glTexCoord2f(.25f,1);
						GL11.glVertex3f(2,0,.63f);
						GL11.glTexCoord2f(.25f,0);
						GL11.glVertex3f(2,2,.63f);
						GL11.glTexCoord2f(0,0);
						GL11.glVertex3f(0,2,.63f);
					}
				GL11.glEnd();
			}
			if (openFraction!=0) {
				float offsetForWeld=0;
				if (welded) {
					offsetForWeld=.5f;
				}
				bindTexture(textureCommon);
				GL11.glBegin(GL11.GL_QUADS);
					//Top Edge
					GL11.glTexCoord2f(.5f,offsetForWeld);
					GL11.glVertex3f(0,1+openFraction,0);
					GL11.glTexCoord2f(.75f,offsetForWeld);
					GL11.glVertex3f(2,1+openFraction,0);
					GL11.glTexCoord2f(.75f,offsetForWeld+.5f);
					GL11.glVertex3f(2,1+openFraction,1);
					GL11.glTexCoord2f(.5f,offsetForWeld+.5f);
					GL11.glVertex3f(0,1+openFraction,1);

					//Bottom Edge
					GL11.glTexCoord2f(.75f,offsetForWeld);
					GL11.glVertex3f(2,1-openFraction,0);
					GL11.glTexCoord2f(.5f,offsetForWeld);
					GL11.glVertex3f(0,1-openFraction,0);
					GL11.glTexCoord2f(.5f,offsetForWeld+.5f);
					GL11.glVertex3f(0,1-openFraction,1);
					GL11.glTexCoord2f(.75f,offsetForWeld+.5f);
					GL11.glVertex3f(2,1-openFraction,1);
				GL11.glEnd();
			}
			
		GL11.glPopMatrix();
	}
}
