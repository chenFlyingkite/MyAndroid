package flyingkite.library.java.log;

import java.util.Locale;

import flyingkite.library.java.functional.LogSS;

public interface Loggable extends Formattable {
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
}
