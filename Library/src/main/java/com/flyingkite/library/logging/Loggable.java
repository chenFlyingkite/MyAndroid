package com.flyingkite.library.logging;

import android.util.Log;

public interface Loggable {
    /**
     * @return Tag for Logcat's TAG
     */
    default String LTag() {
        return getClass().getSimpleName();
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
        log(String.format(java.util.Locale.US, format, param));
    }
}
