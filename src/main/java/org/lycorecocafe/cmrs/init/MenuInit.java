package org.lycorecocafe.cmrs.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.client.gui.menu.MusicBoxMenu;
import org.lycorecocafe.cmrs.client.gui.menu.SignalEmitterMenu;
import org.lycorecocafe.cmrs.client.gui.menu.SignalReceiverMenu;

public class MenuInit {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CMRS.MODID);

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    public static final RegistryObject<MenuType<SignalEmitterMenu>> SIGNAL_EMITTER_MENU = MENUS.register("signal_emitter_menu",
            () -> IForgeMenuType.create(SignalEmitterMenu::new));

    public static final RegistryObject<MenuType<SignalReceiverMenu>> SIGNAL_RECEIVER_MENU = MENUS.register("signal_receiver_menu",
            () -> IForgeMenuType.create(SignalReceiverMenu::new));

    public static final RegistryObject<MenuType<MusicBoxMenu>> MUSIC_BOX_MENU = MENUS.register("music_box_menu",
            () -> IForgeMenuType.create(MusicBoxMenu::new));


}
