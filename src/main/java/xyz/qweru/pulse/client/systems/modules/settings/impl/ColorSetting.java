package xyz.qweru.pulse.client.systems.modules.settings.impl;

import xyz.qweru.pulse.client.systems.modules.settings.Setting;

import java.awt.*;

public class ColorSetting extends Setting {
    private int red;
    private int green;
    private int blue;
    private int alpha;

    public ColorSetting(String name, String description, boolean shouldShow, int red, int green, int blue, int alpha) {
        super(name, description, shouldShow);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setColor(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public void setColor(float red, float green, float blue, float alpha) {
        this.red = (int) (255 * red);
        this.green = (int) (255 * green);
        this.blue = (int) (255 * blue);
        this.alpha = (int) (255 * alpha);
    }

    public Color getJavaColor() {
        return new Color(red, green, blue, alpha);
    }

    public RGBValue getRed() {
        return new RGBValue(red);
    }

    public RGBValue getGreen() {
        return new RGBValue(green);
    }

    public RGBValue getBlue() {
        return new RGBValue(blue);
    }

    public RGBValue getAlpha() {
        return new RGBValue(alpha);
    }

    /**
     * Color helper class
     */
    public static class RGBValue {
        private final int value;

        RGBValue(int value) {
            this.value = value;
        }

        public int asInt() {
            return value;
        }

        public float asFloat() {
            return (float) value / 255;
        }
    }
}
