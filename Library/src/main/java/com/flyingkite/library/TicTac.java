package com.flyingkite.library;

import android.util.Log;

import java.util.Stack;

/**
 * The simple class for performance profiling.
 * It uses a static Stack to hold all the time stamps where {@link #tic()} is called.
 * For tracking performance between multiple classes or other purposes
 * (Like tracking performance in different {@link android.os.AsyncTask})
 * , consider to use {@link TicTac2} to correctly profiling.
 *
 * <p>The naming idea from TicTac is from Matlab's keywords : tic tac</p>
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
public class TicTac {
    private static final String TAG = "TicTac";
    // A handy tic-tac to track the performance
    private static final Stack<Long> tictac = new Stack<>();

    public static boolean showLog = true;

    public static void reset() {
        tictac.clear();
    }

    public static void tic() {
        tictac.push(System.currentTimeMillis());
    }

    public static void tac(String format, Object... params) {
        tac(String.format(format, params));
    }

    public static void tac(String msg) {
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
        log(s.toString());
    }

    protected static void logError(long tac, String msg) {
        if (showLog) {
            Log.e(TAG, "X_X [tic = N/A, tac = " + tac + "] : " + msg);
        }
    }

    protected static void log(String s) {
        if (showLog) {
            Log.e(TAG, s);
        }
    }
}
