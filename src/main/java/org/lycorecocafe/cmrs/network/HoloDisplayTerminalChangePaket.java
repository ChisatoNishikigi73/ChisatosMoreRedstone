package org.lycorecocafe.cmrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;

import java.util.function.Supplier;

import static org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity.MODE;

public class HoloDisplayTerminalChangePaket {
    private final BlockPos pos;
    private final BlockPos startCorner;
    private final BlockPos endCorner;
    private final String playerName;
    private final CompoundTag storedEntityData;
    private final MODE mode;
    private final float rotateSpeed;

    public HoloDisplayTerminalChangePaket(HoloDisplayTerminalBlockEntity e) {
        this.pos = e.getBlockPos();
        this.startCorner = e.getStartCorner();
        this.endCorner = e.getEndCorner();
        this.playerName = e.getPlayerName();
        this.storedEntityData = e.getStoredEntityData();
        this.mode = e.getMode();
        this.rotateSpeed = e.getRotateSpeed();
    }

    public HoloDisplayTerminalChangePaket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.startCorner = buf.readBlockPos();
        this.endCorner = buf.readBlockPos();
        this.playerName = buf.readUtf();
        this.storedEntityData = buf.readNbt();
        this.mode = MODE.valueOf(buf.readUtf());
        this.rotateSpeed = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBlockPos(startCorner);
        buf.writeBlockPos(endCorner);
        buf.writeUtf(playerName);
        buf.writeNbt(storedEntityData);
        buf.writeUtf(mode.name());
        buf.writeFloat(rotateSpeed);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level world = player.level;
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof HoloDisplayTerminalBlockEntity entity) {
                    entity.setPosArea(startCorner, endCorner);
                    entity.setPlayerName(playerName);
                    entity.setStoredEntityData(storedEntityData);
                    entity.setMode(mode);
                    entity.setRotateSpeed(rotateSpeed);

                    LevelChunk chunk = player.level.getChunkAt(pos);
                    CMRS.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new HoloDisplayTerminalChangeNotify(pos, startCorner, endCorner, playerName, storedEntityData, mode, rotateSpeed));
                }
            }
        });
        return true;
    }
}
