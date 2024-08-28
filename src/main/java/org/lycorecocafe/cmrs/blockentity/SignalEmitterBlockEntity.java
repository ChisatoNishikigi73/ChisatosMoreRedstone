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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.client.gui.menu.SignalEmitterMenu;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.utils.game.SignalFinder;

import java.util.ArrayList;
import java.util.List;

public class SignalEmitterBlockEntity extends BlockEntity implements MenuProvider {
    private double frequency;
    private boolean powered;
    private List<BlockPos> matchReceivers = new ArrayList<>();

    public SignalEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.SIGNAL_EMITTER_BE.get(), pos, state);
        this.frequency = 85.5;
        this.powered = false;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
        setChanged();
    }

    // Handle changes in redstone signal to update receiver states
    public void onRedstoneSignalChanged(Level world, boolean isPowered) {
        if (this.powered != isPowered) {
            this.powered = isPowered;
            SignalFinder.setReceiverPowered(world, getMatchReceivers(), isPowered);
            setChanged();
        }
    }

    public BlockPos getBlockPos() {
        return this.worldPosition;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        setChanged();
    }

    public List<BlockPos> getMatchReceivers() {
        return matchReceivers;
    }

    public void setMatchReceivers(List<BlockPos> matchReceivers) {
        this.matchReceivers = matchReceivers;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Signal Emitter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        buffer.writeBlockPos(this.getBlockPos());
        return new SignalEmitterMenu(id, playerInventory, buffer);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("Frequency", frequency);
        List<Long> positions = new ArrayList<>();
        for (BlockPos pos : matchReceivers) {
            positions.add(pos.asLong());
        }
        tag.putLongArray("MatchReceivers", positions);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        frequency = tag.getDouble("Frequency");
        matchReceivers.clear();
        for (long pos : tag.getLongArray("MatchReceivers")) {
            matchReceivers.add(BlockPos.of(pos));
        }
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
