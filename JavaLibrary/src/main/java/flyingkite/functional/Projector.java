package flyingkite.functional;

/**
 * Project object type : given S, returns T
 */
@FunctionalInterface
public interface Projector<S, T> {
    T get(S source);
}
