package org.lycorecocafe.cmrs.client.gui.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;
import org.lycorecocafe.cmrs.init.MenuInit;

public class HoloDisplayTerminalMenu extends AbstractContainerMenu {
    private final HoloDisplayTerminalBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public HoloDisplayTerminalMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, getBlockEntityFromBuf(extraData, playerInventory.player.level));
    }

    public HoloDisplayTerminalMenu(int id, Inventory playerInventory, HoloDisplayTerminalBlockEntity blockEntity) {
        super(MenuInit.HOLO_DISPLAY_TERMINAL_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    private static HoloDisplayTerminalBlockEntity getBlockEntityFromBuf(FriendlyByteBuf buf, Level world) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HoloDisplayTerminalBlockEntity) {
            return (HoloDisplayTerminalBlockEntity) blockEntity;
        }
        throw new IllegalStateException("Block entity is not a HoloDisplayTerminalBlockEntity!");
    }

    public HoloDisplayTerminalBlockEntity getBlockEntity() {
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
