package flyingkite.library.java.math.kit;

import java.util.List;

import flyingkite.library.java.functional.Provider;

@FunctionalInterface
public interface DataList<T> extends Provider<List<T>> {
    List<T> getData();

    @Override
    default List<T> provide() {
        return getData();
    }
}
