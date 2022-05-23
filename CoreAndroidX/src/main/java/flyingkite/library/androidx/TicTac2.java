package flyingkite.library.androidx;

import android.util.Log;

import flyingkite.library.java.log.Loggable;

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
 *
 * Default implementation of log is {@link Log#e(String, String)}
 * @see flyingkite.library.java.tool.TicTac2
 */
public class TicTac2 extends flyingkite.library.java.tool.TicTac2 implements Loggable {
    protected String TAG = "TicTac2";

    public void setTag(String tag) {
        TAG = tag;
    }

    @Override
    public void log(String message) {
        Log.e(TAG, message);
    }

    protected void logError(long tac, String msg) {
        if (log) {
            log(errorString(tac, msg));
        }
    }

    protected String errorString(long tac, String msg) {
        return "X_X [tic = N/A, tac = " + getTime(tac) + "] : " + msg;
    }

    protected void logTac(String s) {
        if (log) {
            log(s);
        }
    }

    @Override
    public String toString() {
        return TAG + " : " + super.toString();
    }

    /**
     * {@link TicTac2} apply Log with {@link Log#v(String, String)}
     */
    public static class v extends TicTac2 {
        @Override
        public void log(String message) {
            Log.v(TAG, message);
        }
    }

    /**
     * {@link TicTac2} apply Log with {@link Log#d(String, String)}
     */
    public static class d extends TicTac2 {
        @Override
        public void log(String message) {
            Log.d(TAG, message);
        }
    }

    /**
     * {@link TicTac2} apply Log with {@link Log#i(String, String)}
     */
    public static class i extends TicTac2 {
        @Override
        public void log(String message) {
            Log.i(TAG, message);
        }
    }

    /**
     * {@link TicTac2} apply Log with {@link Log#w(String, String)}
     */
    public static class w extends TicTac2 {
        @Override
        public void log(String message) {
            Log.w(TAG, message);
        }
    }
}
