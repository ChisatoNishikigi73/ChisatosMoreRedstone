package org.lycorecocafe.cmrs;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.init.BlocksInit;
import org.lycorecocafe.cmrs.init.MenuInit;
import org.lycorecocafe.cmrs.network.ApplySignalPaket;
import org.lycorecocafe.cmrs.network.ClearSignalPaket;
import org.lycorecocafe.cmrs.network.SignalEmitterPacket;
import org.lycorecocafe.cmrs.network.SignalReceiverPacket;
import org.slf4j.Logger;

import static org.apache.http.params.CoreProtocolPNames.PROTOCOL_VERSION;

@Mod(CMRS.MODID)
public class CMRS
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cmrs";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public CMRS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlocksInit.register(modEventBus);
        BlockEntitiesInit.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MenuInit.MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        CHANNEL.registerMessage(0, SignalEmitterPacket.class, SignalEmitterPacket::toBytes, SignalEmitterPacket::new, SignalEmitterPacket::handle);
        CHANNEL.registerMessage(1, SignalReceiverPacket.class, SignalReceiverPacket::toBytes, SignalReceiverPacket::new, SignalReceiverPacket::handle);
        CHANNEL.registerMessage(2, ApplySignalPaket.class, ApplySignalPaket::toBytes, ApplySignalPaket::new, ApplySignalPaket::handle);
        CHANNEL.registerMessage(3, ClearSignalPaket.class, ClearSignalPaket::toBytes, ClearSignalPaket::new, ClearSignalPaket::handle);

        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //CMRSClient.setup(event);
        }
    }
}
