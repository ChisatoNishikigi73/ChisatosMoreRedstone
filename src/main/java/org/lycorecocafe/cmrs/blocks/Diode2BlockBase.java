package org.lycorecocafe.cmrs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.NotNull;

public abstract class Diode2BlockBase extends DiodeBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected Diode2BlockBase(BlockBehaviour.Properties behaviorProperties) {
        super(behaviorProperties);
    }

    public VoxelShape getShape(@NotNull BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return SHAPE;
    }

    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        return canSupportRigidBlock(levelReader, pos.below());
    }

    public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource randomSource) {
        if (!this.isLocked(serverLevel, pos, state)) {
            boolean flag = state.getValue(POWERED);
            boolean flag1 = this.shouldTurnOn(serverLevel, pos, state);
            if (flag && !flag1) {
                serverLevel.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(false)), 2);
            } else if (!flag) {
                serverLevel.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(true)), 2);
                if (!flag1) {
                    serverLevel.scheduleTick(pos, this, this.getDelay(state), TickPriority.VERY_HIGH);
                }
            }

        }
    }

    public int getDirectSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return state.getSignal(blockGetter, pos, direction);
    }

    public int getSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        if (!state.getValue(POWERED)) {
            return 0;
        } else {
            return state.getValue(FACING) == direction ? this.getOutputSignal(blockGetter, pos, state) : 0;
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block p_52528_, BlockPos p_52529_, boolean p_52530_) {
        if (state.canSurvive(level, pos)) {
            this.checkTickOnNeighbor(level, pos, state);
        } else {
            BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            dropResources(state, level, pos, blockentity);
            level.removeBlock(pos, false);

            for(Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
            }

        }
    }

    protected void checkTickOnNeighbor(Level level, BlockPos pos, BlockState state) {
        if (!this.isLocked(level, pos, state)) {
            boolean flag = state.getValue(POWERED);
            boolean flag1 = this.shouldTurnOn(level, pos, state);
            if (flag != flag1 && !level.getBlockTicks().willTickThisTick(pos, this)) {
                TickPriority tickpriority = TickPriority.HIGH;
                if (this.shouldPrioritize(level, pos, state)) {
                    tickpriority = TickPriority.EXTREMELY_HIGH;
                } else if (flag) {
                    tickpriority = TickPriority.VERY_HIGH;
                }

                level.scheduleTick(pos, this, this.getDelay(state), tickpriority);
            }

        }
    }

    public boolean isLocked(LevelReader levelReader, BlockPos pos, BlockState state) {
        return false;
    }

    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return this.getInputSignal(level, pos, state) > 0;
    }

    protected int getInputSignal(Level level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction);
        int i = level.getSignal(blockpos, direction);
        if (i >= 15) {
            return i;
        } else {
            BlockState blockstate = level.getBlockState(blockpos);
            return Math.max(i, blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedStoneWireBlock.POWER) : 0);
        }
    }

    protected int getAlternateSignal(LevelReader levelReader, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        Direction direction1 = direction.getClockWise();
        Direction direction2 = direction.getCounterClockWise();
        return Math.max(this.getAlternateSignalAt(levelReader, pos.relative(direction1), direction1), this.getAlternateSignalAt(levelReader, pos.relative(direction2), direction2));
    }

    protected int getAlternateSignalAt(LevelReader levelReader, BlockPos pos, Direction direction) {
        BlockState blockstate = levelReader.getBlockState(pos);
        if (this.isAlternateInput(blockstate)) {
            if (blockstate.is(Blocks.REDSTONE_BLOCK)) {
                return 15;
            } else {
                return blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedStoneWireBlock.POWER) : levelReader.getDirectSignal(pos, direction);
            }
        } else {
            return 0;
        }
    }

    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_52501_) {
        return this.defaultBlockState().setValue(FACING, p_52501_.getHorizontalDirection().getOpposite());
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack itemStack) {
        if (this.shouldTurnOn(level, pos, state)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState p_52569_, boolean p_52570_) {
        this.updateNeighborsInFront(level, pos, state);
    }

    public void onRemove(BlockState newState, Level level, BlockPos p_52534_, BlockState oldState, boolean p_52536_) {
        if (!p_52536_ && !newState.is(oldState.getBlock())) {
            super.onRemove(newState, level, p_52534_, oldState, p_52536_);
            this.updateNeighborsInFront(level, p_52534_, newState);
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

    protected boolean isAlternateInput(BlockState state) {
        return state.isSignalSource();
    }

    protected int getOutputSignal(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        return 15;
    }

    public static boolean isDiode(BlockState state) {
        return state.getBlock() instanceof Diode2BlockBase;
    }

    public boolean shouldPrioritize(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING).getOpposite();
        BlockState blockstate = blockGetter.getBlockState(pos.relative(direction));
        return isDiode(blockstate) && blockstate.getValue(FACING) != direction;
    }

    protected abstract int getDelay(BlockState state);
}
