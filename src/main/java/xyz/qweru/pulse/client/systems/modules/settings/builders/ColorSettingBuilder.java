package xyz.qweru.pulse.client.systems.modules.settings.builders;

import xyz.qweru.pulse.client.systems.modules.settings.impl.ColorSetting;

import java.awt.*;

public class ColorSettingBuilder {
    private String name;
    private String description;
    private boolean shouldShow = true;
    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private int alpha = 255;

    public ColorSettingBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ColorSettingBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ColorSettingBuilder setShouldShow(boolean shouldShow) {
        this.shouldShow = shouldShow;
        return this;
    }

    public ColorSettingBuilder setRed(int red) {
        this.red = red;
        return this;
    }

    public ColorSettingBuilder setGreen(int green) {
        this.green = green;
        return this;
    }

    public ColorSettingBuilder setBlue(int blue) {
        this.blue = blue;
        return this;
    }

    public ColorSettingBuilder setAlpha(int alpha) {
        this.alpha = alpha;
        return this;
    }

    public ColorSettingBuilder setColor(Color color) {
        this.alpha = color.getAlpha();
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        return this;
    }

    public ColorSettingBuilder setInfo(String name, String description) { this.setName(name); this.setDescription(description); return this; }

    public ColorSetting build() {
        return new ColorSetting(name, description, shouldShow, red, green, blue, alpha);
    }
}