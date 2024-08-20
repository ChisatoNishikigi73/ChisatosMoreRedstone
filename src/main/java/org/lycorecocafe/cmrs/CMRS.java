package org.lycorecocafe.cmrs;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lycorecocafe.cmrs.client.gui.screen.MusicBoxScreen;
import org.lycorecocafe.cmrs.client.gui.screen.SignalEmitterScreen;
import org.lycorecocafe.cmrs.client.gui.screen.SignalReceiverScreen;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.init.BlocksInit;
import org.lycorecocafe.cmrs.init.ItemsInit;
import org.lycorecocafe.cmrs.init.MenuInit;
import org.lycorecocafe.cmrs.network.*;
import org.slf4j.Logger;

@Mod(CMRS.MODID)
public class CMRS {
    public static final String MODID = "cmrs";
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static final Logger LOGGER = LogUtils.getLogger();

    public CMRS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BlocksInit.register(modEventBus);
        BlockEntitiesInit.register(modEventBus);
        ItemsInit.register(modEventBus);
        MenuInit.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        int index = 0;
        CHANNEL.registerMessage(index++, SignalEmitterPacket.class, SignalEmitterPacket::toBytes, SignalEmitterPacket::new, SignalEmitterPacket::handle);
        CHANNEL.registerMessage(index++, SignalReceiverPacket.class, SignalReceiverPacket::toBytes, SignalReceiverPacket::new, SignalReceiverPacket::handle);
        CHANNEL.registerMessage(index++, ApplySignalPaket.class, ApplySignalPaket::toBytes, ApplySignalPaket::new, ApplySignalPaket::handle);
        CHANNEL.registerMessage(index++, ClearSignalPaket.class, ClearSignalPaket::toBytes, ClearSignalPaket::new, ClearSignalPaket::handle);
        CHANNEL.registerMessage(index++, MusicPlayerPacket.class, MusicPlayerPacket::toBytes, MusicPlayerPacket::new, MusicPlayerPacket::handle);
        CHANNEL.registerMessage(index++, MusicPlayerPlayPacket.class, MusicPlayerPlayPacket::toBytes, MusicPlayerPlayPacket::new, MusicPlayerPlayPacket::handle);
        CHANNEL.registerMessage(index++, MusicPlayerDownloadMusicNotify.class, MusicPlayerDownloadMusicNotify::toBytes, MusicPlayerDownloadMusicNotify::new, MusicPlayerDownloadMusicNotify::handle);
        CHANNEL.registerMessage(index++, MusicPlayerStatusChangedPacket.class, MusicPlayerStatusChangedPacket::toBytes, MusicPlayerStatusChangedPacket::new, MusicPlayerStatusChangedPacket::handle);
        CHANNEL.registerMessage(index++, MusicPlayerPlayNotify.class, MusicPlayerPlayNotify::toBytes, MusicPlayerPlayNotify::new, MusicPlayerPlayNotify::handle);

        MenuScreens.register(MenuInit.SIGNAL_EMITTER_MENU.get(), SignalEmitterScreen::new);
        MenuScreens.register(MenuInit.SIGNAL_RECEIVER_MENU.get(), SignalReceiverScreen::new);
        MenuScreens.register(MenuInit.MUSIC_BOX_MENU.get(), MusicBoxScreen::new);
        LOGGER.info("HELLO FROM CHISATO~");

//        if (Config.logDirtBlock)
//            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        //Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
