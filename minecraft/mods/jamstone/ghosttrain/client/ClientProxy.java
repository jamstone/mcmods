package mods.jamstone.ghosttrain.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mods.jamstone.ghosttrain.common.CommonProxy;
import mods.jamstone.ghosttrain.common.EntityWilloWisp;
import mods.jamstone.ghosttrain.common.GhostTrain;
import mods.jamstone.ghosttrain.common.ModelWilloWisp;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityWilloWisp.class,
                new RenderWilloWisp(new ModelWilloWisp(), 0.0f));

        MinecraftForgeClient.registerItemRenderer(GhostTrain.magicBottle.itemID, new ItemRendererMagicBottle());
    }
}
