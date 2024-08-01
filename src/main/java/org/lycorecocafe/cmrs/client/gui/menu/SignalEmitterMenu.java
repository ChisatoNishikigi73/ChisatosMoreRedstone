package org.lycorecocafe.cmrs.client.gui.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lycorecocafe.cmrs.blockentity.SignalEmitterBlockEntity;
import org.lycorecocafe.cmrs.init.MenuInit;

public class SignalEmitterMenu extends AbstractContainerMenu {
    private final SignalEmitterBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public SignalEmitterMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, getBlockEntityFromBuf(extraData, playerInventory.player.level));
    }

    public SignalEmitterMenu(int id, Inventory playerInventory, SignalEmitterBlockEntity blockEntity) {
        super(MenuInit.SIGNAL_EMITTER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    private static SignalEmitterBlockEntity getBlockEntityFromBuf(FriendlyByteBuf buf, Level world) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignalEmitterBlockEntity) {
            return (SignalEmitterBlockEntity) blockEntity;
        }
        throw new IllegalStateException("Block entity is not a SignalEmitterBlockEntity!");
    }

    public SignalEmitterBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ServerLevel getServerLevel() {
        return (ServerLevel) blockEntity.getLevel();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, blockEntity.getBlockState().getBlock());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
