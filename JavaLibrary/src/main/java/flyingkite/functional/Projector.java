package flyingkite.functional;

@FunctionalInterface
public interface Projector<S, T> {
    T get(S source);
}
