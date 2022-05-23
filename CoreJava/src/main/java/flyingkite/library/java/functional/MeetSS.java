package flyingkite.library.java.functional;

@FunctionalInterface
public interface MeetSS<T, R> {
    R meet(T a, T b);
}
