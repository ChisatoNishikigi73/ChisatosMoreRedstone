package org.lycorecocafe.cmrs.client.gui;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class RadioSlider extends AbstractSliderButton {
    private final double minValue;
    private final double maxValue;
    private final Component prefix;
    private final ValueChangeListener valueChangeListener;

    public interface ValueChangeListener {
        void onValueChange(double value);
    }

    public RadioSlider(int x, int y, int width, int height, double minValue, double maxValue, Component prefix, ValueChangeListener valueChangeListener) {
        super(x, y, width, height, Component.empty(), 0.0D);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.prefix = prefix;
        this.valueChangeListener = valueChangeListener;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(prefix.getString() + ": " + String.format("%.1f", this.getHz()) + " Hz"));
    }

    @Override
    protected void applyValue() {
        if (valueChangeListener != null) {
            valueChangeListener.onValueChange(getHz());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            this.onDrag(mouseX, mouseY, 0.0, 0.0);
            return true;
        }
        return false;
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (this.active) {
            this.value = Mth.clamp((mouseX - (this.x + 4)) / (this.width - 8), 0.0, 1.0);
            this.updateMessage();
            this.applyValue();
        }
    }

    public void setHz(double hz) {
        this.value = Mth.clamp((hz - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
        this.updateMessage();
        this.applyValue();
    }

    public double getHz() {
        return Mth.lerp(this.value, this.minValue, this.maxValue);
    }
}
