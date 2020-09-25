package flyingkite.functional;

@FunctionalInterface
public interface Provider<T> {
    T provide();
}
