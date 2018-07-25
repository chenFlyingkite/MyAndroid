package flyingkite.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

    //-------------------------------------------------------------------------
    /**
     * The thread pool for classes that have many simple tasks to be run
     */
    public static final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    /**
     * @return {@link #newFlexThreadPool(int, long)} with alive time = 60 sec
     */
    public static ExecutorService newFlexThreadPool(int atMost) {
        return newFlexThreadPool(atMost, 60);
    }
    /**
     * Creates thread pool similar with {@link Executors#newFixedThreadPool(int)}, with atMost & alive time provided
     */
    public static ExecutorService newFlexThreadPool(int atMost, long aliveSecond) {
        return new ThreadPoolExecutor(0, atMost, aliveSecond, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }
    //-------------------------------------------------------------------------
}
