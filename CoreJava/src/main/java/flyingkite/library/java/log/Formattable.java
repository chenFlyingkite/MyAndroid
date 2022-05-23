package flyingkite.library.java.log;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public interface Formattable {
    default String _fmt(String format, Object... param) {
        return String.format(java.util.Locale.US, format, param);
    }
    /**
     * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     */
    default String _fmtTime(String fmt, Date d) {
        SimpleDateFormat s = new SimpleDateFormat(fmt, Locale.US);
        return s.format(d);
    }

    /**
     * https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     */
    default String _fmtTime(String fmt, long t) {
        return _fmtTime(fmt, new Date(t));
    }
}
