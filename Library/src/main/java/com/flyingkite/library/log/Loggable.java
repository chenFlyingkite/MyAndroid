package com.flyingkite.library.log;

import android.util.Log;

public interface Loggable extends flyingkite.log.Loggable {
    /**
     * @return Tag for Logcat's TAG
     */
    default String LTag() {
        Class<? extends Loggable> c = getClass();
        if (c.isAnonymousClass()) {
            String s = c.getName();
            int dot = s.lastIndexOf(".");
            if (dot > 0) {
                return s.substring(dot + 1);
            }
            return s; // c.getName();
        } else {
            return c.getSimpleName();
        }
    }

    /**
     * Implementation of log a message, like
     * {@link System#out System.out.println()}, {@link Log#v(String, String)}
     */
    default void log(String message) {
        Log.v(LTag(), message);
    }

    /**
     * Implementation of logging formatted message with parameters, like
     * {@link System#out System.out.printf()}
     */
    default void log(String format, Object... param) {
        log(_fmt(format, param));
    }

    //-- Log.i
    default void logI(String message) {
        Log.i(LTag(), message);
    }

    default void logI(String format, Object... param) {
        logI(_fmt(format, param));
    }
    //--

    //-- Log.w
    default void logW(String message) {
        Log.w(LTag(), message);
    }

    default void logW(String format, Object... param) {
        logW(_fmt(format, param));
    }
    //--

    //-- Log.e
    default void logE(String message) {
        Log.e(LTag(), message);
    }

    default void logE(String format, Object... param) {
        logE(_fmt(format, param));
    }
    //--
}
