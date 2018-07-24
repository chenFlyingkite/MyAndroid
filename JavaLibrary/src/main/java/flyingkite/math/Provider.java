package flyingkite.math;

@FunctionalInterface
public interface Provider<T> {
    T provide();
}
