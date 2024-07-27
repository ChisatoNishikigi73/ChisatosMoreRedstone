package org.lycorecocafe.cmrs.blocks.tetrode4block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.blockentity.CrossGateBlockEntity;

public class CrossGateBlock extends Block implements EntityBlock {
    protected final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final BooleanProperty NORTH_POWERED = BooleanProperty.create("north_powered");
    public static final BooleanProperty SOUTH_POWERED = BooleanProperty.create("south_powered");
    public static final BooleanProperty WEST_POWERED = BooleanProperty.create("west_powered");
    public static final BooleanProperty EAST_POWERED = BooleanProperty.create("east_powered");

    public CrossGateBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH_POWERED, false)
                .setValue(SOUTH_POWERED, false)
                .setValue(WEST_POWERED, false)
                .setValue(EAST_POWERED, false));
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH_POWERED, SOUTH_POWERED, WEST_POWERED, EAST_POWERED);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrossGateBlockEntity) {
            ((CrossGateBlockEntity) blockEntity).updatePower();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrossGateBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, blockState, t) -> {
            if (t instanceof CrossGateBlockEntity) {
                ((CrossGateBlockEntity) t).tick(lvl, pos, blockState, (CrossGateBlockEntity) t);
            }
        };
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        switch (direction) {
            case NORTH:
                return state.getValue(NORTH_POWERED) ? 15 : 0;
            case SOUTH:
                return state.getValue(SOUTH_POWERED) ? 15 : 0;
            case WEST:
                return state.getValue(WEST_POWERED) ? 15 : 0;
            case EAST:
                return state.getValue(EAST_POWERED) ? 15 : 0;
            default:
                return 0;
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }
}
