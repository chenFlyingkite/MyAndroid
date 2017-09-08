package com.flyingkite.library;

import android.util.Log;

import java.util.Stack;

public class TicTac {
    private static final String TAG = "Hi";
    // A handy tic-tac to track the performance
    private static final Stack<Long> tictac = new Stack<>();

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

    private static void logError(long tac, String msg) {
        Log.e(TAG, "X_X [tic = N/A, tac = " + tac + "] : " + msg);
    }

    private static void log(String s) {
        Log.e(TAG, s);
    }
}
