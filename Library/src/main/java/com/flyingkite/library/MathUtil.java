package com.flyingkite.library;

public class MathUtil {
    private MathUtil() {}

    // Just copy codes as it is done in Arrays#sort()
    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max;
    }

    public static boolean isInRange(double value, double min, double max) {
        return min <= value && value < max;
    }

    // Just copy codes as it is done in Arrays#sort()
    public static long makeInRange(long value, long min, long max) {
        return Math.min(Math.max(min, value), max);
    }

    public static double makeInRange(double value, double min, double max) {
        return Math.min(Math.max(min, value), max);
    }
}
