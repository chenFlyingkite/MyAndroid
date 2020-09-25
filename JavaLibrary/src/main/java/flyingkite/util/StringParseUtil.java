package flyingkite.util;

public interface StringParseUtil {

    default int parseInt(String s) {
        return parseInt(s, 0);
    }

    default int parseInt(String s, int error) {
        int n = error;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return n;
    }

    default long parseLong(String s) {
        return parseLong(s, 0);
    }

    default long parseLong(String s, int error) {
        long n = error;
        try {
            n = Long.parseLong(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return n;
    }

    default boolean isOneOf(String key, String... param) {
        if (param == null) {
            return key == null;
        } else {
            for (String s : param) {
                if (equal(s, key)) {
                    return true;
                }
            }
            return false;
        }
    }

    default boolean equal(String a, String b) {
        if (a == null && b == null) {
            return true;
        } else {
            // One of non null
            if (a != null) {
                return a.equals(b);
            } else {
                return false;
            }
        }
    }

    // null < "" < "a" = "A" < "b"
    default int stringCompare(String s1, String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (s2 == null) {
                return 1;
            } else {
                return s1.compareToIgnoreCase(s2);
            }
        }
    }
}
