package com.flyingkite.library;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

/**
 * The class performing the same intention with {@link TicTac}.
 * Unlike {@link TicTac} provides static method and uses global time stack,
 * {@link TicTac2} provides for creating instance and use its own one time stack. <br/>
 * {@link TicTac2} is specially better usage for tracking performance in different AsyncTasks,
 * by each task create a new object and call its {@link TicTac2#tic()} and {@link TicTac2#tac(String)} in task.
 *
 * <p>Here is an example of usage:</p>
 * <pre class="prettyprint">
 * public class Main {
 *     public static void main(String[] args) {
 *         // Let's start the tic-tac
 *         TicTac.tic();
 *             f();
 *         TicTac.tac("f is done");
 *         TicTac.tic();
 *             g();
 *             TicTac.tic();
 *                 g1();
 *             TicTac.tac("g1 is done");
 *             TicTac.tic();
 *                 g2();
 *             TicTac.tac("g2 is done");
 *         TicTac.tac("g + g1 + g2 is done");
 *         // Now is ended
 *     }
 *
 *     private void f() {
 *         // your method body
 *     }
 *     private void g() {
 *          // your method body
 *     }
 *     private void g1() {
 *          // your method body
 *     }
 *     private void g2() {
 *          // your method body
 *     }
 * }
 * </pre>
 */
public class TicTac2 {
    // https://en.wikipedia.org/wiki/ISO_8601
    private static final SimpleDateFormat formatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private static String TAG = "TicTac2";
    // A handy tic-tac to track the performance
    private final Stack<Long> tictac = new Stack<>();

    protected boolean showLog = true;

    /**
     * Push time of tic
     * @return tic Time of tic
     */
    public long tic() {
        long tic = System.currentTimeMillis();
        tictac.push(tic);
        return tic;
    }

    /**
     * Print formatted
     * @see #tac(String)
     */
    public long tac(String format, Object... params) {
        return tac(String.format(format, params));
    }

    /**
     * Evaluate time diff, Print logs and return the tac time
     * @return time diff = tac - tic, -1 if no tic
     */
    public long tac(String msg) {
        long tac = System.currentTimeMillis();
        if (tictac.size() < 1) {
            logError(tac, msg);
            return -1;
        }

        long tic = tictac.pop();

        StringBuilder s = new StringBuilder();
        // Reveal the tic's depth by adding space " "
        int n = tictac.size();
        for (int i = 0; i < n; i++) {
            s.append(" ");
        }
        // Our message
        s.append("[").append(tac - tic).append("] : ").append(msg);
        logTac(s.toString());
        return tac;
    }

    /**
     * Evaluate time diff and return the tac time
     * @return time diff = tac - tic, -1 if no tic
     */
    public long tacL() {
        long tac = System.currentTimeMillis();
        if (tictac.size() < 1) {
            return -1;
        }

        long tic = tictac.pop();
        return tac - tic;
    }

    /**
     * Set log or not
     * @see #logTac(String)
     * @see #logError(long, String)
     */
    public void showLog(boolean show) {
        showLog = show;
    }

    /**
     * Clear all the pushed tics
     */
    public void reset() {
        tictac.clear();
    }

    /**
     * Print log when {@link #tac(String)} is called with no tic
     */
    protected void logError(long tac, String msg) {
        if (showLog) {
            Log.e(TAG, errorString(tac, msg));
        }
    }

    protected String errorString(long tac, String msg) {
        return "X_X [tic = N/A, tac = " + getTime(tac) + "] : " + msg;
    }

    protected String getTime(long time) {
        return formatISO8601.format(new Date(time));
    }

    /**
     * Print log when {@link #tac(String)} is called with valid tic
     */
    protected void logTac(String s) {
        if (showLog) {
            Log.e(TAG, s);
        }
    }

    public void setTag(String tag) {
        TAG = tag;
    }

    @Override
    public String toString() {
        return String.format("%s : tictac.size() = %s", TAG, tictac.size());
    }

    /**
     * Tictac2 apply Log with {@link Log#v(String, String)}
     */
    public static class v extends TicTac2 {
        @Override
        protected void logError(long tac, String msg) {
            if (showLog) {
                Log.v(TAG, errorString(tac, msg));
            }
        }

        @Override
        protected void logTac(String s) {
            if (showLog) {
                Log.v(TAG, s);
            }
        }
    }
    /**
     * Tictac2 apply Log with {@link Log#i(String, String)}
     */
    public static class i extends TicTac2 {
        @Override
        protected void logError(long tac, String msg) {
            if (showLog) {
                Log.i(TAG, errorString(tac, msg));
            }
        }

        @Override
        protected void logTac(String s) {
            if (showLog) {
                Log.i(TAG, s);
            }
        }
    }
}
