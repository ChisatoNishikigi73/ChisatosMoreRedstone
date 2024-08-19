package org.lycorecocafe.cmrs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.client.gui.menu.MusicBoxMenu;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.utils.music.MusicPlayer;

public class MusicBoxBlockEntity extends BlockEntity implements MenuProvider {

    private MusicPlayer musicPlayer;
    private String musicUrl = "";
    private MusicPlayer.STATUS status = MusicPlayer.STATUS.NONE;
    private MusicPlayer.STATUS statusLocal = MusicPlayer.STATUS.NONE;

    public MusicBoxBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.MUSIC_BOX_BE.get(), pos, state);
        this.musicPlayer = new MusicPlayer(this);
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public void setMusicPlayer(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
        this.musicPlayer.setMusicUrl(musicUrl);
        setChanged();
    }

    public MusicPlayer.STATUS getStatus() {
        return status;
    }

    public void setStatus(MusicPlayer.STATUS status) {
        this.status = status;
        this.musicPlayer.setStatus(status);
    }

    public MusicPlayer.STATUS getStatusLocal() {
        return statusLocal;
    }

    public void setStatusLocal(MusicPlayer.STATUS statusLocal) {
        this.statusLocal = statusLocal;
        this.musicPlayer.setStatusLocal(statusLocal);
    }

    public void downloadMusic() {
        if (this.level != null && this.level.isClientSide) {
            this.musicPlayer.downloadMusic();
        }
    }

    @Override
    public void invalidateCaps() {
        this.getMusicPlayer().stopSound();
        final CapabilityDispatcher disp = getCapabilities();
        if (disp != null)
            disp.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Music Box Test");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        buf.writeBlockPos(this.worldPosition);
        return new MusicBoxMenu(id, playerInventory, buf);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("musicUrl", this.musicUrl);
        tag.putString("Status", this.status.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.musicUrl = tag.getString("musicUrl");
        this.status = MusicPlayer.STATUS.valueOf(tag.getString("Status"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }
}
