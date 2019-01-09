package flyingkite.math.kit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Counter<T> extends DataList<T> {

    default String key(T t) {
        return "";
    }

    default Map<String, Integer> count() {
        List<T> data = getData();
        Map<String, Integer> ans = new HashMap<>();

        if (data != null) {
            for (T t : data) {
                String k = key(t);
                //ans.merge(k, 1, (a, b) -> {return a + b;});
                Integer v = ans.get(k);
                if (v == null) {
                    ans.put(k, 1);
                } else {
                    ans.put(k, v + 1);
                }
            }
        }
        return ans;
    }
}