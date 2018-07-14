package com.flyingkite.library.util;

public class MathUtil {
    private MathUtil() {}

    /**
     * @return true if min &le; value &lt; max, false otherwise
     */
    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max;
    }

    /**
     * @return true if min &le; value &lt; max, false otherwise
     */
    public static boolean isInRange(double value, double min, double max) {
        return min <= value && value < max;
    }

    /**
     * Returns the value clamped by [min, max]
     * @return value itself if min &le; value &lt; max.
     * Returns min if value &lt; min.
     * Returns max if value &ge; max.
     */
    public static long makeInRange(long value, long min, long max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Returns the value clamped by [min, max]
     * @return value itself if min &le; value &lt; max.
     * Returns min if value &lt; min.
     * Returns max if value &ge; max.
     */
    public static double makeInRange(double value, double min, double max) {
        return Math.min(Math.max(min, value), max);
    }
}
