package org.lycorecocafe.cmrs.events;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.client.gui.screen.MusicBoxScreen;
import org.lycorecocafe.cmrs.client.gui.screen.SignalEmitterScreen;
import org.lycorecocafe.cmrs.client.gui.screen.SignalReceiverScreen;
import org.lycorecocafe.cmrs.init.MenuInit;

@Mod.EventBusSubscriber(modid = CMRS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.SIGNAL_EMITTER_MENU.get(), SignalEmitterScreen::new);
        MenuScreens.register(MenuInit.SIGNAL_RECEIVER_MENU.get(), SignalReceiverScreen::new);
        MenuScreens.register(MenuInit.MUSIC_BOX_MENU.get(), MusicBoxScreen::new);
    }
}