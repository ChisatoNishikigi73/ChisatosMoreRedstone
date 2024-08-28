package org.lycorecocafe.cmrs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;

import java.util.function.Supplier;

public class HoloDisplayTerminalChangeNotify {
    private final BlockPos pos;
    private final BlockPos startCorner;
    private final BlockPos endCorner;
    private final String playerName;
    private final CompoundTag storedEntityData;
    private final HoloDisplayTerminalBlockEntity.MODE mode;
    private final float rotateSpeed;

    public HoloDisplayTerminalChangeNotify(BlockPos pos, BlockPos startCorner, BlockPos endCorner, String playerName, CompoundTag storedEntityData, HoloDisplayTerminalBlockEntity.MODE mode, float rotateSpeed) {
        this.pos = pos;
        this.startCorner = startCorner;
        this.endCorner = endCorner;
        this.playerName = playerName;
        this.storedEntityData = storedEntityData;
        this.mode = mode;
        this.rotateSpeed = rotateSpeed;
    }

    public HoloDisplayTerminalChangeNotify(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.startCorner = buf.readBlockPos();
        this.endCorner = buf.readBlockPos();
        this.playerName = buf.readUtf();
        this.storedEntityData = buf.readNbt();
        this.mode = HoloDisplayTerminalBlockEntity.MODE.valueOf(buf.readUtf());
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
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null && world.getBlockEntity(pos) instanceof HoloDisplayTerminalBlockEntity e) {
                e.setPosArea(startCorner, endCorner);
                e.setPlayerName(playerName);
                e.setStoredEntityData(storedEntityData);
                e.setMode(mode);
                e.setRotateSpeed(rotateSpeed);
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}
