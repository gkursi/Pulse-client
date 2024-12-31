package xyz.qweru.pulse.client.utils.render;

import xyz.qweru.pulse.client.utils.annotations.Status;

import java.awt.*;

public class ColorUtil {

    public static PulseColor add(PulseColor a, PulseColor b) {
        return new PulseColor(Math.min(a.r + b.r, 255), Math.min(a.g + b.g, 255), Math.min(a.b + b.b, 255));
    }

    /**
     * Subtract b from a
     * @param a color a
     * @param b color b
     * @return result
     */
    public static PulseColor subtract(PulseColor a, PulseColor b) {
        return new PulseColor(Math.max(a.r - b.r, 0), Math.max(a.g - b.g, 0), Math.max(a.b - b.b, 0));
    }

    static int invert(int color) {
        return color ^ 0x00ffffff;
    }

    public static PulseColor invert(PulseColor c) {
        int originAlpha = c.getAlpha();
        int colRGB = c.getColor().getRGB();
        PulseColor invCol = new PulseColor(invert(colRGB));
        invCol.setAlpha(originAlpha);
        return invCol;
    }

    public static enum GradientMode {
        DARK_TO_LIGHT,
        LIGHT_TO_DARK,
        INVERT_TO_NORMAL,
        NORMAL_TO_INVERT
    }

    public static class PulseColor {
        int r, g, b, a = 255;
        public PulseColor(Color color) {
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public PulseColor(int r, int g, int b, int a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public PulseColor(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public PulseColor(int rgb) {
            Color color = new Color(rgb);
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public int getRed() {
            return r;
        }

        public int getGreen() {
            return g;
        }

        public int getBlue() {
            return b;
        }

        public int getAlpha() {
            return a;
        }

        public void setAlpha(int a) {
            this.a = a;
        }

        public void setBlue(int b) {
            this.b = b;
        }

        public void setGreen(int g) {
            this.g = g;
        }

        public void setRed(int r) {
            this.r = r;
        }

        public void setColor(Color color) {
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            a = color.getAlpha();
        }

        public Color getColor() {
            return new Color(r, g, b, a);
        }

        public int getRGB() {
            return getColor().getRGB();
        }
    }
}
