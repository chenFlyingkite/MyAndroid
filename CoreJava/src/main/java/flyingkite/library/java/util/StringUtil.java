package flyingkite.library.java.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import flyingkite.library.java.functional.FXY;

public class StringUtil {
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static char ox(boolean o) {
        return o ? 'o' : 'x';
    }

    public static int containsAt(String key, String[] data) {
        return containsAt(key, Arrays.asList(data));
    }

    public static int containsAt(String key, List<String> data) {
        for (int i = 0; i < data.size(); i++) {
            if (key.contains(data.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String contains(String key, Collection<String> data) {
        for (String s : data) {
            if (s.contains(key)) {//contains.meet(s, key)
                return s;
            }
        }
        return null;
    }

    public static boolean endsOf(String src, String... target) {
        if (src != null) {
            String s = src.toLowerCase();
            for (int i = 0; i < target.length; i++) {
                if (s.endsWith(target[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final FXY<Boolean, String, String> contains = String::contains;
    public static final FXY<Boolean, String, String> equals = String::equals;

    public static String join(List<? extends CharSequence> list, CharSequence prefix, CharSequence delim, CharSequence suffix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                CharSequence s = list.get(i);
                if (i > 0) {
                    sb.append(delim);
                }
                sb.append(s);
            }
        }
        sb.append(suffix);
        return sb.toString();
    }

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

    public static long parseLong(String s) {
        return parseLong(s, 0);
    }

    public static long parseLong(String s, long exceptionValue) {
        long ans = exceptionValue;
        try {
            ans = Long.parseLong(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static int parseInt(String s, int exceptionValue) {
        int ans = exceptionValue;
        try {
            ans = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static float parseFloat(String s) {
        return parseFloat(s, 0);
    }

    public static float parseFloat(String s, float exceptionValue) {
        float ans = exceptionValue;
        try {
            ans = Float.parseFloat(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return ans;
    }
}
