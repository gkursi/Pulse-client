package xyz.qweru.pulse.client.systems.modules.settings.impl;

import net.minecraft.util.math.MathHelper;
import xyz.qweru.pulse.client.systems.modules.settings.Setting;
import xyz.qweru.pulse.client.utils.Util;

public class NumberSetting extends Setting {

    ValueModifierRunnable mod = (value -> value);

    public void setMin(float min) {
        this.min = min;
    }

    public void setMax(float max) {
        this.max = max;
    }

    private float min;
    private float max;
    private float defaultValue;
    private float currentValue;

    public NumberSetting(String name, String description, float min, float max, float defaultValue, boolean shouldShow) {
        super(name, description, shouldShow);
        this.max = max;
        this.min = min;
        this.defaultValue = defaultValue;
        this.currentValue = defaultValue;
    }

    public float getValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        this.currentValue = MathHelper.clamp(mod.get(currentValue), getMin(), getMax());
        if((currentValue + "").endsWith(".0")) this.currentValue = ((int) this.currentValue);
        onToggle();
    }
    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public void setValueModifier(ValueModifierRunnable mod) {
        this.mod = mod;
    }

    @FunctionalInterface
    public interface ValueModifierRunnable {
        float get(float value);
    }

    public int getValueInt() {
        return (int) currentValue;
    }

    public double getValueDouble() {
        return currentValue;
    }

    public long getValueLong() {
        return ((long) currentValue);
    }
}
