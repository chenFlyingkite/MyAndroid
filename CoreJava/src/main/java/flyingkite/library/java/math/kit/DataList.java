package flyingkite.library.java.math.kit;

import java.util.List;

import flyingkite.library.java.functional.FX;

@FunctionalInterface
public interface DataList<T> extends FX<List<T>, Void> {
    List<T> getData();

    @Override
    default List<T> get(Void unused) {
        return getData();
    }
}
