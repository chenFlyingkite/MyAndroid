package com.flyingkite.library;

import android.util.Log;

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
    private static final String TAG = "TicTac2";
    // A handy tic-tac to track the performance
    private final Stack<Long> tictac = new Stack<>();

    protected boolean showLog = true;

    public void tic() {
        tictac.push(System.currentTimeMillis());
    }

    public void tac(String format, Object... params) {
        tac(String.format(format, params));
    }

    public void tac(String msg) {
        long tac = System.currentTimeMillis();
        if (tictac.size() < 1) {
            logError(tac, msg);
            return;
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
    }

    public void showLog(boolean show) {
        showLog = show;
    }

    public void reset() {
        tictac.clear();
    }

    protected void logError(long tac, String msg) {
        if (showLog) {
            Log.e(TAG, "X_X [tic = N/A, tac = " + tac + "] : " + msg);
        }
    }

    protected void logTac(String s) {
        if (showLog) {
            Log.e(TAG, s);
        }
    }

    @Override
    public String toString() {
        return String.format("%s : tictac.size() = %s", TAG, tictac.size());
    }
}
