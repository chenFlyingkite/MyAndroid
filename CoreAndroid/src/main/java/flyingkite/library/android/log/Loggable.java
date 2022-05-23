package flyingkite.library.android.log;

import android.text.TextUtils;
import android.util.Log;

public interface Loggable extends flyingkite.library.java.log.Loggable {
    /**
     * @return Tag for Logcat's TAG
     */
    default String LTag() {
        // Since anonymous class returns "" in getClass().getSimpleName(),
        // so we use name getClass().getName().last
        Class<? extends Loggable> c = getClass();
        String t = "";
        if (c.isAnonymousClass()) {
            String s = c.getName();
            int dot = s.lastIndexOf(".");
            if (dot > 0) {
                t = s.substring(dot + 1);
            } else {
                t = s; // c.getName();
            }
        } else {
            t = c.getSimpleName();
        }

        // Use nonempty tag
        if (TextUtils.isEmpty(t)) {
            return "Loggable";
        } else {
            return t;
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

    /**
     * Implementation of logging formatted message with parameters, like
     * {@link System#out System.out.printf()}
     */
    default void log(Throwable t, String format, Object... param) {
        Log.v(LTag(), _fmt(format, param), t);
    }

    //-- Log.d
    default void logD(String format, Object... param) {
        logD(_fmt(format, param));
    }

    default void logD(String message) {
        Log.d(LTag(), message);
    }

    default void logD(Throwable t, String format, Object... param) {
        Log.d(LTag(), _fmt(format, param), t);
    }
    //--

    //-- Log.i
    default void logI(String format, Object... param) {
        logI(_fmt(format, param));
    }

    default void logI(String message) {
        Log.i(LTag(), message);
    }

    default void logI(Throwable t, String format, Object... param) {
        Log.i(LTag(), _fmt(format, param), t);
    }
    //--

    //-- Log.w
    default void logW(String format, Object... param) {
        logW(_fmt(format, param));
    }

    default void logW(String message) {
        Log.w(LTag(), message);
    }

    default void logW(Throwable t, String format, Object... param) {
        Log.w(LTag(), _fmt(format, param), t);
    }
    //--

    //-- Log.e
    default void logE(String format, Object... param) {
        logE(_fmt(format, param));
    }

    default void logE(String message) {
        Log.e(LTag(), message);
    }

    default void logE(Throwable t, String format, Object... param) {
        Log.e(LTag(), _fmt(format, param), t);
    }
    //--
}
