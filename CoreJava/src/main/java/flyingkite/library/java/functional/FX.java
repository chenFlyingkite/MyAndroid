package flyingkite.library.java.functional;

/**
 * Function of Y = F(x), return type, and parameters
 */
@FunctionalInterface
public interface FX<Y, X> {
    Y get(X x);
}
