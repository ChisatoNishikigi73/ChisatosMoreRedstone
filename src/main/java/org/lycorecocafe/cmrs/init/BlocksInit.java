package org.lycorecocafe.cmrs.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blocks.diode2block.NOTGateBlock;
import org.lycorecocafe.cmrs.blocks.triode3block.*;

public class BlocksInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CMRS.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CMRS.MODID);

    public static final RegistryObject<Block> AND_GATE = BLOCKS.register("and_gate", () -> new ANDGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> OR_GATE = BLOCKS.register("or_gate", () -> new ORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> NOT_GATE = BLOCKS.register("not_gate", () -> new NOTGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> XOR_GATE = BLOCKS.register("xor_gate", () -> new XORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> XNOR_GATE = BLOCKS.register("xnor_gate", () -> new XNORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> NAND_GATE = BLOCKS.register("nand_gate", () -> new NANDGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> NOR_GATE = BLOCKS.register("nor_gate", () -> new NORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));


    public static final RegistryObject<Item> AND_GATE_ITEM = ITEMS.register("and_gate", () -> new BlockItem(AND_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> OR_GATE_ITEM = ITEMS.register("or_gate", () -> new BlockItem(OR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> NOT_GATE_ITEM = ITEMS.register("not_gate", () -> new BlockItem(NOT_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> XOR_GATE_ITEM = ITEMS.register("xor_gate", () -> new BlockItem(XOR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> XNOR_GATE_ITEM = ITEMS.register("xnor_gate", () -> new BlockItem(XNOR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> NAND_GATE_ITEM = ITEMS.register("nand_gate", () -> new BlockItem(NAND_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));
    public static final RegistryObject<Item> NOR_GATE_ITEM = ITEMS.register("nor_gate", () -> new BlockItem(NOR_GATE.get(), new Item.Properties().tab(CreativeModeTabInit.CMRS_TAB)));


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
