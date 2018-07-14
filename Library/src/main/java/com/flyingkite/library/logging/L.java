package com.flyingkite.library.logging;

import android.util.Log;

public class L implements Loggable {
    private static String TAG = "L";

    public static void logV(String format, Object... param) {
        Log.v(TAG, String.format(java.util.Locale.US, format, param));
    }
}
