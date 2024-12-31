package xyz.qweru.pulse.client.utils.render;

import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import java.awt.*;

public class AnimationUtil {
    private final long start;
    private final long end;
    private final TimerUtil timer = new TimerUtil();
    private boolean reset = false;
    private final boolean startAfterReset;

    public AnimationUtil(long start, long end) {
        this(start, end, false);
    }

    public AnimationUtil(long start, long end, boolean startAfterReset) {
        this.start = start;
        this.end = end;
        this.startAfterReset = startAfterReset;
        reset();
        reset = false;
    }

    public boolean hasEnded() {
        return timer.hasReached(end);
    }

    public void reset() {
        reset = true;
        timer.reset();
    }

    public Color getColor(Color startColor, Color endColor) {
        ColorUtil.PulseColor result = new ColorUtil.PulseColor(0, 0, 0, 0);
        result.setRed(getInt(startColor.getRed(), endColor.getRed()));
        result.setGreen(getInt(startColor.getGreen(), endColor.getGreen()));
        result.setBlue(getInt(startColor.getBlue(), endColor.getBlue()));
        result.setAlpha(endColor.getAlpha());
        return result.getColor();
    }

    public float getFloat(float startFloat, float endFloat) {
        if(startAfterReset && !reset) return endFloat;
        if(timer.getFromLast() <= start) return startFloat;
        if(timer.getFromLast() >= end) return endFloat;
        float diff = Math.abs(endFloat - startFloat);
        float multi = ((float) timer.getFromLast() / end);
        float res;
        if(endFloat > startFloat) {
            res = startFloat + (diff * multi);
        } else if(startFloat == endFloat) return startFloat;
        else {
            res = startFloat - (diff * multi);
        }
        return clamp(startFloat, endFloat, res);
    }

    public double getDouble(double start, double end) {
        return (double) getFloat((float) start, (float) end);
    }

    public int getInt(int startInt, int endInt) {
        return ((int) getFloat(startInt, endInt));
    }

    float clamp(float n0, float n1, float num) {
        float min;
        float max;
        if(n0 < n1) {
            min = n0;
            max = n1;
        } else if(n1 < n0) {
            min = n1;
            max = n0;
        } else {
            min = n0;
            max = n0;
        }
        if(num < min) return min;
        else return Math.min(num, max);
    }

    // see https://easings.net/en#

    /**
     * Example: (cubic ease-out) AnimationUtil.easeOut(n, 3)
     * @param n 0 - 1
     * @param pow power
     * @return converted value
     */
    public static double easeOutPow(double n, int pow) {
        return 1 - Math.pow(1 - n, pow);
    }

    /**
     * Circular ease-out
     * @param n 0 - 1
     * @return converted value
     */
    public static double easeOutCirc(double n) {
        return Math.sqrt(1 - Math.pow(n - 1, 2));
    }
}
