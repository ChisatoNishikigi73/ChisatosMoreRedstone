package org.lycorecocafe.cmrs.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;

public class HoloDisplayTerminalBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);

    public HoloDisplayTerminalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof HoloDisplayTerminalBlockEntity) {
                MenuProvider containerProvider = (HoloDisplayTerminalBlockEntity) blockEntity;
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
        return new HoloDisplayTerminalBlockEntity(pos, state);
    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
//        // 检查类型是否匹配，只有在匹配时才返回ticker
//        if (type == BlockEntitiesInit.HOLO_DISPLAY_TERMINAL_BE.get()) {
//            return (level1, pos, state1, blockEntity) -> {
//                if (blockEntity instanceof HoloDisplayTerminalBlockEntity) {
//                    HoloDisplayTerminalBlockEntity.tick(level1, pos, state1, (HoloDisplayTerminalBlockEntity) blockEntity);
//                }
//            };
//        }
//        return null; // 类型不匹配时返回null
//    }
}
