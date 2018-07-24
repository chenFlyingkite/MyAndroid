package com.flyingkite.library.logging;

import android.support.annotation.NonNull;
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

    default void printLog(@NonNull LogSS ss, String tag, String message) {
        ss.run(tag, message);
    }

    default void printfLog(@NonNull LogSS ss, String tag, String format, Object... param) {
        printLog(ss, tag, _fmt(format, param));
    }

    default String _fmt(String format, Object... param) {
        return String.format(java.util.Locale.US, format, param);
    }
}
