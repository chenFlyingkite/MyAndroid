package flyingkite.collection;

import java.util.Arrays;

public class ArraysF {
    public static <T> int len(T[] a) {
        return a == null ? 0 : a.length;
    }

    public static <T> T[] copy(T[] a) {
        if (a == null) {
            return null;
        } else {
            return Arrays.copyOf(a, len(a));
        }
    }
}
