package rocks.cogg.ms13;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;

import java.util.HashMap;

/*
 * This whole thing is broken. It is meant to provide a more realistic and ss13-esque damage model.
 * It currently makes everything immortal and tracks damage to entities.
 */

public class DamageManager {
	static private HashMap<EntityLivingBase,DamageInfo> worldMap =new HashMap<EntityLivingBase,DamageInfo>();
	
	static private DamageInfo getInfo(Entity e) {
		if (e instanceof EntityLivingBase) {
			if (worldMap.containsKey(e)) {
				return worldMap.get(e);
			} else {
				DamageInfo d = new DamageInfo((EntityLivingBase)e);
				worldMap.put((EntityLivingBase)e, d);
				return d;
			}
		} else {
			return null;
		}
	}
	
	static void applyDamage(DamageSource dmg,float amount) {
		DamageInfo info = getInfo(dmg.getEntity());
		if (info!=null) {
			info.applyDamage(dmg,amount);
		}
	}
}

class DamageInfo {
	EntityLivingBase entity;
	
	float dmgBurn=0;
	float dmgBrute=0;
	float dmgAsphyxia=0;
	boolean brokenLegs;
	
	public DamageInfo(EntityLivingBase e) {
		entity=e;
	}
	
	public void applyDamage(DamageSource dmg,float amount) {
		if (dmg==DamageSource.inFire||dmg==DamageSource.onFire||dmg==DamageSource.lava) {
			dmgBurn+=amount;
		} else if (dmg instanceof EntityDamageSource||dmg==DamageSource.anvil||dmg==DamageSource.cactus||dmg==DamageSource.fallingBlock||dmg==DamageSource.inWall) {
			dmgBrute+=amount;
		} else if (dmg==DamageSource.fall) {
			if (amount>6) {
				brokenLegs=true;
			}
			dmgBrute+=amount/2;
		} else if (dmg==DamageSource.drown) {
			dmgAsphyxia+=amount;
		}
		System.out.println("Something took "+amount+" damage. It's current damage info is:");
		show();
	}
	
	public void show() {
		System.out.println("	Burn: "+dmgBurn);
		System.out.println("	Brute: "+dmgBrute);
		System.out.println("	Asphyxia: "+dmgAsphyxia);
		System.out.println("	Bork Legs: "+brokenLegs);
	}
}