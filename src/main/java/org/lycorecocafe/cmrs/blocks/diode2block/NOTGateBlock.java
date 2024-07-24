package org.lycorecocafe.cmrs.blocks.diode2block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.lycorecocafe.cmrs.blockentity.NOTGateBlockEntity;

public class NOTGateBlock extends HorizontalDirectionalBlock implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final BooleanProperty INPUT = BooleanProperty.create("input");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public VoxelShape getShape(BlockState p_52556_, BlockGetter p_52557_, BlockPos p_52558_, CollisionContext p_52559_) {
        return SHAPE;
    }

    public boolean canSurvive(BlockState p_52538_, LevelReader p_52539_, BlockPos p_52540_) {
        return canSupportRigidBlock(p_52539_, p_52540_.below());
    }

    public NOTGateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(INPUT, false)
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INPUT, FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }


    @Override
    public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NOTGateBlockEntity) {
                ((NOTGateBlockEntity) blockEntity).updateState(world, pos);
                emitRedstoneSignal(world, pos, state);
            }
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide) {
            boolean input = world.hasSignal(pos.relative(state.getValue(FACING)), state.getValue(FACING));
            BlockState newState = state.setValue(INPUT, input);

            if (newState != state) {
                world.setBlock(pos, newState, 3);
                emitRedstoneSignal(world, pos, newState);
            }
        }
    }


    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public void emitRedstoneSignal(Level world, BlockPos pos, BlockState state) {
        boolean input = state.getValue(INPUT);
        boolean output = !input;

        BlockState newState = state.setValue(POWERED, output);

        if (newState != state) {
            world.setBlock(pos, newState, 3);
            world.updateNeighborsAt(pos, this);

            Direction facing = state.getValue(FACING);
            BlockPos outputPos = pos.relative(facing);
            world.updateNeighborsAt(outputPos, this);
        }
    }

    protected void updateNeighborsInFront(Level level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(level, pos, level.getBlockState(pos), java.util.EnumSet.of(direction.getOpposite()), false).isCanceled())
            return;
        level.neighborChanged(blockpos, this, pos);
        level.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
    }



    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NOTGateBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return world.isClientSide ? null : (world1, pos, state1, entity) -> {
            if (entity instanceof NOTGateBlockEntity) {
                ((NOTGateBlockEntity) entity).updateState(world1, pos);
            }
        };
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, @NotNull Level world, @NotNull BlockPos pos) {
        return (!state.getValue(INPUT)) ? 15 : 0;
    }

    @Override
    public int getSignal(BlockState state, @NotNull BlockGetter p_52521_, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (!state.getValue(POWERED)) {
            return 0;
        } else {
            return state.getValue(FACING) == direction ? 15 : 0;
        }
    }


}