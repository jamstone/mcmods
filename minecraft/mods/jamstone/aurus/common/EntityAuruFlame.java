package mods.jamstone.aurus.common;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityAuruFlame extends EntityAuru {

    public EntityAuruFlame(World par1World) {
	super(par1World);
	this.texture = "/mods/jamstone_aurus/textures/models/AuruFlame.png";
	this.plan = EntityAuru.Mood.VICIOUS.getPlan();
    }

    @Override
    protected void launchAttack() {
	this.worldObj.playSoundAtEntity(this, "mob.fledgeling.shoot",
		this.getSoundVolume(),
		1.0f);

	Entity entity = new EntityFieryProjectileSmall(this,
		this.lostTargetedEntity);

	this.worldObj.spawnEntityInWorld(entity);

    }

}
