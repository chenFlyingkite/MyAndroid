package flyingkite.library.java.functional;

@FunctionalInterface
public interface LogSST {
    void run(String tag, String msg, Throwable tr);
}
