package flyingkite.math.kit;

import java.util.ArrayList;
import java.util.List;

public interface Selector<T> extends DataList<T> {
    default boolean meet(T t) {
        return true;
    }

    default List<T> select() {
        List<T> ans = new ArrayList<>();
        List<T> data = getData();

        if (data != null) {
            for (T t : data) {
                if (meet(t)) {
                    ans.add(t);
                }
            }
        }
        return ans;
    }
}
