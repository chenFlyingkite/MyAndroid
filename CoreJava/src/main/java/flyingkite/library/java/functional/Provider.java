package flyingkite.library.java.functional;

@FunctionalInterface
public interface Provider<T> {
    T provide();
}
