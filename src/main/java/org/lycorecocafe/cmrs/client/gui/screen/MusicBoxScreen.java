package org.lycorecocafe.cmrs.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.MusicBoxBlockEntity;
import org.lycorecocafe.cmrs.client.gui.menu.MusicBoxMenu;
import org.lycorecocafe.cmrs.network.MusicPlayerPacket;
import org.lycorecocafe.cmrs.network.MusicPlayerPlayPacket;
import org.lycorecocafe.cmrs.utils.game.music.MusicPlayer;
import org.slf4j.Logger;

public class MusicBoxScreen extends AbstractContainerScreen<MusicBoxMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(CMRS.MODID, "textures/gui/signal_emitter.png");
    private static final Logger LOGGER = LogUtils.getLogger();
    private EditBox inputBox; // 输入框
    private Button downLoadButton; // 下载按钮
    private Button playButton; // 播放按钮

    public MusicBoxScreen(MusicBoxMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        MusicBoxBlockEntity blockEntity = menu.getBlockEntity();

        // 显示“Music Url(.ogg)”的标题，按钮不可点击
        Component titleText = Component.literal("Music Url(.ogg)");

        // 初始化输入框
        this.inputBox = new EditBox(this.font, centerX - 65, centerY - 35, 130, 20, Component.literal(""));
        this.inputBox.setMaxLength(256);
        inputBox.setValue(blockEntity.getMusicUrl());

        // 初始化下载按钮
        this.downLoadButton = new Button(centerX - 50, centerY - 10, 100, 20, Component.literal("Set Url"), button -> {
            blockEntity.setMusicUrl(inputBox.getValue());
            blockEntity.setStatus(MusicPlayer.STATUS.URL);
            CMRS.CHANNEL.sendToServer(new MusicPlayerPacket(menu.getBlockEntity().getBlockPos(), blockEntity.getMusicUrl(), blockEntity.getStatus()));
        });

        // 初始化播放按钮
        this.playButton = new Button(centerX - 50, centerY + 35, 100, 20, Component.literal("Play"), button -> {
            if (blockEntity.getStatus().equals(MusicPlayer.STATUS.NONE) || blockEntity.getStatus().equals(MusicPlayer.STATUS.ERROR))
                return;

            if (blockEntity.getStatus().equals(MusicPlayer.STATUS.PLAYING)) {
                CMRS.CHANNEL.sendToServer(new MusicPlayerPlayPacket(menu.getBlockEntity().getBlockPos(), blockEntity.getMusicUrl(), MusicPlayer.STATUS.PAUSE));
            } else if (blockEntity.getStatus().equals(MusicPlayer.STATUS.PAUSE) || blockEntity.getStatus().equals(MusicPlayer.STATUS.URL)) {
                CMRS.CHANNEL.sendToServer(new MusicPlayerPlayPacket(menu.getBlockEntity().getBlockPos(), blockEntity.getMusicUrl(), MusicPlayer.STATUS.PLAYING));
            }

//            CMRS.CHANNEL.sendToServer(new MusicPlayerPlayPacket(menu.getBlockEntity().getBlockPos(), blockEntity.getMusicUrl(), blockEntity.getStatus()));
        });

        this.addRenderableWidget(new Button(centerX - 50, centerY - 60, 100, 20, titleText, button -> {
        })).active = false;
        this.addRenderableWidget(this.inputBox);
        this.addRenderableWidget(this.playButton);
        this.addRenderableWidget(this.downLoadButton);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        // 根据音乐播放器的状态更新播放按钮的文本和状态
        MusicPlayer.STATUS status = menu.getBlockEntity().getStatus();
        Component playButtonC = switch (status) {
            case PLAYING -> Component.literal("Pause");
            case PAUSE -> Component.literal("Play");
            default -> Component.literal("Play");
        };
        this.playButton.setMessage(playButtonC);
//        this.playButton.active = status != MusicPlayer.STATUS.DOWNLOADING && status != MusicPlayer.STATUS.NONE && status != MusicPlayer.STATUS.ERROR;

        // 显示音乐状态
        drawCenteredString(matrixStack, this.font, "Music Status:", this.width / 2, this.height / 2 + 13, 4210752);
        drawCenteredString(matrixStack, this.font, menu.getBlockEntity().getStatusLocal().toString(), this.width / 2, this.height / 2 + 23, 4210752);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
    }
}
