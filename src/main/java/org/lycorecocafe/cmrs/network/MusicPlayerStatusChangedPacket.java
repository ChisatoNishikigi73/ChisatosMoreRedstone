package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;
import org.lycorecocafe.cmrs.utils.music.MusicPlayer.STATUS;

import java.util.function.Supplier;

public class MusicPlayerStatusChangedPacket {
    private final BlockPos pos;
    private final STATUS status;

    public MusicPlayerStatusChangedPacket(BlockPos pos, STATUS status) {
        this.pos = pos;
        this.status = status;
    }

    public MusicPlayerStatusChangedPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.status = STATUS.valueOf(buf.readUtf(32767));
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(status.name());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level.getBlockEntity(pos) instanceof MusicBoxBlockEntity blockEntity) {
                System.out.println("status: " + blockEntity.getStatus());
                blockEntity.setStatus(status);
                blockEntity.setChanged();
                player.level.sendBlockUpdated(pos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
//
//                // 广播到所有加载了这个区块的客户端
//                LevelChunk chunk = player.level.getChunkAt(pos);
//                CMRS.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new MusicPlayerDownloadMusicNotify(pos, musicUrl));
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
