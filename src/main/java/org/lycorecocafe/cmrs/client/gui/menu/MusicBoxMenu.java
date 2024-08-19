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
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;
import org.lycorecocafe.cmrs.init.MenuInit;

public class MusicBoxMenu extends AbstractContainerMenu {
    private final MusicBoxBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public MusicBoxMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, (MusicBoxBlockEntity) playerInventory.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public MusicBoxMenu(int id, Inventory playerInventory, MusicBoxBlockEntity blockEntity) {
        super(MenuInit.MUSIC_BOX_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }

    private static MusicBoxBlockEntity getBlockEntityFromBuf(FriendlyByteBuf buf, Level world) {
        BlockPos pos = buf.readBlockPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MusicBoxBlockEntity) {
            return (MusicBoxBlockEntity) blockEntity;
        }
        throw new IllegalStateException("Block entity is not a MusicBoxBlockEntity!");
    }

    public MusicBoxBlockEntity getBlockEntity() {
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
