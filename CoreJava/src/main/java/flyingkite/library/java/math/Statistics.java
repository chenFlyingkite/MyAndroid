package flyingkite.library.java.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flyingkite.library.java.data.IIPair;
import flyingkite.library.java.log.L;

public class Statistics {
    /**
     * https://en.wikipedia.org/wiki/Mean
     */
    public static <T extends Number> double mean(List<T> data) {
        double mean = 0;
        int n = data.size();
        for (T d : data) {
            mean += d.doubleValue() / n;
        }
        return mean;
    }

    /**
     * variance of Sample
     * https://en.wikipedia.org/wiki/Variance#Sample_variance
     * @param sample true if sample, false if population
     */
    public static <T extends Number> double variance(List<T> data, boolean sample) {
        // μ = mean
        final double mean = mean(data);
        double var = 0, dx;
        int n = data.size();
        int div = sample ? n - 1 : n;
        for (T d : data) {
            // Sum up (x_i - μ)^2
            dx = d.doubleValue() - mean;
            var += dx * dx / div;
        }
        return var;
    }

    /**
     * deviation of sample
     * @param sample true if sample, false if population
     */
    public static <T extends Number> double deviation(List<T> data, boolean sample) {
        return Math.sqrt(variance(data, sample));
    }

    /**
     * Skewness of sample
     * https://en.wikipedia.org/wiki/Skewness
     *           m_3     1 / n * Sum_i=1:n {(x_i - μ)^3}
     * gamma1 = ----- = ------------------------------------------------------
     *           s^3     sqrt(1 / (n - 1) * ( Sum_i=1:n {(x_i - μ)^2} ) ) ^ 3
     *
     */
    public static <T extends Number> double skewness(List<T> data) {
        double mean = mean(data);
        double std = deviation(data);
        double s3 = std * std * std;

        double m3 = 0, dx;
        for (T d : data) {
            // Sum up (x_i - μ)^3
            dx = d.doubleValue() - mean;
            m3 += dx * dx * dx;
        }
        m3 /= data.size();

        return m3 / s3;
    }

    /**
     * variance of Sample
     * https://en.wikipedia.org/wiki/Variance#Sample_variance
     */
    public static <T extends Number> double variance(List<T> data) {
        return variance(data, true);
    }

    /**
     * deviation of sample
     */
    public static <T extends Number> double deviation(List<T> data) {
        return Math.sqrt(variance(data, true));
    }



    public static <T extends Comparable<T>> T min(List<T> data) {
        return Collections.min(data);
    }

    public static <T extends Comparable<T>> T max(List<T> data) {
        return Collections.max(data);
    }

    /**
     * https://en.wikipedia.org/wiki/Median
     */
    public static <T extends Number> double median(List<T> data) {
        List<Double> sorted = sortedDouble(data);
        return mid(sorted);
    }

    private static <T extends Number> List<Double> sortedDouble(List<T> data) {
        // Sort on double value
        List<Double> sorted = new ArrayList<>();
        for (T t : data) {
            sorted.add(t.doubleValue());
        }
        Collections.sort(sorted);
        return sorted;
    }

    private static double mid(List<Double> sorted) {
        // size = 2 * q + r, 0 <= r < 2
        int size = sorted.size();
        int q = size / 2;
        int r = size % 2;
        if (r == 1) {
            return sorted.get(q);
        } else {
            return 0.5 * sorted.get(q - 1) + 0.5 * sorted.get(q);
        }
    }

    /**
     * https://en.wikipedia.org/wiki/Median
     */
    public static <T extends Number> double quartile_1(List<T> data) {
        List<Double> sorted = sortedDouble(data);
        int n = sorted.size();
        return mid(sorted.subList(0, (n + 1) / 2));
    }

    /**
     * https://en.wikipedia.org/wiki/Median
     */
    public static <T extends Number> double quartile_3(List<T> data) {
        List<Double> sorted = sortedDouble(data);
        int n = sorted.size();
        return mid(sorted.subList((n + 1) / 2, n));
    }

    /**
     * Returns the indices of mode in data, (we returns the index of item)
     * Use data[result[0]] to take the 1st mode number
     */
    public static <T extends Number> List<Integer> modeIndices(List<T> data) {
        Map<T, IIPair> map = new HashMap<>();
        // Count the set as {value -> (index, count), ...}
        IIPair p;
        for (int i = 0; i < data.size(); i++) {
            T d = data.get(i);
            if (map.containsKey(d)) {
                p = map.get(d);
                p.setV(p.getV() + 1);
            } else {
                p = new IIPair(i, 1);
            }
            map.put(d, p);
        }
        // Sort (index, count) in map
        List<IIPair> counts = new ArrayList<>(map.values());
        Collections.sort(counts);
        // Take those count is max as indices
        int max = counts.get(counts.size() - 1).getV();
        List<Integer> indices = new ArrayList<>();
        for (IIPair pair : counts) {
            if (max == pair.getV()) {
                indices.add(pair.getK());
            }
        }
        return indices;
    }

    public static void run() {
        double x;
        List<Integer> y;
        x = mean(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", x);
        x = variance(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", x);
        x = deviation(Arrays.asList(1, 2, 3, 4, 5, 6));
        L.log("x = %s", x);
        x = mean(Arrays.asList(1.5, 2.5, 3.5, 4.5, 5.5, 6.5));
        L.log("x = %s", x);
        y = modeIndices(Arrays.asList(1, 2, 3, 4, 5, 6, 6));
        L.log("y = %s", y);
        y = modeIndices(Arrays.asList(-1, -1, -1, -1, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 4, 5, 6, 6));
        L.log("y = %s", y);
        // https://zh.wikipedia.org/wiki/%E5%9B%9B%E5%88%86%E4%BD%8D%E6%95%B0
        x = median(Arrays.asList(6, 47, 49, 15, 42, 41, 7, 39, 43, 40, 36));
        L.log("x = %s", x);
        x = median(Arrays.asList(7, 15, 36, 39, 40, 41));
        L.log("x = %s", x);
    }
}
