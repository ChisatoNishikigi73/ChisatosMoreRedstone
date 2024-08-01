package org.lycorecocafe.cmrs.client.gui.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import org.lycorecocafe.cmrs.blockentity.SignalReceiverBlockEntity;
import org.lycorecocafe.cmrs.init.MenuInit;

public class SignalReceiverMenu extends AbstractContainerMenu {
    private final SignalReceiverBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public SignalReceiverMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (SignalReceiverBlockEntity) playerInventory.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public SignalReceiverMenu(int id, Inventory playerInventory, SignalReceiverBlockEntity blockEntity) {
        super(MenuInit.SIGNAL_RECEIVER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    public SignalReceiverBlockEntity getBlockEntity() {
        return blockEntity;
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