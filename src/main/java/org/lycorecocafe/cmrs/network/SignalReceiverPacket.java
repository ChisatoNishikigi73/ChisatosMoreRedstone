package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.SignalReceiverBlockEntity;

import java.util.function.Supplier;

public class SignalReceiverPacket {
    private final BlockPos receiverPos;
    private final double frequency;
    private final BlockPos emitterPos;

    public SignalReceiverPacket(BlockPos receiverPos, double frequency, BlockPos emitterPos) {
        this.receiverPos = receiverPos;
        this.frequency = frequency;
        this.emitterPos = emitterPos;
    }

    public SignalReceiverPacket(FriendlyByteBuf buf) {
        this.receiverPos = buf.readBlockPos();
        this.frequency = buf.readDouble();
        this.emitterPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(receiverPos);
        buf.writeDouble(frequency);
        buf.writeBlockPos(emitterPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                BlockEntity blockEntity = player.level.getBlockEntity(receiverPos);
                if (blockEntity instanceof SignalReceiverBlockEntity) {
                    SignalReceiverBlockEntity receiverBlockEntity = (SignalReceiverBlockEntity) blockEntity;
                    receiverBlockEntity.setFrequency(frequency);
                    receiverBlockEntity.setEmitterPos(emitterPos);
                    receiverBlockEntity.setChanged();
                    receiverBlockEntity.getLevel().sendBlockUpdated(receiverBlockEntity.getBlockPos(), receiverBlockEntity.getBlockState(), receiverBlockEntity.getBlockState(), 3);
                }
            }
        });
        return true;
    }
}
