package flyingkite.library.java.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtil {

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> nonNull(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    public static <T> List<T> nonNull(T[] array) {
        List<T> a = new ArrayList<>();
        if (array != null) {
            Collections.addAll(a, array);
        }
        return a;
    }

    public static <T> T itemOf(List<T> list, int index) {
        if (list == null || index < 0 || list.size() <= index) {
            return null;
        } else {
            return list.get(index);
        }
    }

    public static <T> int indexOf(T[] list, T item) {
        if (list != null) {
            // Just as it done in ArrayList#indexOf(Object)
            for (int i = 0; i < list.length; i++) {
                T li = list[i];
                if (item == null) {
                    if (li == null) {
                        return i;
                    }
                } else {
                    if (item.equals(li)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}