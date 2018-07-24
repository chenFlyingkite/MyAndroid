package flyingkite.logging;

@FunctionalInterface
public interface LogSS {
    void run(String tag, String message);
}
