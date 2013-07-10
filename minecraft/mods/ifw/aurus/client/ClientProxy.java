package mods.ifw.aurus.client;

import mods.ifw.aurus.common.CommonProxy;
import mods.ifw.aurus.common.EntityAuru;
import mods.ifw.aurus.common.EntityEye;
import mods.ifw.aurus.common.EntityFieryProjectileSmall;
import mods.ifw.aurus.common.EntityIcyProjectileSmall;
import mods.ifw.aurus.common.EntityProjectile;
import mods.ifw.aurus.common.EntitySparkyProjectileSmall;
import mods.ifw.aurus.common.EntityToxicProjectileSmall;
import mods.ifw.aurus.common.ModelAuru;
import mods.ifw.aurus.common.RenderAuru;
import mods.ifw.aurus.common.RenderEye;
import mods.ifw.aurus.common.RenderProjectile;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	public ClientProxy() {
		super();
	}

	@Override
	public void registerRenderers() {
		// MinecraftForgeClient.preloadTexture("/textures/nest.png");

		RenderingRegistry.registerEntityRenderingHandler(EntityEye.class,
				new RenderEye());
		// // RenderingRegistry.registerEntityRenderingHandler(
		// // EntityFrostGhast.class, new RenderGhast());
		RenderingRegistry.registerEntityRenderingHandler(EntityAuru.class,
				new RenderAuru(new ModelAuru(1.0f, 0, 0), 1.0f));

		RenderingRegistry.registerEntityRenderingHandler(
				EntityProjectile.class, new RenderProjectile(0));

		RenderingRegistry.registerEntityRenderingHandler(
				EntityIcyProjectileSmall.class, new RenderProjectile(1));

		RenderingRegistry.registerEntityRenderingHandler(
				EntityFieryProjectileSmall.class, new RenderProjectile(2));

		RenderingRegistry.registerEntityRenderingHandler(
				EntityToxicProjectileSmall.class, new RenderProjectile(3));

		RenderingRegistry.registerEntityRenderingHandler(
				EntitySparkyProjectileSmall.class, new RenderProjectile(4));

	}
}
