package com.flyingkite.library;

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

    private static TicTac2 tictac = new TicTac2();

    static {
        tictac.setTag(TAG);
    }

    public static long tic() {
        return tictac.tic();
    }

    public static long tacL() {
        return tictac.tacL();
    }

    public static long tac(String msg) {
        return tictac.tac(msg);
    }

    public static long tac(String format, Object... param) {
        return tictac.tac(format, param);
    }

    public static void showLog(boolean show) {
        tictac.showLog = show;
    }

    public static void reset() {
        tictac.reset();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
