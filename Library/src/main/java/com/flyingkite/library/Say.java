package com.flyingkite.library;

import android.util.Log;

import java.util.Locale;

public class Say {
    private static final String TAG = "Hi";

    //-- Logging
    public static void Log(String s) {
        Log.e(TAG, s);
    }

    public static void Log(String format, Object... args) {
        Log(String.format(format, args));
    }

    public static void LogW(String s) {
        Log.w(TAG, s);
    }

    public static void LogW(String format, Object... args) {
        LogW(String.format(format, args));
    }

    public static void LogI(String s) {
        Log.i(TAG, s);
    }

    public static void LogI(String format, Object... args) {
        LogI(String.format(format, args));
    }
    //-- Logging

    public static void sleep(long ms) {
        Log("zz... " + ms + " ms");
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Log("!!! Interrupted " + e);
        } finally {
            Log("awake ^_^");
        }
    }

    public static void sleepI(long ms) {
        LogI("zz... " + ms + " ms");
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            LogI("!!! Interrupted " + e);
        } finally {
            LogI("awake ^_^");
        }
    }

    // Formatting
    public static String MMSSFFF(long ms) {
        if (ms < 0) return "-" + MMSSFFF(-ms);

        final long f = ms % 1000;
        final long s = ms / 1000;
        final long sec = s % 60;
        final long min = s / 60;
        return String.format(Locale.US, "%02d:%02d.%03d", min, sec, f);
    }

    public static String ox(boolean b) {
         return b ? "o" : "x";
    }
}
