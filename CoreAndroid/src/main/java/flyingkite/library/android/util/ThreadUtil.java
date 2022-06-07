package flyingkite.library.android.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import flyingkite.library.java.util.StringUtil;

public class ThreadUtil extends flyingkite.library.java.util.ThreadUtil {

    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    public static boolean isUIThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static void logUIThread(String s) {
        Log.e("Hi", "isUIThread = " + StringUtil.ox(isUIThread()) + ", " + s);
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
    //-------------------------------------------------------------------------

}
