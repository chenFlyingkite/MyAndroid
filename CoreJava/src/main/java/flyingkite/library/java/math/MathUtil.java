package flyingkite.library.java.math;

public class MathUtil {
    /**
     * @return true if min &le; value &lt; max, false otherwise
     */
    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max;
    }

    /**
     * @see #isInRange(long, long, long)
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
    public static int makeInRange(int value, int min, int max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * @see #makeInRange(int, int, int)
     */
    public static long makeInRange(long value, long min, long max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * @see #makeInRange(int, int, int)
     */
    public static float makeInRange(float value, float min, float max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * @see #makeInRange(int, int, int)
     */
    public static double makeInRange(double value, double min, double max) {
        return Math.min(Math.max(min, value), max);
    }
    // For mins & maxs uses Collections.min() & Collections.max()

    public static int mins(int... values) {
        int min = values[0];
        for (int i = 1; i < values.length; i++) {
            if (min > values[i]) {
                min = values[i];
            }
        }
        return min;
    }

    public static long mins(long... values) {
        long min = values[0];
        for (int i = 1; i < values.length; i++) {
            if (min > values[i]) {
                min = values[i];
            }
        }
        return min;
    }

    public static double mins(double... values) {
        double min = values[0];
        for (int i = 1; i < values.length; i++) {
            if (min > values[i]) {
                min = values[i];
            }
        }
        return min;
    }

    public static int maxs(int... values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (max < values[i]) {
                max = values[i];
            }
        }
        return max;
    }

    public static long maxs(long... values) {
        long max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (max < values[i]) {
                max = values[i];
            }
        }
        return max;
    }

    public static double maxs(double... values) {
        double max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (max < values[i]) {
                max = values[i];
            }
        }
        return max;
    }
}
