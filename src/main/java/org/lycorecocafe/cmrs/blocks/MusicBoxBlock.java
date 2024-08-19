package org.lycorecocafe.cmrs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;

public class MusicBoxBlock extends HorizontalDirectionalBlock implements EntityBlock {
    protected final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public MusicBoxBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MusicBoxBlockEntity) {
                MenuProvider containerProvider = (MusicBoxBlockEntity) blockEntity;
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MusicBoxBlockEntity(pos, state);
    }

//    @Override
//    public boolean isSignalSource(BlockState state) {
//        return true;
//    }

//    @Override
//    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
////        System.out.println("neighborChanged");
//        if (!world.isClientSide) {
////            System.out.println(world);
//            boolean isPowered = world.hasNeighborSignal(pos);
//            BlockEntity blockEntity = world.getBlockEntity(pos);
//            if (blockEntity instanceof MusicBoxBlockEntity) {
//                ((MusicBoxBlockEntity) blockEntity).onRedstoneSignalChanged(world, isPowered);
//            }
//        }
//    }
}
