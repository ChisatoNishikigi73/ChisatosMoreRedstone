package org.lycorecocafe.cmrs.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.client.gui.RadioSlider;
import org.lycorecocafe.cmrs.client.gui.menu.SignalEmitterMenu;
import org.lycorecocafe.cmrs.network.ApplySignalPaket;
import org.lycorecocafe.cmrs.network.ClearSignalPaket;
import org.lycorecocafe.cmrs.network.SignalEmitterPacket;
import org.lycorecocafe.cmrs.utils.game.SignalFinder;
import org.lycorecocafe.cmrs.utils.game.result.PositionsComparisonResult;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SignalEmitterScreen extends AbstractContainerScreen<SignalEmitterMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(CMRS.MODID, "textures/gui/signal_emitter.png");
    double minHz = 85.5;
    double maxHz = 135.5;
    private EditBox inputBox;
    private Button sendButton;
    private EditBox rangeInputBox;
    private Button increaseButton;
    private Button decreaseButton;
    private RadioSlider radioSlider;
    private boolean isUpdating = false;
    private int range = 128;

    public SignalEmitterScreen(SignalEmitterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.inputBox = new EditBox(this.font, centerX - 50, centerY - 35, 100, 20, Component.literal(""));
        this.inputBox.setFilter(this::isValidHzInput);
        this.inputBox.setResponder(this::updateSliderFromInput);
        this.addRenderableWidget(this.inputBox);

        this.increaseButton = new Button(centerX + 60, centerY - 10, 20, 20, Component.literal("+"), button -> {
            double currentHz = radioSlider.getHz();
            setSliderHz(currentHz + 1.5);
        });
        this.addRenderableWidget(this.increaseButton);

        this.decreaseButton = new Button(centerX - 80, centerY - 10, 20, 20, Component.literal("-"), button -> {
            double currentHz = radioSlider.getHz();
            setSliderHz(currentHz - 1.5);
        });
        this.addRenderableWidget(this.decreaseButton);

        this.radioSlider = new RadioSlider(centerX - 50, centerY - 10, 100, 20, minHz, maxHz, Component.literal("F"), this::updateInputFromSlider);
        this.addRenderableWidget(this.radioSlider);

        this.sendButton = new Button(centerX - 50, centerY + 25, 100, 20, Component.literal("Find Receiver"), button -> {
            List<BlockPos> positions = SignalFinder.findReceiversInRange(this.menu.getBlockEntity(), range);

            PositionsComparisonResult result = new PositionsComparisonResult().compare(positions, menu.getBlockEntity().getMatchReceivers());
            if (!result.getRemoved().isEmpty()) {
                CMRS.CHANNEL.sendToServer(new ClearSignalPaket(menu.getBlockEntity().getBlockPos(), result.getRemoved()));
            }

            menu.getBlockEntity().setMatchReceivers(positions);
            SignalFinder.applySignal(menu.getBlockEntity(), positions);
            CMRS.CHANNEL.sendToServer(new ApplySignalPaket(menu.getBlockEntity().getBlockPos(), positions));
            CMRS.CHANNEL.sendToServer(new SignalEmitterPacket(menu.getBlockEntity().getBlockPos(), radioSlider.getHz(), menu.getBlockEntity().getMatchReceivers()));

        });

        this.rangeInputBox = new EditBox(this.font, centerX + 52, centerY + 25, 28, 20, Component.literal("128"));
        this.rangeInputBox.setFilter(this::isValidHzInput);
        this.rangeInputBox.setResponder(this::setRange);
        this.addRenderableWidget(this.rangeInputBox);

        this.addRenderableWidget(this.sendButton);

        Component titleText = Component.literal("Emitter FM");
        this.addRenderableWidget(new Button(centerX - 50, centerY - 60, 100, 20, titleText, button -> {
        })).active = false;

        ////TODO: BlaBlaBlaBlaBla I have no idea what im doing
        radioSlider.setHz(getFrequency());
        updateInputFromSlider(radioSlider.getHz());
        this.rangeInputBox.setValue(String.valueOf(range));
    }

    private boolean isValidHzInput(String input) {
        return input.matches("\\d*\\.?\\d{0,1}");
    }

    private void setRange(String input) {
        try {
            this.range = Integer.parseInt(input);

        } catch (NumberFormatException e) {
            this.range = 128;
        }
    }

    private void updateSliderFromInput(String input) {
        if (!isUpdating) {
            try {
                isUpdating = true;
                double hz = Double.parseDouble(input);
                radioSlider.setHz(hz);
                setFrequency(hz);
                CMRS.CHANNEL.sendToServer(new SignalEmitterPacket(menu.getBlockEntity().getBlockPos(), hz, menu.getBlockEntity().getMatchReceivers()));
            } catch (NumberFormatException e) {
                // Handle invalid input if necessary
            } finally {
                isUpdating = false;
            }
        }
    }

    private void updateInputFromSlider(double hz) {
        if (!isUpdating) {
            try {
                isUpdating = true;
                setFrequency(hz);
                CMRS.CHANNEL.sendToServer(new SignalEmitterPacket(menu.getBlockEntity().getBlockPos(), hz, menu.getBlockEntity().getMatchReceivers()));
            } finally {
                isUpdating = false;
            }
        }
    }

    private void setSliderHz(double hz) {
        this.radioSlider.setHz(hz);
        this.updateInputFromSlider(radioSlider.getHz());
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }

    private double getFrequency() {
        return this.menu.getBlockEntity().getFrequency();
    }

    private void setFrequency(double hz) {
        this.menu.getBlockEntity().setFrequency(hz);
        this.inputBox.setValue(String.format("%.1f", hz));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
//        this.inputBox.render(matrixStack, mouseX, mouseY, partialTicks);
//        this.sendButton.render(matrixStack, mouseX, mouseY, partialTicks);
//        this.rangeInputBox.render(matrixStack, mouseX, mouseY, partialTicks);
//        this.increaseButton.render(matrixStack, mouseX, mouseY, partialTicks);
//        this.decreaseButton.render(matrixStack, mouseX, mouseY, partialTicks);
//        this.radioSlider.render(matrixStack, mouseX, mouseY, partialTicks);
//        drawString(matrixStack, this.font, "已匹配" + menu.getBlockEntity().getMatchReceivers().size() + "个接收器", this.width / 2 - 50, this.height / 2 + 13, 4210752);
        drawString(matrixStack, this.font, "Matched " + menu.getBlockEntity().getMatchReceivers().size() + " Receiver", this.width / 2 - 50, this.height / 2 + 13, 42107521);
    }

    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
        this.font.draw(p_97808_, this.title, (float) this.titleLabelX, (float) this.titleLabelY, 4210752);
    }
}
