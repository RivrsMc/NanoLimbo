package ua.nanit.limbo.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class MathUtil {

    public static int clamp(int value, int min, int max) {
        return value < min ? min : Math.min(value, max);
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }

    public static int floor(double value) {
        int temp = (int) value;
        return value < (double) temp ? temp - 1 : temp;
    }

    public static int floor(float value) {
        int temp = (int) value;
        return value < (float) temp ? temp - 1 : temp;
    }
}