package mods.jamstone.aurus.client;

import mods.jamstone.aurus.common.CommonProxy;
import mods.jamstone.aurus.common.EntityAuru;
import mods.jamstone.aurus.common.EntityEye;
import mods.jamstone.aurus.common.EntityFieryProjectileSmall;
import mods.jamstone.aurus.common.EntityIcyProjectileSmall;
import mods.jamstone.aurus.common.EntityProjectile;
import mods.jamstone.aurus.common.EntitySparkyProjectileSmall;
import mods.jamstone.aurus.common.EntityToxicProjectileSmall;
import mods.jamstone.aurus.common.ModelAuru;
import mods.jamstone.aurus.common.RenderAuru;
import mods.jamstone.aurus.common.RenderEye;
import mods.jamstone.aurus.common.RenderProjectile;
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
