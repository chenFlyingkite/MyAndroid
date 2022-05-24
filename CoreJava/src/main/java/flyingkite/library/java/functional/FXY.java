package flyingkite.library.java.functional;

/**
 * Function of Z = F(x, y), return type, and parameters
 */
@FunctionalInterface
public interface FXY<Z, X, Y> {
    Z get(X x, Y y);
}
