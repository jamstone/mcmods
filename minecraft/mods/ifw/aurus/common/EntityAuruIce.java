package mods.ifw.aurus.common;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityAuruIce extends EntityAuru {

    public EntityAuruIce(World par1World) {
	super(par1World);
	this.texture = "/mods/ifw_aurus/textures/models/AuruIce.png";
	this.plan = EntityAuru.Mood.COLD.getPlan();
    }

    @Override
    protected void launchAttack() {
	this.worldObj.playSoundAtEntity(this, "mob.fledgeling.shoot",
		this.getSoundVolume(), 1.0f);

	Entity entity = new EntityIcyProjectileSmall(this,
		this.lostTargetedEntity);

	this.worldObj.spawnEntityInWorld(entity);

    }

}
