package mods.jamstone.aurus.common;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityAuruCloud extends EntityAuru {

    public EntityAuruCloud(World par1World) {
	super(par1World);
	this.texture = "/mods/jamstone_aurus/textures/models/AuruCloud.png";
	this.plan = EntityAuru.Mood.TRICKY.getPlan();
    }

    @Override
    protected void launchAttack() {
	this.worldObj.playSoundAtEntity(this, "mob.fledgeling.shoot",
		this.getSoundVolume(),
		1.0f);

	Entity entity = new EntitySparkyProjectileSmall(this,
		this.lostTargetedEntity);

	this.worldObj.spawnEntityInWorld(entity);

    }

}
