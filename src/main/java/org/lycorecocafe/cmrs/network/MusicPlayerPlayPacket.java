package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;
import org.lycorecocafe.cmrs.utils.music.MusicPlayer.STATUS;

import java.util.function.Supplier;

public class MusicPlayerPlayPacket {
    private final BlockPos pos;
    private final String musicUrl;
    private final STATUS status;

    public MusicPlayerPlayPacket(BlockPos pos, String musicUrl, STATUS status) {
        this.pos = pos;
        this.musicUrl = musicUrl;
        this.status = status;
    }

    public MusicPlayerPlayPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.musicUrl = buf.readUtf(32767);
        this.status = STATUS.valueOf(buf.readUtf(32767));
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(musicUrl);
        buf.writeUtf(status.name());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level.getBlockEntity(pos) instanceof MusicBoxBlockEntity blockEntity) {
                blockEntity.setMusicUrl(musicUrl);
                blockEntity.setStatus(status);
                blockEntity.setChanged();
                player.level.sendBlockUpdated(pos, blockEntity.getBlockState(), blockEntity.getBlockState(), 3);


                // 广播到所有加载了这个区块的客户端
                LevelChunk chunk = player.level.getChunkAt(pos);
                CMRS.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new MusicPlayerPlayNotify(pos, musicUrl, status));
            }
        });
        return true;
    }
}
