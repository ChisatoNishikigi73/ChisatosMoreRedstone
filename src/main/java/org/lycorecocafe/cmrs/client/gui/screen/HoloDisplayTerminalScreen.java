package org.lycorecocafe.cmrs.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity;
import org.lycorecocafe.cmrs.client.gui.menu.HoloDisplayTerminalMenu;
import org.lycorecocafe.cmrs.network.HoloDisplayTerminalChangePaket;
import org.lycorecocafe.cmrs.utils.game.entity.EntityHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lycorecocafe.cmrs.CMRS.CHANNEL;
import static org.lycorecocafe.cmrs.blockentity.holo.HoloDisplayTerminalBlockEntity.MODE;

@OnlyIn(Dist.CLIENT)
public class HoloDisplayTerminalScreen extends AbstractContainerScreen<HoloDisplayTerminalMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(CMRS.MODID, "textures/gui/holo_control.png");
    //Area mode
    EditBox xCoordInput;
    EditBox yCoordInput;
    EditBox zCoordInput;
    EditBox xCoordInput2;
    EditBox yCoordInput2;
    EditBox zCoordInput2;
    Button confirmButton;
    private Button modelButton;
    private Button areaButton;
    private Button trackerButton;
    private EditBox rotateSpeed;
    //Tracker mode
    private List<Button> playerButtons = new ArrayList<>();

    public HoloDisplayTerminalScreen(HoloDisplayTerminalMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int buttonWidth = 80;
        int buttonSpacing = 10;

        int startX = centerX - (buttonWidth * 3 + buttonSpacing * 2) / 2;

        this.modelButton = new Button(startX, 35, buttonWidth, 20, Component.literal("MODEL"), button -> {
            menu.getBlockEntity().updateMode(MODE.MODEL);
            selectModelMode();
        });

        this.areaButton = new Button(startX + buttonWidth + buttonSpacing, 35, buttonWidth, 20, Component.literal("AREA"), button -> {
            menu.getBlockEntity().updateMode(MODE.AREA);
            selectAreaMode();
        });

        this.trackerButton = new Button(startX + (buttonWidth + buttonSpacing) * 2, 35, buttonWidth, 20, Component.literal("TRACKER"), button -> {
            menu.getBlockEntity().setMode(HoloDisplayTerminalBlockEntity.MODE.OFFLINE);
            menu.getBlockEntity().setPlayerName("");
            CMRS.CHANNEL.sendToServer(new HoloDisplayTerminalChangePaket(menu.getBlockEntity()));
            selectTrackerMode();
        });

        rotateSpeed = new EditBox(this.font, centerX - 40, 160, 80, 20, Component.literal("Rotate Speed"));
        rotateSpeed.setValue(String.valueOf(menu.getBlockEntity().getRotateSpeed())); // 设置默认值
        rotateSpeed.setResponder(this::updateRotateSpeed);

        this.addRenderableWidget(rotateSpeed);
        this.addRenderableWidget(this.modelButton);
        this.addRenderableWidget(this.areaButton);
        this.addRenderableWidget(this.trackerButton);

//        System.out.println(menu.getBlockEntity().getMode());
        switch (menu.getBlockEntity().getMode()) {
            case TRACKER, OFFLINE -> selectTrackerMode();
            case AREA -> selectAreaMode();
            case MODEL -> selectModelMode();
        }
    }

    private void updateRotateSpeed(String speed) {
        if (speed.matches("^[0-9]*\\.?[0-9]+$|^[0-9]+$")) {
            menu.getBlockEntity().setRotateSpeed(Float.parseFloat(speed));
            CMRS.CHANNEL.sendToServer(new HoloDisplayTerminalChangePaket(menu.getBlockEntity()));
        }
    }

    private void selectModelMode() {
        this.modelButton.active = false;
        this.areaButton.active = true;
        this.trackerButton.active = true;
        unselectAreaMode();
        unSelectTrackerMode();
    }

    private void selectAreaMode() {
        this.modelButton.active = true;
        this.areaButton.active = false;
        this.trackerButton.active = true;
        unSelectTrackerMode();

        // 局部变量创建输入框
        int centerX = this.width / 2;
        int inputBoxWidth = 50;
        int inputBoxHeight = 20;
        xCoordInput = new EditBox(this.font, centerX - 55, 70, inputBoxWidth, inputBoxHeight, Component.literal("X1"));
        yCoordInput = new EditBox(this.font, centerX, 70, inputBoxWidth, inputBoxHeight, Component.literal("Y1"));
        zCoordInput = new EditBox(this.font, centerX + 55, 70, inputBoxWidth, inputBoxHeight, Component.literal("Z1"));

        xCoordInput2 = new EditBox(this.font, centerX - 55, 100, inputBoxWidth, inputBoxHeight, Component.literal("X2"));
        yCoordInput2 = new EditBox(this.font, centerX, 100, inputBoxWidth, inputBoxHeight, Component.literal("Y2"));
        zCoordInput2 = new EditBox(this.font, centerX + 55, 100, inputBoxWidth, inputBoxHeight, Component.literal("Z2"));

        this.addRenderableWidget(xCoordInput);
        this.addRenderableWidget(yCoordInput);
        this.addRenderableWidget(zCoordInput);
        this.addRenderableWidget(xCoordInput2);
        this.addRenderableWidget(yCoordInput2);
        this.addRenderableWidget(zCoordInput2);

        // 设置输入框的值
        BlockPos inputPos1 = menu.getBlockEntity().getStartCorner();
        BlockPos inputPos2 = menu.getBlockEntity().getEndCorner();

        xCoordInput.setValue(String.valueOf(inputPos1.getX()));
        yCoordInput.setValue(String.valueOf(inputPos1.getY()));
        zCoordInput.setValue(String.valueOf(inputPos1.getZ()));
        xCoordInput2.setValue(String.valueOf(inputPos2.getX()));
        yCoordInput2.setValue(String.valueOf(inputPos2.getY()));
        zCoordInput2.setValue(String.valueOf(inputPos2.getZ()));

        // 当用户点击确认按钮时，获取输入框的值
        confirmButton = new Button(centerX - 40, 130, 80, 20, Component.literal("Confirm"), button -> {
            try {
                int x1 = Integer.parseInt(xCoordInput.getValue());
                int y1 = Integer.parseInt(yCoordInput.getValue());
                int z1 = Integer.parseInt(zCoordInput.getValue());
                int x2 = Integer.parseInt(xCoordInput2.getValue());
                int y2 = Integer.parseInt(yCoordInput2.getValue());
                int z2 = Integer.parseInt(zCoordInput2.getValue());

                BlockPos pos1 = new BlockPos(x1, y1, z1);
                BlockPos pos2 = new BlockPos(x2, y2, z2);

                menu.getBlockEntity().setPosArea(pos1, pos2);
                menu.getBlockEntity().setMode(MODE.AREA);
                CHANNEL.sendToServer(new HoloDisplayTerminalChangePaket(menu.getBlockEntity()));
            } catch (NumberFormatException e) {
                xCoordInput.setValue(String.valueOf(inputPos1.getX()));
                yCoordInput.setValue(String.valueOf(inputPos1.getY()));
                zCoordInput.setValue(String.valueOf(inputPos1.getZ()));
                xCoordInput2.setValue(String.valueOf(inputPos2.getX()));
                yCoordInput2.setValue(String.valueOf(inputPos2.getY()));
                zCoordInput2.setValue(String.valueOf(inputPos2.getZ()));
            }
        });

        this.addRenderableWidget(confirmButton);
    }

    private void unselectAreaMode() {
        try {
            xCoordInput.visible = false;
            yCoordInput.visible = false;
            zCoordInput.visible = false;
            xCoordInput2.visible = false;
            yCoordInput2.visible = false;
            zCoordInput2.visible = false;
            confirmButton.visible = false;
        } catch (Exception e) {
        }
    }

    private void selectTrackerMode() {
        clearPlayerButtons();

        this.modelButton.active = true;
        this.areaButton.active = true;
        this.trackerButton.active = false;
        unselectAreaMode();
        unSelectTrackerMode();

        // 获取玩家列表
        List<String> playerNames = Minecraft.getInstance().level.players().stream()
                .map(player -> player.getName().getString())
                .collect(Collectors.toList());

        // 创建玩家按钮
        int centerX = this.width / 2;
        int buttonWidth = 100;
        int buttonHeight = 20;
        int buttonSpacing = 5;

        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            Button playerButton = new Button(centerX - buttonWidth / 2, 70 + i * (buttonHeight + buttonSpacing), buttonWidth, buttonHeight, Component.literal(playerName), button -> {
                menu.getBlockEntity().setMode(HoloDisplayTerminalBlockEntity.MODE.TRACKER);
                menu.getBlockEntity().setPlayerName(playerName);
                CHANNEL.sendToServer(new HoloDisplayTerminalChangePaket(menu.getBlockEntity()));
            });
            this.addRenderableWidget(playerButton);
            playerButtons.add(playerButton);
        }
    }

    private void unSelectTrackerMode() {
        clearPlayerButtons();
    }

    private void clearPlayerButtons() {
        for (Button button : this.playerButtons) {
            button.visible = false;  // 设置按钮为不可见
        }
        this.playerButtons = new ArrayList<>();
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.5F);
        fill(matrixStack, 0, 0, this.width, this.height, 0x80000000);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        renderBg(matrixStack, partialTicks, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, "Holographic Display Terminal", this.width / 2, 20, 0xFFFFFF);
        drawString(matrixStack, this.font, "Rotate", this.width / 2 - 80, 165, 0xFFFFFF);

        if (menu.getBlockEntity().getMode().equals(MODE.MODEL)) {
            Entity e = EntityHelper.getEntityByNBT(menu.getBlockEntity().getStoredEntityData(), menu.getBlockEntity().getLevel());
            if (e != null) {
                drawCenteredString(matrixStack, this.font, "Stored Entity: " + e.getDisplayName().getString(), this.width / 2, 60, 0xFFFFFF);
            } else {
                drawCenteredString(matrixStack, this.font, "Stored Entity: N/A", this.width / 2, 60, 0xFFFFFF);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
        // 不渲染默认的“物品栏”字样
    }
}
