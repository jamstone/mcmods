package mods.ifw.aurus.common;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityAuruBog extends EntityAuru {

    public EntityAuruBog(World par1World) {
	super(par1World);
	this.texture = "/mods/ifw_aurus/textures/models/AuruBog.png";
	this.plan = EntityAuru.Mood.GUERILLA.getPlan();
    }

    @Override
    protected void launchAttack() {
	this.worldObj.playSoundAtEntity(this, "mob.fledgeling.shoot",
		this.getSoundVolume(),
		1.0f);

	Entity entity = new EntityToxicProjectileSmall(this,
		this.lostTargetedEntity);

	this.worldObj.spawnEntityInWorld(entity);

    }

}
