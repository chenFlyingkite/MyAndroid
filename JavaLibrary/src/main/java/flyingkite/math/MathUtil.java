package flyingkite.math;

public class MathUtil {
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
    // For mins & maxs uses Collections.min() & Collections.max()
}
