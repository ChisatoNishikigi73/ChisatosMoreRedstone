package org.lycorecocafe.cmrs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;
import org.lycorecocafe.cmrs.utils.game.music.MusicPlayer;

import java.util.function.Supplier;

public class MusicPlayerPlayNotify {
    private final BlockPos pos;
    private final String musicUrl;
    private final MusicPlayer.STATUS status;

    public MusicPlayerPlayNotify(BlockPos pos, String musicUrl, MusicPlayer.STATUS status) {
        this.pos = pos;
        this.musicUrl = musicUrl;
        this.status = status;
    }

    public MusicPlayerPlayNotify(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.musicUrl = buf.readUtf();
        this.status = MusicPlayer.STATUS.valueOf(buf.readUtf());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(musicUrl);
        buf.writeUtf(status.name());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null && world.getBlockEntity(pos) instanceof MusicBoxBlockEntity clientBlockEntity) {
                if (status == MusicPlayer.STATUS.PLAYING) {
                    clientBlockEntity.setStatusLocal(MusicPlayer.STATUS.PLAYING);
                    clientBlockEntity.getMusicPlayer().play();
                } else if (status == MusicPlayer.STATUS.PAUSE) {
                    clientBlockEntity.setStatusLocal(MusicPlayer.STATUS.PAUSE);
                    clientBlockEntity.getMusicPlayer().stopSound();
                }
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
