package org.lycorecocafe.cmrs.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.items.EntityRecorderItem;
import org.lycorecocafe.cmrs.items.RedstoneDetectorItem;

public class ItemsInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CMRS.MODID);

    public static final RegistryObject<Item> AND_GATE_ITEM = ITEMS.register("and_gate", () -> new BlockItem(BlocksInit.AND_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> OR_GATE_ITEM = ITEMS.register("or_gate", () -> new BlockItem(BlocksInit.OR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> NOT_GATE_ITEM = ITEMS.register("not_gate", () -> new BlockItem(BlocksInit.NOT_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> XOR_GATE_ITEM = ITEMS.register("xor_gate", () -> new BlockItem(BlocksInit.XOR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> XNOR_GATE_ITEM = ITEMS.register("xnor_gate", () -> new BlockItem(BlocksInit.XNOR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> NAND_GATE_ITEM = ITEMS.register("nand_gate", () -> new BlockItem(BlocksInit.NAND_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> NOR_GATE_ITEM = ITEMS.register("nor_gate", () -> new BlockItem(BlocksInit.NOR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> CROSS_GATE_ITEM = ITEMS.register("cross_gate", () -> new BlockItem(BlocksInit.CROSS_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> REDSTONE_DETECTOR = ITEMS.register("redstone_detector", () -> new RedstoneDetectorItem(new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> SIGNAL_EMITTER_ITEM = ITEMS.register("signal_emitter", () -> new BlockItem(BlocksInit.SIGNAL_EMITTER.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> SIGNAL_RECEIVER_ITEM = ITEMS.register("signal_receiver", () -> new BlockItem(BlocksInit.SIGNAL_RECEIVER.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> MUSIC_BOX_ITEM = ITEMS.register("music_box", () -> new BlockItem(BlocksInit.MUSIC_BOX.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> HOLO_DISPLAY_TERMINAL_ITEM = ITEMS.register("holo_display_terminal", () -> new BlockItem(BlocksInit.HOLO_DISPLAY_TERMINAL.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> ENTITY_RECORDER = ITEMS.register("entity_recorder", () -> new EntityRecorderItem(new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
