package org.lycorecocafe.cmrs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

//@Mixin(RedStoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {

    private static final int MAX_RANGE = 16;

//    @Inject(method = "neighborChanged", at = @At("HEAD"))
//    private void onNeighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
//        Set<Block> blacklist = new HashSet<>();  // 在这里定义你的黑名单
//        SignalSource signalSource = findRedstoneSignalSource(world, pos, MAX_RANGE, blacklist);
//        if (signalSource != null && !signalSource.signalSources.isEmpty()) {
//            System.out.println("Redstone block at " + pos + " is powered by one of the signal sources: " + signalSource.signalSources);
//        }
//    }

    public static SignalSource findRedstoneSignalSource(Level world, BlockPos startPos, int maxRange, Set<Block> blacklist) {
        Set<BlockPos> visited = new HashSet<>();
        Stack<BlockPos> stack = new Stack<>();
        SignalSource signalSource = new SignalSource();

        stack.push(startPos);

        while (!stack.isEmpty()) {
            BlockPos pos = stack.pop();

            if (visited.contains(pos) || startPos.distManhattan(pos) > maxRange) {
                continue;
            }
            visited.add(pos);

            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof RedStoneWireBlock) {
                int power = state.getValue(RedStoneWireBlock.POWER);
                if (power > 0) {
                    // 继续遍历红石线的相邻方块
                    for (Direction direction : Direction.values()) {
                        BlockPos neighborPos = pos.relative(direction);
                        if (!visited.contains(neighborPos)) {
                            BlockState neighborState = world.getBlockState(neighborPos);
                            if (neighborState.getBlock() instanceof RedStoneWireBlock || neighborState.isSignalSource()) {
                                stack.push(neighborPos);
                            }
                        }
                    }
                }
            } else if (state.isSignalSource() && !(state.getBlock() instanceof RedStoneWireBlock) && !blacklist.contains(state.getBlock())) {
                signalSource.addSignalSource(pos, state);
            }
        }

        return signalSource;
    }

    public static SignalSource findRedstoneSignalSource(Level world, BlockPos startPos, int maxRange) {
        return findRedstoneSignalSource(world, startPos, maxRange, new HashSet<>());
    }

    public static SignalSource findRedstoneSignalSourceWithoutSignal(Level world, BlockPos startPos, int maxRange, Set<Block> blacklist) {
        Set<BlockPos> visited = new HashSet<>();
        Stack<BlockPos> stack = new Stack<>();
        SignalSource signalSource = new SignalSource();

        stack.push(startPos);

        while (!stack.isEmpty()) {
            BlockPos pos = stack.pop();

            if (visited.contains(pos) || startPos.distManhattan(pos) > maxRange) {
                continue;
            }
            visited.add(pos);

            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof RedStoneWireBlock) {
                // 继续遍历红石线的相邻方块
                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = pos.relative(direction);
                    if (!visited.contains(neighborPos)) {
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof RedStoneWireBlock || neighborState.isSignalSource()) {
                            stack.push(neighborPos);
                        }
                    }
                }
            } else if (state.isSignalSource() && !(state.getBlock() instanceof RedStoneWireBlock) && !blacklist.contains(state.getBlock())) {
                // 检查红石火把的状态，如果熄灭则跳过
                if (state.getBlock() instanceof RedstoneTorchBlock && !state.getValue(RedstoneTorchBlock.LIT)) {
                    continue;
                }
                signalSource.addSignalSource(pos, state);
            }
        }

        return signalSource;
    }

    public static SignalSource findRedstoneSignalSourceWithoutSignal(Level world, BlockPos startPos, int maxRange) {
        return findRedstoneSignalSourceWithoutSignal(world, startPos, maxRange, new HashSet<>());
    }

    public static boolean hasRedstoneSignalSource(Level world, BlockPos pos, int maxRange, Set<Block> blacklist) {
        SignalSource signalSource = findRedstoneSignalSource(world, pos, maxRange, blacklist);
        return signalSource != null && !signalSource.signalSources.isEmpty();
    }


    public static boolean hasRedstoneSignalSourceWithDirection(Level world, BlockPos pos, Direction direction, int maxRange, Set<Block> blacklist) {
        BlockPos blockpos = pos.relative(direction);
        return hasRedstoneSignalSource(world, blockpos, maxRange, blacklist);
    }

    public static boolean hasRedstoneSignalSourceWithDirection(Level world, BlockPos pos, Direction direction, Set<Block> blacklist) {
        BlockPos blockpos = pos.relative(direction);
        return hasRedstoneSignalSource(world, blockpos, 128, blacklist);
    }

    public static boolean hasRedstoneSignalSourceWithDirection(Level world, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return hasRedstoneSignalSource(world, blockpos, 128, new HashSet<>());
    }


    public static boolean hasRedstoneSignalSourceWithoutSignal(Level world, BlockPos pos, int maxRange, Set<Block> blacklist) {
        SignalSource signalSource = findRedstoneSignalSourceWithoutSignal(world, pos, maxRange, blacklist);
        return signalSource != null && !signalSource.signalSources.isEmpty();
    }

    public static boolean hasRedstoneSignalSourceWithDirectionWithoutSignal(Level world, BlockPos pos, Direction direction, int maxRange, Set<Block> blacklist) {
        BlockPos blockpos = pos.relative(direction);
        return hasRedstoneSignalSourceWithoutSignal(world, blockpos, maxRange, blacklist);
    }

    public static class SignalSource {
        public final Set<BlockData> signalSources;

        public SignalSource() {
            this.signalSources = new HashSet<>();
        }

        public void addSignalSource(BlockPos pos, BlockState state) {
            signalSources.add(new BlockData(pos, state));
        }

        public static class BlockData {
            public final BlockPos pos;
            public final BlockState state;

            public BlockData(BlockPos pos, BlockState state) {
                this.pos = pos;
                this.state = state;
            }
        }
    }
}
