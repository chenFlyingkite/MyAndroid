package flyingkite.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private ThreadUtil() {}
    public static final ExecutorService cachedPool = Executors.newCachedThreadPool();

    public static void f() {
        System.out.println("f");
    }
}
