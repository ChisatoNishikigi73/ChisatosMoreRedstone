package org.lycorecocafe.cmrs.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blocks.*;
import org.lycorecocafe.cmrs.blocks.diode2block.NOTGateBlock;
import org.lycorecocafe.cmrs.blocks.tetrode4block.CrossGateBlock;
import org.lycorecocafe.cmrs.blocks.triode3block.*;

public class BlocksInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CMRS.MODID);

    public static final RegistryObject<Block> AND_GATE = BLOCKS.register("and_gate", () -> new ANDGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> OR_GATE = BLOCKS.register("or_gate", () -> new ORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> NOT_GATE = BLOCKS.register("not_gate", () -> new NOTGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> XOR_GATE = BLOCKS.register("xor_gate", () -> new XORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> XNOR_GATE = BLOCKS.register("xnor_gate", () -> new XNORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> NAND_GATE = BLOCKS.register("nand_gate", () -> new NANDGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> NOR_GATE = BLOCKS.register("nor_gate", () -> new NORGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> CROSS_GATE = BLOCKS.register("cross_gate", () -> new CrossGateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> AHOPPER = BLOCKS.register("ahopper", () -> new AHopperBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> SIGNAL_EMITTER = BLOCKS.register("signal_emitter", () -> new SignalEmitterBlock(Block.Properties.of(Material.METAL).strength(3.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SIGNAL_RECEIVER = BLOCKS.register("signal_receiver", () -> new SignalReceiverBlock(Block.Properties.of(Material.METAL).strength(3.0f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> MUSIC_BOX = BLOCKS.register("music_box", () -> new MusicBoxBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F)));
    public static final RegistryObject<Block> HOLO_DISPLAY_TERMINAL = BLOCKS.register("holo_display_terminal", () -> new HoloDisplayTerminalBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.0F).lightLevel((state) -> 10)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
