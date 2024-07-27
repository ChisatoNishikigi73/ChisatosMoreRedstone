package org.lycorecocafe.cmrs.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.*;

public class BlockEntitiesInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CMRS.MODID);

    public static final RegistryObject<BlockEntityType<ANDGateBlockEntity>> AND_GATE = BLOCK_ENTITIES.register("and_gate", () -> BlockEntityType.Builder.of(ANDGateBlockEntity::new, BlocksInit.AND_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ORGateBlockEntity>> OR_GATE = BLOCK_ENTITIES.register("or_gate", () -> BlockEntityType.Builder.of(ORGateBlockEntity::new, BlocksInit.OR_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<NOTGateBlockEntity>> NOT_GATE = BLOCK_ENTITIES.register("not_gate", () -> BlockEntityType.Builder.of(NOTGateBlockEntity::new, BlocksInit.NOT_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<XORGateBlockEntity>> XOR_GATE = BLOCK_ENTITIES.register("xor_gate", () -> BlockEntityType.Builder.of(XORGateBlockEntity::new, BlocksInit.XOR_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<XNORGateBlockEntity>> XNOR_GATE = BLOCK_ENTITIES.register("xnor_gate", () -> BlockEntityType.Builder.of(XNORGateBlockEntity::new, BlocksInit.XOR_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<NANDGateBlockEntity>> NAND_GATE = BLOCK_ENTITIES.register("nand_gate", () -> BlockEntityType.Builder.of(NANDGateBlockEntity::new, BlocksInit.XOR_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<NORGateBlockEntity>> NOR_GATE = BLOCK_ENTITIES.register("nor_gate", () -> BlockEntityType.Builder.of(NORGateBlockEntity::new, BlocksInit.XOR_GATE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrossGateBlockEntity>> CROSS_GATE = BLOCK_ENTITIES.register("cross_gate", () -> BlockEntityType.Builder.of(CrossGateBlockEntity::new, BlocksInit.CROSS_GATE.get()).build(null));
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
