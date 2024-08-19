package org.lycorecocafe.cmrs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;

import java.util.function.Supplier;

public class MusicPlayerDownloadMusicNotify {
    private final BlockPos pos;
    private final String musicUrl;

    public MusicPlayerDownloadMusicNotify(BlockPos pos, String musicUrl) {
        this.pos = pos;
        this.musicUrl = musicUrl;
    }

    public MusicPlayerDownloadMusicNotify(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.musicUrl = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(musicUrl);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null && world.getBlockEntity(pos) instanceof MusicBoxBlockEntity clientBlockEntity) {
                clientBlockEntity.setMusicUrl(musicUrl);
                clientBlockEntity.downloadMusic();
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
