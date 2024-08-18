package org.lycorecocafe.cmrs.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.client.gui.menu.MusicBoxMenu;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.utils.music.MusicPlayer;

public class MusicBoxBlockEntity extends BlockEntity implements MenuProvider {

    MusicPlayer musicPlayer = new MusicPlayer(this);

    public MusicBoxBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.MUSIC_BOX_BE.get(), pos, state);

    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public void setMusicPlayer(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Music Box Test");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        buffer.writeBlockPos(this.getBlockPos());
        return new MusicBoxMenu(id, playerInventory, buffer);
    }
//
//    @Override
//    protected void saveAdditional(CompoundTag tag) {
//        super.saveAdditional(tag);
//        tag.putDouble("Frequency", frequency);
//        List<Long> positions = new ArrayList<>();
//        for (BlockPos pos : matchReceivers) {
//            positions.add(pos.asLong());
//        }
//        tag.putLongArray("MatchReceivers", positions);
//    }
//
//    @Override
//    public void load(CompoundTag tag) {
//        super.load(tag);
//
//        frequency = tag.getDouble("Frequency");
//        matchReceivers.clear();
//        for (long pos : tag.getLongArray("MatchReceivers")) {
//            matchReceivers.add(BlockPos.of(pos));
//        }
//    }
//
//    @Override
//    public CompoundTag getUpdateTag() {
//        CompoundTag tag = super.getUpdateTag();
//        saveAdditional(tag);
//        return tag;
//    }
//
//    @Override
//    public void handleUpdateTag(CompoundTag tag) {
//        load(tag);
//    }
//
//    @Nullable
//    @Override
//    public ClientboundBlockEntityDataPacket getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }
//
//    @Override
//    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
//        handleUpdateTag(pkt.getTag());
//    }
}
