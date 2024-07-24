package org.lycorecocafe.cmrs.blocks.triode3block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.lycorecocafe.cmrs.blocks.Triode3BlockBase;

public class XORGateBlock extends Triode3BlockBase {
    public static final BooleanProperty LEFT_INPUT = BooleanProperty.create("left_input");
    public static final BooleanProperty RIGHT_INPUT = BooleanProperty.create("right_input");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public XORGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEFT_INPUT, false)
                .setValue(RIGHT_INPUT, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        checkTickOnNeighbor(world, pos, state);
    }

    @Override
    public BooleanProperty getLeftInputProperty() {
        return LEFT_INPUT;
    }

    @Override
    public BooleanProperty getRightInputProperty() {
        return RIGHT_INPUT;
    }

    @Override
    public BooleanProperty getPoweredProperty() {
        return POWERED;
    }

    @Override
    public boolean outputComputation(boolean leftInput, boolean rightInput) {
        return leftInput ^ rightInput;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEFT_INPUT, RIGHT_INPUT, FACING, POWERED);
    }
}