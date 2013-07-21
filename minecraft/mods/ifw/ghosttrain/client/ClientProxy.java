package mods.ifw.ghosttrain.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mods.ifw.ghosttrain.common.CommonProxy;
import mods.ifw.ghosttrain.common.EntityWilloWisp;
import mods.ifw.ghosttrain.common.ModelWilloWisp;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
    }

    @Override
    public void registerRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityWilloWisp.class,
                new RenderWilloWisp(new ModelWilloWisp(), 0.0f));
    }
}
