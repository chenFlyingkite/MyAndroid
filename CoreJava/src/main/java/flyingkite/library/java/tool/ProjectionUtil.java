package flyingkite.library.java.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flyingkite.library.java.functional.Projector;

public class ProjectionUtil {

    public static <T, S> List<T> select(Collection<S> collection, Projector<S, T> projector) {
        return select(collection, projector, null);
    }

    /**
     * Selects target item from collection with condition
     */
    public static <T, S> List<T> select(Collection<S> collection, Projector<S, T> projector, Projector<S, Boolean> condition) {
        if (collection == null || projector == null) return null;

        List<T> li = new ArrayList<>();
        for (S s : collection) {
            if (condition == null || condition.get(s)) {
                li.add(projector.get(s));
            }
        }
        return li;
    }
}
