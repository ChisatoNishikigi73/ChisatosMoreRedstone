package org.lycorecocafe.cmrs.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.SignalEmitterBlockEntity;
import org.lycorecocafe.cmrs.blockentity.SignalReceiverBlockEntity;
import org.lycorecocafe.cmrs.blocks.SignalReceiverBlock;
import org.lycorecocafe.cmrs.network.SignalReceiverPacket;

import java.util.ArrayList;
import java.util.List;

public class SignalHandler {

//    public static void findAndPrintReceivers(SignalEmitterBlockEntity emitter, int range) {
//        Level world = emitter.getLevel();
//        if (world == null) return;
//
//        BlockPos emitterPos = emitter.getBlockPos();
//        double emitterFrequency = emitter.getFrequency();
//
//        for (int x = -range; x <= range; x++) {
//            for (int y = -range; y <= range; y++) {
//                for (int z = -range; z <= range; z++) {
//                    BlockPos currentPos = emitterPos.offset(x, y, z);
//                    BlockEntity blockEntity = world.getBlockEntity(currentPos);
//
//                    if (blockEntity instanceof SignalReceiverBlockEntity) {
//                        SignalReceiverBlockEntity receiver = (SignalReceiverBlockEntity) blockEntity;
//                        if (receiver.getFrequency() == emitterFrequency) {
//                            System.out.println("Matching receiver found at: " + currentPos);
//                        }
//                    }
//                }
//            }
//        }
//    }

    public static List<BlockPos> findReceiversInRange(SignalEmitterBlockEntity emitter, int range) {
        Level world = emitter.getLevel();
        if (world == null) return null;
        List<BlockPos> receivers = new ArrayList<>();

        BlockPos emitterPos = emitter.getBlockPos();
        double emitterFrequency = emitter.getFrequency();

        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    BlockPos currentPos = emitterPos.offset(x, y, z);
                    BlockEntity blockEntity = world.getBlockEntity(currentPos);

                    if (blockEntity instanceof SignalReceiverBlockEntity) {
                        SignalReceiverBlockEntity receiver = (SignalReceiverBlockEntity) blockEntity;
                        if (receiver.getFrequency() == emitterFrequency) {
                            receivers.add(currentPos);
//                            .out.println("Matching receiver found at: " + currentPos);
                        }
                    }
                }
            }
        }
        return receivers;
    }

    public static void applySignal(SignalEmitterBlockEntity emitter, List<BlockPos> receiverPos) {
        Level world = emitter.getLevel();
        if (world == null) return;

        for (BlockPos pos : receiverPos) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignalReceiverBlockEntity receiver) {
                receiver.setEmitterPos(emitter.getBlockPos());
                receiver.setPowered(emitter.isPowered());
                receiver.setChanged();
                world.setBlock(pos, receiver.getBlockState().setValue(SignalReceiverBlock.POWERED, emitter.isPowered()), 3);
                world.sendBlockUpdated(pos, receiver.getBlockState(), receiver.getBlockState(), 3);

                CMRS.CHANNEL.sendToServer(new SignalReceiverPacket(receiver.getBlockPos(), receiver.getFrequency(), emitter.getBlockPos()));
            }
        }
    }

    public static void clearSignal(SignalEmitterBlockEntity emitter, List<BlockPos> receiverPos) {
        Level world = emitter.getLevel();
        if (world == null) return;

        for (BlockPos pos : receiverPos) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignalReceiverBlockEntity receiver) {
                receiver.setEmitterPos(pos);
                receiver.setPowered(false);
                receiver.setChanged();
                world.setBlock(pos, receiver.getBlockState().setValue(SignalReceiverBlock.POWERED, receiver.isPowered()), 3);
                world.sendBlockUpdated(pos, receiver.getBlockState(), receiver.getBlockState(), 3);
//                CMRS.CHANNEL.sendToServer(new SignalReceiverPacket(pos, receiver.getFrequency(), emitter.getBlockPos()));
            }
        }
    }

//    public static void applySignal(SignalEmitterBlockEntity emitter, List<BlockPos> receiverPos, boolean powered) {
//        Level world = emitter.getLevel();
//        if (world == null) return;
//
//        for (BlockPos pos : receiverPos) {
//            BlockEntity blockEntity = world.getBlockEntity(pos);
//            if (blockEntity instanceof SignalReceiverBlockEntity receiver) {
//                //TODO: another emitter
//                receiver.setEmitterPos(receiver.getBlockPos());
//                receiver.setPowered(false);
//                receiver.setChanged();
//                world.setBlock(pos, receiver.getBlockState().setValue(SignalReceiverBlock.POWERED, false), 3);
//                world.sendBlockUpdated(pos, receiver.getBlockState(), receiver.getBlockState(), 3);
//            }
//        }
//    }

    public static void setReceiverPowered(Level world, BlockPos pos, boolean powered) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignalReceiverBlockEntity) {
                ((SignalReceiverBlockEntity) blockEntity).setPowered(powered);
            }
        }
    }

    public static void setReceiverPowered(Level world, List<BlockPos> positions, boolean powered) {
        if (!world.isClientSide) {
            for (BlockPos pos : positions) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof SignalReceiverBlockEntity) {
                    ((SignalReceiverBlockEntity) blockEntity).setPowered(powered);
                }
            }
        }
    }
}