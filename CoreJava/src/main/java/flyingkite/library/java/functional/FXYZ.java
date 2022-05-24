package flyingkite.library.java.functional;

/**
 * Function of W = F(x, y, z)
 */
@FunctionalInterface
public interface FXYZ<W, X, Y, Z> {
    W get(X x, Y y, Z z);
}
