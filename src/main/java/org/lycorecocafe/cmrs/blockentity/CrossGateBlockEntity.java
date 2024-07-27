package org.lycorecocafe.cmrs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.lycorecocafe.cmrs.blocks.tetrode4block.CrossGateBlock;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.init.BlocksInit;

import java.util.HashSet;
import java.util.Set;

import static org.lycorecocafe.cmrs.mixin.RedstoneWireBlockMixin.hasRedstoneSignalSourceWithDirection;

public class CrossGateBlockEntity extends BlockEntity implements BlockEntityTicker<CrossGateBlockEntity> {
    private static final Set<Block> BLACKLIST = new HashSet<>();  // 定义黑名单

    static {
        BLACKLIST.add(BlocksInit.CROSS_GATE.get());
    }

    public CrossGateBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.CROSS_GATE.get(), pos, state);
    }

    //TODO:More detailed detection, such as through various gates, CROSS-GATE with input on the other end, etc
    public void updatePower() {
        if (level != null) {
            BlockState state = getBlockState();
            boolean northPowered = hasRedstoneSignalSourceWithDirection(level, getBlockPos(), Direction.NORTH, BLACKLIST);
            boolean southPowered = hasRedstoneSignalSourceWithDirection(level, getBlockPos(), Direction.SOUTH, BLACKLIST);
            boolean westPowered = hasRedstoneSignalSourceWithDirection(level, getBlockPos(), Direction.WEST, BLACKLIST);
            boolean eastPowered = hasRedstoneSignalSourceWithDirection(level, getBlockPos(), Direction.EAST, BLACKLIST);
            level.setBlock(getBlockPos(), state
                    .setValue(CrossGateBlock.NORTH_POWERED, northPowered)
                    .setValue(CrossGateBlock.SOUTH_POWERED, southPowered)
                    .setValue(CrossGateBlock.WEST_POWERED, westPowered)
                    .setValue(CrossGateBlock.EAST_POWERED, eastPowered), 3);
        }
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, CrossGateBlockEntity be) {
        updatePower();
    }
}