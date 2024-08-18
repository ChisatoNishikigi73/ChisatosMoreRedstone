package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PlaySoundTestPacket {
    private final BlockPos pos;
    private final SoundEvent soundEvent;

    // 构造函数，初始化位置和声音事件
    public PlaySoundTestPacket(BlockPos pos, SoundEvent soundEvent) {
        this.pos = pos;
        this.soundEvent = soundEvent;
    }

    // 从 FriendlyByteBuf 中读取数据的构造函数
    public PlaySoundTestPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        ResourceLocation soundEventLocation = buf.readResourceLocation();
        this.soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(soundEventLocation);
    }

    // 处理包的内容（服务器端执行）
    public static void handle(PlaySoundTestPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                player.getLevel().playSound(null, msg.pos, msg.soundEvent, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }

    // 将数据写入 FriendlyByteBuf
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(soundEvent.getLocation());
    }
}