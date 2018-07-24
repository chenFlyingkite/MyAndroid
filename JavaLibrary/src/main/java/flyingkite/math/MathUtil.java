package flyingkite.math;

public final class MathUtil {
    // Greek alphabet
    // https://en.wikipedia.org/wiki/Greek_alphabet
    // Α α alpha, άλφα
    // Β β beta, βήτα
    // Γ γ gamma, γάμμα
    // Δ δ delta, δέλτα
    // Ε ε epsilon, έψιλον
    // Ζ ζ zeta, ζήτα
    // Η η eta, ήτα
    // Θ θ theta, θήτα
    // Ι ι iota, ιώτα
    // Κ κ kappa, κάππα
    // Λ λ lambda, λάμδα
    // Μ μ mu, μυ
    // Ν ν nu, νυ
    // Ξ ξ xi, ξι
    // Ο ο omicron, όμικρον
    // Π π pi, πι
    // Ρ ρ rho, ρώ
    // Σ σ/ς[note 1] sigma, σίγμα
    // Τ τ tau, ταυ
    // Υ υ upsilon, ύψιλον
    // Φ φ phi, φι
    // Χ χ chi, χι
    // Ψ ψ psi, ψι
    // Ω ω omega, ωμέγα


    public static boolean isInRange(long value, long min, long max) {
        return min <= value && value < max;
    }

    public static int mins(int... values) {
        int min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }
        return min;
    }
    public static double mins(double... values) {
        double min = values[0];
        for (int i = 1; i < values.length; i++) {
            min = Math.min(min, values[i]);
        }
        return min;
    }

    public static int maxs(int... values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }
        return max;
    }

    public static double maxs(double... values) {
        double max = values[0];
        for (int i = 1; i < values.length; i++) {
            max = Math.max(max, values[i]);
        }
        return max;
    }
}
