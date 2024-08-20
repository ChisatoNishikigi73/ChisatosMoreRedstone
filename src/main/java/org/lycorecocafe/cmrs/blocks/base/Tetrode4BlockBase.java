package org.lycorecocafe.cmrs.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Can't work lol
 **/
public abstract class Tetrode4BlockBase extends HorizontalDirectionalBlock {
    protected final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    @Deprecated
    public Tetrode4BlockBase(Properties properties) {
        super(properties);
    }

    public abstract IntegerProperty getStateProperty();

    public abstract boolean getLeftInput();

    public abstract boolean getRightInput();

    public abstract boolean getFormerInput();

    public abstract boolean getAfterInput();

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public boolean canSurvive(BlockState p_52538_, LevelReader p_52539_, BlockPos p_52540_) {
        return true;
        //return canSupportRigidBlock(p_52539_, p_52540_.below());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getStateProperty(), FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public abstract void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving);

    protected boolean getInputSignalFromDirectionBool(Level world, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return world.hasSignal(blockpos, direction);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter blockGetter, BlockPos pos) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.DIAMOND_PICKAXE ||
                player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.IRON_PICKAXE ||
                player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.STONE_PICKAXE) {
            return 0.17F;  // 调整为适当的速度值
        }
        return super.getDestroyProgress(state, player, blockGetter, pos);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        // 返回你希望掉落的物品
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (state.canSurvive(world, pos)) {
            this.checkTickOnNeighbor(world, pos, state);
        } else {
            BlockEntity blockentity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            dropResources(state, world, pos, blockentity);
            world.removeBlock(pos, false);

            for (Direction direction : Direction.values()) {
                world.updateNeighborsAt(pos.relative(direction), this);
            }

        }
    }

    protected abstract void checkTickOnNeighbor(Level world, BlockPos pos, BlockState state);

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public abstract int getAnalogOutputSignal(BlockState state, @NotNull Level world, @NotNull BlockPos pos);

    @Override
    public abstract int getSignal(BlockState state, @NotNull BlockGetter p_52521_, @NotNull BlockPos pos, @NotNull Direction direction);
}