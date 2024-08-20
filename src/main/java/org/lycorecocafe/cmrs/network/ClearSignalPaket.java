package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.SignalEmitterBlockEntity;
import org.lycorecocafe.cmrs.handler.SignalHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClearSignalPaket {
    private final BlockPos emitterPos;
    private final List<BlockPos> receiverPos;

    public ClearSignalPaket(BlockPos emitterPos, List<BlockPos> receiverPos) {
        this.emitterPos = emitterPos;
        this.receiverPos = receiverPos;
    }

    public ClearSignalPaket(FriendlyByteBuf buf) {
        this.emitterPos = buf.readBlockPos();
        int size = buf.readInt();
        this.receiverPos = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.receiverPos.add(buf.readBlockPos());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(emitterPos);
        buf.writeInt(receiverPos.size());
        for (BlockPos pos : receiverPos) {
            buf.writeBlockPos(pos);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level world = player.level;
                BlockEntity blockEntity = world.getBlockEntity(emitterPos);
                if (blockEntity instanceof SignalEmitterBlockEntity emitter) {
                    SignalHandler.clearSignal(emitter, receiverPos);
                }
            }
        });
        return true;
    }
}
