package flyingkite.library.java.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DiscreteSample {
    public double[] pdf;
    public double[] cdf;
    public int[] observe;
    public double[] observePdf;

    public DiscreteSample() {
        this(8);
    }

    public DiscreteSample(int n) {
        resize(n);
    }

    public void resize(int n) {
        pdf = new double[n];
        cdf = new double[n];
        observe = new int[n];
        observePdf = new double[n];
    }

    public int size() {
        return pdf.length;
    }

    public void setPdf(double[] p) {
        boolean sizeDiff = pdf.length != p.length;
        if (sizeDiff) {
            resize(p.length);
        }
        pdf = p;
        evalCdf();
    }

    public void evalCdf() {
        cdf[0] = pdf[0];
        for (int i = 1; i < size(); i++) {
            cdf[i] = cdf[i - 1] + pdf[i];
        }
    }

    public void normCdf() {
        double d = cdf[size() - 1];
        if (d == 0) return;

        for (int i = 0; i < size() - 1; i++) {
            cdf[i] /= d;
        }
        cdf[size() - 1] = 1;
    }

    public void drawSample(int n) {
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            double d = r.nextDouble();
            observe[getBin(d)]++;
        }
    }

    public List<Integer> randomSample(int n) {
        List<Integer> s = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            double d = r.nextDouble();
            s.add(getBin(d));
        }
        return s;
    }

    public void evalObservePdf() {
        int n = Math2.sum(observe);
        for (int i = 0; i < size(); i++) {
            observePdf[i] = 1F * observe[i] / n;
        }
    }

    public void clearSample() {
        Arrays.fill(observe, 0);
        Arrays.fill(observePdf, 0);
    }

    private int getBin(double cdfValue) {
        if (MathUtil.isInRange(cdfValue, 0, cdf[0])) {
            return 0;
        }
        for (int i = 1; i < size(); i++) {
            if (MathUtil.isInRange(cdfValue, cdf[i - 1], cdf[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        int n = size();
        int sn = Math2.sum(observe);
        StringBuilder sb = new StringBuilder()
                .append(n).append(" types, ").append(sn).append(" draws :")
                .append("\npdf = ").append(Arrays.toString(pdf))
                .append("\nsamples = ").append(Arrays.toString(observe))
                .append("\nsample pdf = ").append(Arrays.toString(observePdf));

        return sb.toString();
    }
}
