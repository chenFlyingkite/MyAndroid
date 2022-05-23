package flyingkite.library.java.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import flyingkite.library.java.collection.ListUtil;
import flyingkite.library.java.log.Loggable;

public class ArrayUtil {

    /**
     * Add keys into source at head if not exists
     * result is used for {@link List#toArray(Object[])}
     * E.g.
     * Let z = addAtHeadIfMissing(s, k, z)
     *         z       |        s       |     k
     * ----------------+----------------+--------------
     *   [_id, _data]  |    (null)      | [_id, _data]
     *   [_id, _data]  |  [     _data]  | [_id, _data]
     *   [_id, _data]  |  [_id, _data]  | [     _data]
     */
    public static <T> T[] addAtHeadIfMissing(T[] source, T[] keys, T[] result) {
        List<T> p = ListUtil.nonNull(source);
        List<T> q = new ArrayList<>();
        if (keys != null) {
            for (T s : keys) {
                if (!p.contains(s)) {
                    q.add(s);
                }
            }
        }
        q.addAll(p);
        result = q.toArray(result);
        return result;
    }

    /**
     * Array join of a + b
     * @return a + b
     */
    public static <T> T[] join(T[] a, T[] b, T[] c) {
        List<T> al = ListUtil.nonNull(a);
        List<T> bl = ListUtil.nonNull(b);
        al.addAll(bl);
        return al.toArray(c);
    }

    public static <T> T[] copyOf(T[] original){
        if (original == null) {
            return null;
        } else {
            return Arrays.copyOf(original, original.length);
        }
    }

    private static <T> String str(T[] a) {
        return Arrays.toString(a);
    }

    public static <T> void test(T[] a, T[] b, T[] c) {
        T[] d;
        z.log("For a = %s, b = %s, c = %s", str(a), str(b), str(c));
        d = addAtHeadIfMissing(a, b, c);
        z.log("addAtHeadIfMissing = %s", str(d));

        d = join(a, b, c);
        z.log("join = %s", str(d));
    }

    private static Loggable z = new Loggable() {
        @Override
        public void log(String msg) {
            System.out.println(msg);
        }
    };
}
