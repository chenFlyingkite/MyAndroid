package flyingkite.library.java.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flyingkite.library.java.functional.FX;

// Collections of S to select the items, and each items return T
public class ProjectionUtil {

    /**
     * Returns each item's projection in pool by sending condition = null
     * {@link #select(Collection, FX, FX)}
     */
    public static <T, S> List<T> select(Collection<S> pool, FX<T, S> returns) {
        return select(pool, returns, null);
    }

    /**
     * From each item /S/ in pool, if /S/ satisfies /condition/, we return /T/ from /S/
     * Usually used for select target item from collection with condition
     *
     * @param condition null if pass everything
     */
    public static <T, S> List<T> select(Collection<S> pool, FX<T, S> returns, FX<Boolean, S> condition) {
        if (pool == null || returns == null) return null;

        List<T> li = new ArrayList<>();
        for (S s : pool) {
            if (condition == null || condition.get(s)) {
                li.add(returns.get(s));
            }
        }
        return li;
    }
}
