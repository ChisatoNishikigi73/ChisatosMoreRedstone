package org.lycorecocafe.cmrs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.lycorecocafe.cmrs.blocks.triode3block.NORGateBlock;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;

public class NORGateBlockEntity extends BlockEntity {

    public NORGateBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.OR_GATE.get(), pos, state);
    }

    public void updateState(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Direction facing = state.getValue(NORGateBlock.FACING);

        boolean leftInput = hasSignalFromSide(world, pos, facing.getClockWise());
        boolean rightInput = hasSignalFromSide(world, pos, facing.getCounterClockWise());

        BlockState newState = state.setValue(NORGateBlock.LEFT_INPUT, leftInput).setValue(NORGateBlock.RIGHT_INPUT, rightInput);

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