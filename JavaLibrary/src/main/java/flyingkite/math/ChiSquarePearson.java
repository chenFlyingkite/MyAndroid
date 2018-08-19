package flyingkite.math;

public class ChiSquarePearson {

    public static boolean acceptH0(DiscreteSample sample, int alphaOfTable) {
        double chiAlpha = ChiSquareTable.getChiTailArea(sample.size() - 1, alphaOfTable);
        return acceptH0(sample, chiAlpha);
    }

    /**
     * https://en.wikipedia.org/wiki/Pearson%27s_chi-squared_test#Testing_for_statistical_independence
     */
    public static boolean acceptH0(DiscreteSample sample, double chiAlpha) {
        double chi2 = getChiSquareValue(sample);
        return chi2 < chiAlpha;
    }

    public static double getChiSquareValue(DiscreteSample sample) {
        int m = sample.size(); // m category
        double[] expect = new double[m];

        int N = Math2.sum(sample.observe); // N observation

        // Evaluate Oij
        for (int i = 0; i < m; i++) {
            expect[i] = sample.pdf[i] * N;
        }

        // Evaluate test-statistic value
        double sum = 0;
        for (int i = 0; i < m; i++) {
            double dx = sample.observe[i] - expect[i];
            sum += dx * dx / expect[i];
        }
        return sum;
    }
}
