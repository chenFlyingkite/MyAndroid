package com.flyingkite.library;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadUtil {
    private ThreadUtil() {}

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

    public static boolean isUIThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static void logUIThread(String s) {
        Log.e("Hi", "isUIThread = " + (isUIThread() ? "o" : "x") + ", " + s);
    }

    public static void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    public static void runOnWorkerThread(final Runnable action) {
        if (isUIThread()) {
            cachedThreadPool.submit(action);
        } else {
            action.run();
        }
    }
}
