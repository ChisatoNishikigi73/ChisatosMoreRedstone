package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.SignalEmitterBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SignalEmitterPacket {
    private final BlockPos pos;
    private final double frequency;
    private final List<BlockPos> positions;

    public SignalEmitterPacket(BlockPos pos, double frequency, List<BlockPos> positions) {
        this.pos = pos;
        this.frequency = frequency;
        this.positions = positions;
    }

    public SignalEmitterPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.frequency = buf.readDouble();
        int size = buf.readInt();
        this.positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.positions.add(buf.readBlockPos());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeDouble(frequency);
        buf.writeInt(positions.size());
        for (BlockPos position : positions) {
            buf.writeBlockPos(position);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level.getBlockEntity(pos) instanceof SignalEmitterBlockEntity) {
                SignalEmitterBlockEntity blockEntity = (SignalEmitterBlockEntity) player.level.getBlockEntity(pos);
                blockEntity.setFrequency(frequency);
                blockEntity.setMatchReceivers(positions);
                blockEntity.setChanged();
            }
        });
        return true;
    }
}