package flyingkite.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtil {

    public static <T> List<T> nonNull(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    public static <T> List<T> nonNull(T[] array) {
        return array == null ? new ArrayList<>() : Arrays.asList(array);
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