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
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.blocks.SignalReceiverBlock;
import org.lycorecocafe.cmrs.client.gui.menu.SignalReceiverMenu;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;

public class SignalReceiverBlockEntity extends BlockEntity implements MenuProvider {
    private double frequency;
    private BlockPos EmitterPos;
    private boolean powered;

    public SignalReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.SIGNAL_RECEIVER_BE.get(), pos, state);
        this.frequency = 85.5;
        this.powered = false;
    }

    public boolean isPowered() {
        return powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
        if (level != null) {
            level.setBlock(worldPosition, getBlockState().setValue(SignalReceiverBlock.POWERED, powered), 3);
//            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
        setChanged();
    }

    public double getFrequency() {
        return frequency;
    }

    public BlockPos getEmitterPos() {
        return EmitterPos;
    }

    public void setEmitterPos(BlockPos emitterPos) {
        EmitterPos = emitterPos;
//        System.out.println("set " + emitterPos + " to " + this.getBlockPos() + " " + this + " " + this.level);
        setChanged();
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Signal Receiver");
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        buf.writeBlockPos(this.worldPosition);
        return new SignalReceiverMenu(id, playerInventory, buf);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("Frequency", frequency);
        tag.putBoolean("Powered", powered);

        if (EmitterPos != null) {
            tag.putLong("EmitterPos", EmitterPos.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        frequency = tag.getDouble("Frequency");
        powered = tag.getBoolean("Powered");

        if (tag.contains("EmitterPos")) {
            EmitterPos = BlockPos.of(tag.getLong("EmitterPos"));
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
