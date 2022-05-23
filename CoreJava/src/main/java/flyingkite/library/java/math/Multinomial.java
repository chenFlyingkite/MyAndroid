package flyingkite.library.java.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import flyingkite.library.java.log.L;
import flyingkite.library.java.log.Loggable;

/**
 *
 * https://en.wikipedia.org/wiki/Multinomial_distribution
 *
 */
public class Multinomial {

    /**
     * Returns the probability density function
     * of x1, ..., xk & p1, ..., pk
     *          n!
     * = ---------------- * p1^x1 * ... * pk^xk
     *   x1! * ... * xk!
     */
    public static BigDecimal pdf(int[] xi, double[] pi) {
        int k = xi.length;

        BigDecimal d = new BigDecimal(nCx(xi));
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < xi[i]; j++) {
                d = d.multiply(BigDecimal.valueOf(pi[i]));
            }
        }

        return d;
    }

    /**
     * Returns multinomial of (x1, x2, ..., xk), n = x1 + ... + xk
     *           n!
     * = ----------------
     *    x1! * ... * xk!
     */
    public static BigInteger nCx(int[] xi) {
        int k = xi.length;
        int n = Math2.sum(xi);
        BigDecimal ans = BigDecimal.ONE;
        // List values we will multiply
        int[] nf = new int[n]; // = [1:n]
        int[] xf = new int[n]; // = [1:x1], ..., [1:xk]
        int m = 0; // = 1 ~ n
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < xi[i]; j++) {
                nf[m] = m + 1;
                xf[m] = j + 1;
                m++;
            }
        }
        // Multiply them
        BigDecimal nj;
        BigDecimal xj;
        for (int i = 0; i < n; i++) {
            nj = BigDecimal.valueOf(nf[i]);
            xj = BigDecimal.valueOf(xf[i]);
            ans = ans.multiply(nj).divide(xj, BigDecimal.ROUND_HALF_UP);
        }
        return ans.toBigInteger();
    }
//
//    public static BigInteger nCx2(int[] xi) {
//        int n = Math2.sum(xi);
//        BigInteger ans = Math2.factorial(n); // n!
//        for (int i = 0; i < xi.length; i++) {
//            ans = ans.divide(Math2.factorial(xi[i])); // xi!
//        }
//        return ans;
//    }

    private static <T> void printList(T[] a) {
        int n = a == null ? 0 : a.length;
        z.log("%s items -> %s", n, Arrays.toString(a));
    }

    private static void printList(int[] a) {
        int n = a == null ? 0 : a.length;
        z.log("%s items -> %s", n, Arrays.toString(a));
    }
    private static void printList(double[] a) {
        int n = a == null ? 0 : a.length;
        z.log("%s items -> %s", n, Arrays.toString(a));
    }

    private static Loggable z = new Loggable() {
        @Override
        public void log(String msg) {
            L.log(msg);
        }
    };

    public static void test() {
        pdf(new int[]{1, 2, 3, 4, 5}, new double[]{.1, .2, .3, .4, .5});
        int[] xi = {1, 1, 1};
        double[] pi = {1, 1, 1};
        xi = new int[]{5, 10};
        pi = new double[]{1, 1};
        xi = new int[]{1, 2, 3};
        pi = new double[]{0.2, 0.3, 0.5};
        L.log("->" + pdf(xi, pi));
        L.log("->" + nCx(xi));

        DiscreteSample smp = new DiscreteSample(8);
        smp.pdf[0] = .025;
        for (int i = 1; i <= 2; i++) {
            smp.pdf[i] = .100;
        }
        for (int i = 3; i <= 7; i++) {
            smp.pdf[i] = .155;
        }
        smp.evalCdf();
        L.log("pdf");
        Multinomial.printList(smp.pdf);
        //L.log("cdf");
        //printList(smp.cdf);
        int n = 10;
        smp.drawSample(n);
        L.log("sample");
        printList(smp.observe);
        double alpha = ChiSquareTable.getChiTailArea(smp.size(), ChiSquareTable._100);
        boolean x = ChiSquarePearson.acceptH0(smp, alpha);
        L.log("accept = %s", x);

        for (int i = 0; i < 30; i++) {
            smp.clearSample();
            smp.drawSample(n);
            smp.evalObservePdf();
            printList(smp.observe);
            printList(smp.observePdf);
            alpha = ChiSquareTable.getChiTailArea(smp.size(), ChiSquareTable._100);
            x = ChiSquarePearson.acceptH0(smp, alpha);
            L.log("accept = %s", x);

        }
    }
}
