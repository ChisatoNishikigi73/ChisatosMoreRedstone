package org.lycorecocafe.cmrs.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lycorecocafe.cmrs.CMRS;
import org.lycorecocafe.cmrs.client.gui.RadioSlider;
import org.lycorecocafe.cmrs.client.gui.menu.SignalReceiverMenu;
import org.lycorecocafe.cmrs.network.SignalReceiverPacket;

import java.util.Objects;

//TODO: Maintain long-term consistency between Receiver and Transmitter (update frequency followed by updates)
//TODO: Single upstream mode and multiple upstream modes
public class SignalReceiverScreen extends AbstractContainerScreen<SignalReceiverMenu> {
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(CMRS.MODID, "textures/gui/signal_receiver.png");
    private EditBox inputBox;
    private Button increaseButton;
    private Button decreaseButton;
    private RadioSlider radioSlider;
//    private BlockPos blockPos;

    private boolean isUpdating = false;

    //TODO: Put it in static plz
    double minHz = 85.5;
    double maxHz = 135.5;

    public SignalReceiverScreen(SignalReceiverMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
//        this.blockPos =  menu.getBlockEntity().getBlockPos();
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

        Component titleText = Component.literal("Receiver FM");
        this.addRenderableWidget(new Button(centerX - 50, centerY - 60, 100, 20, titleText, button -> {})).active = false;

        if(menu.getBlockEntity().getEmitterPos() == null) {
            menu.getBlockEntity().setEmitterPos(menu.getBlockEntity().getBlockPos());
        }
        radioSlider.setHz(menu.getBlockEntity().getFrequency());
        updateInputFromSlider(radioSlider.getHz());
//        System.out.println(menu.getBlockEntity());
    }

    private boolean isValidHzInput(String input) {
        return input.matches("\\d*\\.?\\d{0,1}");
    }

    private void updateSliderFromInput(String input) {
        if (!isUpdating) {
            try {
                isUpdating = true;
                double hz = Double.parseDouble(input);
//                //TODO: handshake plz
//                if (!Objects.equals(hz, radioSlider.getHz())) {
//                    menu.getBlockEntity().setEmitterPos(menu.getBlockEntity().getBlockPos());
//                    CMRS.CHANNEL.sendToServer(new SignalReceiverPacket(menu.getBlockEntity().getBlockPos(), hz, menu.getBlockEntity().getEmitterPos()));
//                }
                radioSlider.setHz(hz);
                menu.getBlockEntity().setFrequency(hz);
                CMRS.CHANNEL.sendToServer(new SignalReceiverPacket(menu.getBlockEntity().getBlockPos(), hz, menu.getBlockEntity().getEmitterPos()));
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
//                if (!Objects.equals(hz, radioSlider.getHz())) {
//                    menu.getBlockEntity().setEmitterPos(menu.getBlockEntity().getBlockPos());
//                    CMRS.CHANNEL.sendToServer(new SignalReceiverPacket(menu.getBlockEntity().getBlockPos(), hz, menu.getBlockEntity().getEmitterPos()));
//                }
                this.inputBox.setValue(String.format("%.1f", hz));
                menu.getBlockEntity().setFrequency(hz);
                CMRS.CHANNEL.sendToServer(new SignalReceiverPacket(menu.getBlockEntity().getBlockPos(), hz, menu.getBlockEntity().getEmitterPos()));
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

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        this.inputBox.render(matrixStack, mouseX, mouseY, partialTicks);
        this.increaseButton.render(matrixStack, mouseX, mouseY, partialTicks);
        this.decreaseButton.render(matrixStack, mouseX, mouseY, partialTicks);
        this.radioSlider.render(matrixStack, mouseX, mouseY, partialTicks);
        if (menu.getBlockEntity().getEmitterPos() != null && !Objects.equals(menu.getBlockEntity().getEmitterPos(), menu.getBlockEntity().getBlockPos())) {
            drawCenteredString(matrixStack, this.font, "Upstream Emitter:", this.width / 2, this.height / 2 + 13, 4210752);
            drawCenteredString(matrixStack, this.font, menu.getBlockEntity().getEmitterPos().toShortString(), this.width / 2, this.height / 2 + 23, 4210752);
        }else {
            drawCenteredString(matrixStack, this.font, "Upstream Emitter:", this.width / 2, this.height / 2 + 13, 4210752);
            drawCenteredString(matrixStack, this.font, "N/A", this.width / 2, this.height / 2 + 23, 4210752);

        }
    }

    protected void renderLabels(PoseStack p_97808_, int p_97809_, int p_97810_) {
        this.font.draw(p_97808_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
    }
}
