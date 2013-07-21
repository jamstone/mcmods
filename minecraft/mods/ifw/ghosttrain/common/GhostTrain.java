package mods.ifw.ghosttrain.common;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.Configuration;

import java.util.EnumSet;

@Mod(modid = GhostTrain.modid, name = "GhostTrain", version = "0.1a")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class GhostTrain implements ITickHandler {
    public static final String modid = "ifw_ghosttrain";
    public static GhostTrain instance;
    public static Configuration config;

    @SidedProxy(clientSide = "mods.ifw.ghosttrain.client.ClientProxy", serverSide = "mods.ifw.ghosttrain.common.CommonProxy")
    public static CommonProxy proxy;

    public static int startingBlockID = 500;
    public int lastBlockID = startingBlockID;

    public static Block blockStation;
    public int stationID;

    public static Block blockBrightSpace;
    public int brightID;


    public int addConfigBlock(Configuration config, String name) {
        return config.getBlock(name, ++lastBlockID).getInt();
    }

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {

        instance = this;
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        stationID = addConfigBlock(config, "Station");
        brightID = addConfigBlock(config, "Bright Space");

        config.save();
    }

    @Init
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();

        this.registerBlocks();

        this.registerItems();

        this.registerEntities();

        this.registerTileEntities();

        this.registerMobs();

        this.registerEtc();

    }

    private void registerItems() {

    }

    public void registerBlocks() {

        blockStation = new BlockStation(stationID).setUnlocalizedName(modid
                + "blockStation");
        LanguageRegistry.addName(blockStation, "Ghost Train Station");
        GameRegistry.registerBlock(blockStation, blockStation.getUnlocalizedName2());

        blockBrightSpace = new BlockBrightSpace(brightID).setUnlocalizedName(modid
                + "blockBrightSpace");
        LanguageRegistry.addName(blockBrightSpace, "Bright Space");
        GameRegistry.registerBlock(blockBrightSpace, blockBrightSpace.getUnlocalizedName2());

    }

    public void registerEntities() {
        LanguageRegistry.instance().addStringLocalization("entity.willoWisp.name",
                "en_US", "Willo Wisp");
        EntityRegistry.registerGlobalEntityID(EntityWilloWisp.class, "willoWisp",
                EntityRegistry.findGlobalUniqueEntityId(), 0x99FFFF, 0xCCCCFF);
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityStation.class, "tileEntityStation");
    }

    public void registerMobs() {
    }

    public void registerEtc() {
    }

    @PostInit
    public static void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel() {
        return null;
    }

}