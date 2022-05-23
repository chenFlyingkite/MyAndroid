package flyingkite.library.java.math;

import java.util.HashMap;
import java.util.Map;

/**
 * http://kisi.deu.edu.tr/joshua.cowley/Chi-square-table.pdf
 */
public class ChiSquareTable {
    // Chi Square's alpha value index
    public static final int _995 = 0;
    public static final int _990 = 1;
    public static final int _975 = 2;
    public static final int _950 = 3;
    public static final int _900 = 4;
    public static final int _100 = 5;
    public static final int _050 = 6;
    public static final int _025 = 7;
    public static final int _010 = 8;
    public static final int _005 = 9;

    private static final Map<Integer, Double[]> table = new HashMap<>();

    /**
     * Returns &chi;2-alpha in table, -1 if not found
     */
    public static double getChiTailArea(int free, int alpha) {
        Double[] row = getChiTableRow(free);
        if (row != null) {
            if (MathUtil.isInRange(alpha, 0, row.length)) {
                return row[alpha];
            }
        }
        return -1;
    }

    /**
     * Returns the &chi;2-alpha
     * Free = degree of freedom
     * https://en.wikipedia.org/wiki/Degrees_of_freedom_(statistics)
     */
    public static Double[] getChiTableRow(int free) {
        return table.get(free);
    }

    static {
        // df = degree of freedom
        // Chi's  [df]    & alpha      _995,    _990,    _975,    _950,    _900,    _100,    _050,    _025,    _010,    _005
        table.put(  1, new Double[]{  0.000,   0.000,   0.001,   0.004,   0.016,   2.706,   3.841,   5.024,   6.635,   7.879});
        table.put(  2, new Double[]{  0.010,   0.020,   0.051,   0.103,   0.211,   4.605,   5.991,   7.378,   9.210,  10.597});
        table.put(  3, new Double[]{  0.072,   0.115,   0.216,   0.352,   0.584,   6.251,   7.815,   9.348,  11.345,  12.838});
        table.put(  4, new Double[]{  0.207,   0.297,   0.484,   0.711,   1.064,   7.779,   9.488,  11.143,  13.277,  14.860});
        table.put(  5, new Double[]{  0.412,   0.554,   0.831,   1.145,   1.610,   9.236,  11.070,  12.833,  15.086,  16.750});
        table.put(  6, new Double[]{  0.676,   0.872,   1.237,   1.635,   2.204,  10.645,  12.592,  14.449,  16.812,  18.548});
        table.put(  7, new Double[]{  0.989,   1.239,   1.690,   2.167,   2.833,  12.017,  14.067,  16.013,  18.475,  20.278});
        table.put(  8, new Double[]{  1.344,   1.646,   2.180,   2.733,   3.490,  13.362,  15.507,  17.535,  20.090,  21.955});
        table.put(  9, new Double[]{  1.735,   2.088,   2.700,   3.325,   4.168,  14.684,  16.919,  19.023,  21.666,  23.589});
        table.put( 10, new Double[]{  2.156,   2.558,   3.247,   3.940,   4.865,  15.987,  18.307,  20.483,  23.209,  25.188});
        table.put( 11, new Double[]{  2.603,   3.053,   3.816,   4.575,   5.578,  17.275,  19.675,  21.920,  24.725,  26.757});
        table.put( 12, new Double[]{  3.074,   3.571,   4.404,   5.226,   6.304,  18.549,  21.026,  23.337,  26.217,  28.300});
        table.put( 13, new Double[]{  3.565,   4.107,   5.009,   5.892,   7.042,  19.812,  22.362,  24.736,  27.688,  29.819});
        table.put( 14, new Double[]{  4.075,   4.660,   5.629,   6.571,   7.790,  21.064,  23.685,  26.119,  29.141,  31.319});
        table.put( 15, new Double[]{  4.601,   5.229,   6.262,   7.261,   8.547,  22.307,  24.996,  27.488,  30.578,  32.801});
        table.put( 16, new Double[]{  5.142,   5.812,   6.908,   7.962,   9.312,  23.542,  26.296,  28.845,  32.000,  34.267});
        table.put( 17, new Double[]{  5.697,   6.408,   7.564,   8.672,  10.085,  24.769,  27.587,  30.191,  33.409,  35.718});
        table.put( 18, new Double[]{  6.265,   7.015,   8.231,   9.390,  10.865,  25.989,  28.869,  31.526,  34.805,  37.156});
        table.put( 19, new Double[]{  6.844,   7.633,   8.907,  10.117,  11.651,  27.204,  30.144,  32.852,  36.191,  38.582});
        table.put( 20, new Double[]{  7.434,   8.260,   9.591,  10.851,  12.443,  28.412,  31.410,  34.170,  37.566,  39.997});
        table.put( 21, new Double[]{  8.034,   8.897,  10.283,  11.591,  13.240,  29.615,  32.671,  35.479,  38.932,  41.401});
        table.put( 22, new Double[]{  8.643,   9.542,  10.982,  12.338,  14.041,  30.813,  33.924,  36.781,  40.289,  42.796});
        table.put( 23, new Double[]{  9.260,  10.196,  11.689,  13.091,  14.848,  32.007,  35.172,  38.076,  41.638,  44.181});
        table.put( 24, new Double[]{  9.886,  10.856,  12.401,  13.848,  15.659,  33.196,  36.415,  39.364,  42.980,  45.559});
        table.put( 25, new Double[]{ 10.520,  11.524,  13.120,  14.611,  16.473,  34.382,  37.652,  40.646,  44.314,  46.928});
        table.put( 26, new Double[]{ 11.160,  12.198,  13.844,  15.379,  17.292,  35.563,  38.885,  41.923,  45.642,  48.290});
        table.put( 27, new Double[]{ 11.808,  12.879,  14.573,  16.151,  18.114,  36.741,  40.113,  43.195,  46.963,  49.645});
        table.put( 28, new Double[]{ 12.461,  13.565,  15.308,  16.928,  18.939,  37.916,  41.337,  44.461,  48.278,  50.993});
        table.put( 29, new Double[]{ 13.121,  14.256,  16.047,  17.708,  19.768,  39.087,  42.557,  45.722,  49.588,  52.336});
        table.put( 30, new Double[]{ 13.787,  14.953,  16.791,  18.493,  20.599,  40.256,  43.773,  46.979,  50.892,  53.672});
        table.put( 40, new Double[]{ 20.707,  22.164,  24.433,  26.509,  29.051,  51.805,  55.758,  59.342,  63.691,  66.766});
        table.put( 50, new Double[]{ 27.991,  29.707,  32.357,  34.764,  37.689,  63.167,  67.505,  71.420,  76.154,  79.490});
        table.put( 60, new Double[]{ 35.534,  37.485,  40.482,  43.188,  46.459,  74.397,  79.082,  83.298,  88.379,  91.952});
        table.put( 70, new Double[]{ 43.275,  45.442,  48.758,  51.739,  55.329,  85.527,  90.531,  95.023, 100.425, 104.215});
        table.put( 80, new Double[]{ 51.172,  53.540,  57.153,  60.391,  64.278,  96.578, 101.879, 106.629, 112.329, 116.321});
        table.put( 90, new Double[]{ 59.196,  61.754,  65.647,  69.126,  73.291, 107.565, 113.145, 118.136, 124.116, 128.299});
        table.put(100, new Double[]{ 67.328,  70.065,  74.222,  77.929,  82.358, 118.498, 124.342, 129.561, 135.807, 140.169});
    }
}
