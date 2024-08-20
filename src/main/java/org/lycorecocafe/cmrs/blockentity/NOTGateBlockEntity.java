package org.lycorecocafe.cmrs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.lycorecocafe.cmrs.blocks.diode2block.NOTGateBlock;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;

public class NOTGateBlockEntity extends BlockEntity {

    public NOTGateBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.NOT_GATE.get(), pos, state);
    }

    public void updateState(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Direction facing = state.getValue(NOTGateBlock.FACING);

        boolean input = hasSignalFromSide(world, pos, facing);

        BlockState newState = state.setValue(NOTGateBlock.INPUT, input);

        if (newState != state) {
            world.setBlock(pos, newState, 3);
        }

        world.updateNeighborsAt(pos, this.getBlockState().getBlock());


    }

    private boolean hasSignalFromSide(Level world, BlockPos pos, Direction direction) {
        BlockPos sidePos = pos.relative(direction);
        return world.getSignal(sidePos, direction) > 0;
    }
}