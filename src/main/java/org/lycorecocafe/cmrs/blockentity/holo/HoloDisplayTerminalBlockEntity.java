package org.lycorecocafe.cmrs.blockentity.holo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.client.gui.menu.HoloDisplayTerminalMenu;
import org.lycorecocafe.cmrs.init.BlockEntitiesInit;
import org.lycorecocafe.cmrs.network.HoloDisplayTerminalChangePaket;

import java.util.Objects;

public class HoloDisplayTerminalBlockEntity extends BlockEntity implements MenuProvider {

    private BlockPos startCorner = this.getBlockPos().offset(5, 5, 5);
    // 区域的起始角
    private BlockPos endCorner = this.getBlockPos().offset(-5, -1, -5);// 区域的结束角
    private AABB boundingBox = new AABB(startCorner, endCorner); // 使用AABB表示区域
    private String playerName = "";
    private MODE mode = MODE.AREA;
    private CompoundTag storedEntityData = new CompoundTag(); // 用于存储实体的完整NBT数据
    private boolean initialized = false;
    private Player player;
    private int time = 0;

    private float rotateSpeed = 0.0f;

    public HoloDisplayTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesInit.HOLO_DISPLAY_TERMINAL_BE.get(), pos, state);
    }

    protected void setInitialized() {
        if (initialized) return;
        if (mode == MODE.TRACKER) {
            this.player = getPlayerByName(playerName);
        }
        initialized = true;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        buf.writeBlockPos(this.worldPosition);
        return new HoloDisplayTerminalMenu(id, playerInventory, buf);
    }

    // 基于玩家位置更新角点和边界框的方法
    public void updateCornersBasedOnPlayer(Player player) {
        if (level != null && !level.players().isEmpty()) {
            if (player.isRemoved()) {
                updateMode(MODE.OFFLINE);
            }
            BlockPos playerPos = player.blockPosition();

            int radius = 12; // 定义以玩家为中心的边界框半径

            // 以玩家为中心设置startCorner和endCorner
            startCorner = playerPos.offset(-radius, -radius + 7, -radius);
            endCorner = playerPos.offset(radius, radius, radius);

            // 基于新的角点创建AABB
            updateBoundingBox();

            setChanged(); // 标记方块实体已更改，以保存更新后的边界框
        }
    }

    // 获取该区域内所有方块的状态
    public BlockState[][][] getDisplayBlockStates() {
        setInitialized();
        if (mode.equals(MODE.OFFLINE)) {
            if (this.time == 1000) {
                this.time = 0;
                player = getPlayerByName(playerName);
            } else {
                time++;
            }
        }
        if (mode.equals(MODE.TRACKER) && player != null) {
            updateCornersBasedOnPlayer(player);
        }
        if (level != null && level.isLoaded(startCorner) && level.isLoaded(endCorner)) {
            int x1 = Math.min(startCorner.getX(), endCorner.getX());
            int y1 = Math.min(startCorner.getY(), endCorner.getY());
            int z1 = Math.min(startCorner.getZ(), endCorner.getZ());
            int x2 = Math.max(startCorner.getX(), endCorner.getX());
            int y2 = Math.max(startCorner.getY(), endCorner.getY());
            int z2 = Math.max(startCorner.getZ(), endCorner.getZ());

            BlockState[][][] blockStates = new BlockState[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        BlockPos currentPos = new BlockPos(x, y, z);
                        blockStates[x - x1][y - y1][z - z1] = level.getBlockState(currentPos);
                    }
                }
            }
            return blockStates;
        }
        return new BlockState[0][0][0];
    }


    public Player getPlayerByName(String playerName) {
        if (!((mode.equals(MODE.TRACKER) || mode.equals(MODE.OFFLINE)) && this.getLevel().isClientSide) || Objects.equals(playerName, "") || playerName == null)
            return null;
//        System.out.println("Getting player by name: " + playerName);
        ClientLevel clientWorld = Minecraft.getInstance().level;
        if (clientWorld != null) {
            for (Player player : clientWorld.players()) {
                if (player.getName().getString().equals(playerName)) {
                    updateMode(MODE.TRACKER);
                    return player;
                }
            }
        }
        updateMode(MODE.OFFLINE);
        return null; // 未找到玩家
    }

    // 获取要显示的实体范围
    public AABB getDisplayRange() {
        return boundingBox;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            this.playerName = "";
            this.player = null;
        }
        this.player = getPlayerByName(playerName);
        this.playerName = playerName;
    }

    public void cleanPlayerName() {
        this.player = null;
        this.playerName = "";
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }

    // 获取实体的NBT数据
    public CompoundTag getStoredEntityData() {
        return this.storedEntityData;
    }

    // 存储实体的NBT数据
    public void setStoredEntityData(CompoundTag entityData) {
        this.storedEntityData = entityData;
        setChanged();
    }

    public void updateMode(MODE mode) {
        if (this.mode.equals(mode)) {
            return;
        }
        if (!(mode.equals(MODE.TRACKER) || mode.equals(MODE.OFFLINE))) {
            this.playerName = "";
            this.player = null;
        }
        this.mode = mode;
        CMRS.CHANNEL.sendToServer(new HoloDisplayTerminalChangePaket(this));
    }

    public void setPosArea(BlockPos pos1, BlockPos pos2) {
        this.startCorner = pos1;
        this.endCorner = pos2;
        boundingBox = new AABB(pos1, pos2);
        setChanged();
    }

    public void setTrackedPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public BlockPos getEndCorner() {
        return endCorner;
    }

    public void setEndCorner(BlockPos pos) {
        this.endCorner = pos;
        updateBoundingBox(); // 更新边界框
        setChanged();
    }

    public BlockPos getStartCorner() {
        return startCorner;
    }

    public void setStartCorner(BlockPos pos) {
        this.startCorner = pos;
        updateBoundingBox(); // 更新边界框
        setChanged();
    }

    private void updateBoundingBox() {
        if (startCorner != null && endCorner != null) {
            boundingBox = new AABB(startCorner, endCorner);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("StartCorner")) {
            startCorner = BlockPos.of(tag.getLong("StartCorner"));
        }
        if (tag.contains("EndCorner")) {
            endCorner = BlockPos.of(tag.getLong("EndCorner"));
        }
        if (tag.contains("PlayerName")) {
            playerName = tag.getString("PlayerName");
        }
        if (tag.contains("Mode")) {
            mode = MODE.valueOf(tag.getString("Mode"));
        }
        if (tag.contains("StoredEntityData")) {
            storedEntityData = tag.getCompound("StoredEntityData"); // 加载完整的实体NBT数据
        }
        if (tag.contains("RotateSpeed")) {
            rotateSpeed = tag.getFloat("RotateSpeed");
        }
        updateBoundingBox(); // 加载后更新边界框
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (startCorner != null) {
            tag.putLong("StartCorner", startCorner.asLong());
        }
        if (endCorner != null) {
            tag.putLong("EndCorner", endCorner.asLong());
        }
        if (playerName != null) {
            tag.putString("PlayerName", playerName);
        }
        if (mode != null) {
            tag.putString("Mode", mode.name());
        }
        if (storedEntityData != null) {
            tag.put("StoredEntityData", storedEntityData); // 保存完整的实体NBT数据
        }
        tag.putFloat("RotateSpeed", rotateSpeed);

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

    public enum MODE {
        NONE,
        MODEL,
        AREA,
        TRACKER,
        OFFLINE,
    }
}
