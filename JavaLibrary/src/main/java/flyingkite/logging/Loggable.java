package flyingkite.logging;

import java.util.Locale;

public interface Loggable {
    /**
     * Writing the log with message
     * @param msg The message to be logged
     */
    void log(String msg);

    /**
     * Writing the log with String format and its parameters
     *
     * @see String#format(String, Object...)
     * @see String#format(Locale, String, Object...)
     */
    default void log(String format, Object... param) {
        log(_fmt(format, param));
    }

    default void printLog(LogSS ss, String tag, String message) {
        ss.run(tag, message);
    }

    default void printfLog(LogSS ss, String tag, String format, Object... param) {
        printLog(ss, tag, _fmt(format, param));
    }

    default String _fmt(String format, Object... param) {
        return String.format(java.util.Locale.US, format, param);
    }
}
