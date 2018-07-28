package flyingkite.tool;

public class StringUtil {

    /**
     * Convert milliseconds to mm:ss.SSS format
     * E.g. MMSSFFF(123456 ms)
     *    = MMSSFFF(123.456 sec)
     *    = MMSSFFF(2 min 3.5 sec)
     *    = "02:03.5"
     */
    public static String MMSSFFF(long ms) {
        if (ms < 0) return "-" + MMSSFFF(-ms);

        final long f = ms % 1000;
        final long s = ms / 1000;
        final long sec = s % 60;
        final long min = s / 60;
        return String.format(java.util.Locale.US, "%02d:%02d.%03d", min, sec, f);
    }
}
