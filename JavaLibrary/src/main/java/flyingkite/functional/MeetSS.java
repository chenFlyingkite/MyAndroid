package flyingkite.functional;

@FunctionalInterface
public interface MeetSS<T, R> {
    R meet(T a, T b);
}
